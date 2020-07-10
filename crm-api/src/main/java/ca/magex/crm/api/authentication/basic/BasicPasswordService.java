package ca.magex.crm.api.authentication.basic;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.password.PasswordEncoder;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmUserRepository;

public class BasicPasswordService implements CrmPasswordService {

	private long expiration = TimeUnit.DAYS.toMillis(365);
	
	private CrmUserRepository userRepository;
	
	private CrmPasswordRepository passwordRepository;
	
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Creates a new Basic Password Service using the Basic Password Encoder
	 * 
	 * @param userRepository;
	 * @param passwordRepository
	 */
	public BasicPasswordService(CrmUserRepository userRepository, CrmPasswordRepository passwordRepository) {
		this(userRepository, passwordRepository, new BasicPasswordEncoder());
	}
	
	/**
	 * Creates a new Basic Password Service
	 * 
	 * @param passwordRepository
	 * @param passwordEncoder
	 */
	public BasicPasswordService(CrmUserRepository userRepository, CrmPasswordRepository passwordRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordRepository = passwordRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public User findUser(String username) {
		return userRepository.findUsers(new UsersFilter().withUsername(username), Paging.singleInstance()).getSingleItem();
	}
	
	public CrmPasswordRepository getPasswordRepository() {
		return passwordRepository;
	}
	
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
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