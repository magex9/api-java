package ca.magex.crm.api.authentication.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.store.CrmStore;

public class BasicPasswordService implements CrmPasswordService {

	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	private Map<String, BasicPasswordDetails> passwords;
	
	private PasswordEncoder passwordEncoder;
	
	public BasicPasswordService() {
		this(new BasicPasswordEncoder());
	}
	
	public BasicPasswordService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
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
		String tempPassword = RandomStringUtils.random(10, CrmStore.BASE_58).toString();
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
