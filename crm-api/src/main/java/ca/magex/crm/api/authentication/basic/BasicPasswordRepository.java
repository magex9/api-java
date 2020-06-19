package ca.magex.crm.api.authentication.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.authentication.CrmPasswordRepository;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.repositories.CrmStore;

public class BasicPasswordRepository implements CrmPasswordRepository {

	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	private Map<String, BasicPasswordDetails> passwords;
	
	private PasswordEncoder passwordEncoder;
	
	public BasicPasswordRepository() {
		this(new BasicPasswordEncoder());
	}
	
	public BasicPasswordRepository(PasswordEncoder passwordEncoder) {
		this.passwords = new HashMap<String, BasicPasswordDetails>();
	}
	
	@Override
	public String getEncodedPassword(String username) {
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isTempPassword(String username) {
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean isExpiredPassword(String username) {
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getExpiration() != null && passwordDetails.getExpiration().before(new Date());
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordEncoder.matches(rawPassword, passwordDetails.getCipherText());
	}

	@Override
	public String generateTemporaryPassword(String username) {
		String tempPassword = CrmStore.generateId(BasicPasswordDetails.class).toString();
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails != null) {
			passwords.put(
					username, 
					passwordDetails.withTemporaryPassword(
							encodePassword(tempPassword), 
							new Date(System.currentTimeMillis() + expiration)));
		}
		else {
			passwords.put(
					username, 
					new BasicPasswordDetails(
							encodePassword(tempPassword), 
							true, 
							new Date(System.currentTimeMillis() + expiration)));
		}
		return tempPassword;
	}

	@Override
	public void updatePassword(String username, String encodedPassword) {
		BasicPasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		passwords.put(username, passwordDetails.withPassword(encodedPassword));
	}

	@Override
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

}
