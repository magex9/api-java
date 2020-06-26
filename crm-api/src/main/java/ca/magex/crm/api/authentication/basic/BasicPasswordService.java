package ca.magex.crm.api.authentication.basic;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.repositories.CrmPasswordRepository;

public class BasicPasswordService implements CrmPasswordService {

	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	private CrmPasswordRepository passwordRepository;
	
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Creates a new Basic Password Service using the Basic Password Encoder
	 * 
	 * @param passwordRepository
	 */
	public BasicPasswordService(CrmPasswordRepository passwordRepository) {
		this(passwordRepository, new BasicPasswordEncoder());
	}
	
	/**
	 * Creates a new Basic Password Service
	 * 
	 * @param passwordRepository
	 * @param passwordEncoder
	 */
	public BasicPasswordService(CrmPasswordRepository passwordRepository, PasswordEncoder passwordEncoder) {
		this.passwordRepository = passwordRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public String getEncodedPassword(String username) {
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isTempPassword(String username) {
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean isExpiredPassword(String username) {
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getExpiration() != null && passwordDetails.getExpiration().before(new Date());
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordEncoder.matches(rawPassword, passwordDetails.getCipherText());
	}

	@Override
	public String generateTemporaryPassword(String username) {
		String tempPassword = passwordRepository.generateTemporaryPassword();
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails != null) {
			passwordRepository.savePasswordDetails(
					passwordDetails.withTemporaryPassword(
							encodePassword(tempPassword), 
							new Date(System.currentTimeMillis() + expiration)));			
		}
		else {
			passwordRepository.savePasswordDetails(
				new CrmPasswordDetails(
					username,
					encodePassword(tempPassword), 
					true, 
					new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90))));
		}
		return tempPassword;
	}

	@Override
	public void updatePassword(String username, String encodedPassword) {
		CrmPasswordDetails passwordDetails = passwordRepository.findPasswordDetails(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		passwordRepository.savePasswordDetails(passwordDetails.withPassword(encodedPassword));
	}

	@Override
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}
}