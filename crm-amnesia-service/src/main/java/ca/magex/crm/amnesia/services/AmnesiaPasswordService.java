package ca.magex.crm.amnesia.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;

public class AmnesiaPasswordService implements CrmPasswordService {

	private PasswordEncoder passwordEncoder;
	
	private Map<String, PasswordDetails> passwords;
	
	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	public AmnesiaPasswordService() {
		this.passwords = new HashMap<String, PasswordDetails>();
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	@Override
	public String getEncodedPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isTempPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean isExpiredPassword(@NotNull String username) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getExpiration() != null && passwordDetails.getExpiration().before(new Date());
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordEncoder.matches(rawPassword, passwordDetails.getCipherText());
	}

	@Override
	public String generateTemporaryPassword(@NotNull String username) {
		String tempPassword = RandomStringUtils.random(10);
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails != null) {
			passwords.put(
					username, 
					passwordDetails.withTemporaryPassword(
							passwordEncoder.encode(tempPassword), 
							new Date(System.currentTimeMillis() + expiration)));
		}
		else {
			passwords.put(
					username, 
					new PasswordDetails(
							passwordEncoder.encode(tempPassword), 
							true, 
							new Date(System.currentTimeMillis() + expiration)));
		}
		return tempPassword;
	}

	@Override
	public void updatePassword(@NotNull String username, @NotNull String encodedPassword) {
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		passwords.put(username, passwordDetails.withPassword(encodedPassword));
	}

}
