package ca.magex.crm.hazelcast.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(CrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastPasswordService implements CrmPasswordService {

	public static String HZ_PASSWORDS_KEY = "passwords";

	private XATransactionAwareHazelcastInstance hzInstance;
	private PasswordEncoder passwordEncoder;

	private long expiration = TimeUnit.DAYS.toMillis(365);

	public HazelcastPasswordService(
			XATransactionAwareHazelcastInstance hzInstance,
			PasswordEncoder passwordEncoder) {
		this.hzInstance = hzInstance;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String getEncodedPassword(String username) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isExpiredPassword(String username) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return isExpired(passwordDetails.getExpiration());
	}

	@Override
	public boolean isTempPassword(String username) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordEncoder.matches(rawPassword, passwordDetails.getCipherText());
	}

	@Override
	public String generateTemporaryPassword(String username) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		String tempPassword = RandomStringUtils.random(10, "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ");
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails != null) {
			passwords.put(
					username,
					passwordDetails.withTemporaryPassword(
							passwordEncoder.encode(tempPassword),
							new Date(System.currentTimeMillis() + expiration)));
		} else {
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
	public void updatePassword(String username, String encodedPassword) {
		TransactionalMap<String, PasswordDetails> passwords = hzInstance.getPasswordsMap();
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		passwords.put(username, passwordDetails.withPassword(encodedPassword));
	}

	/**
	 * ensures the expiration date is in the future
	 * @param expirationDate
	 * @return
	 */
	private boolean isExpired(Date expirationDate) {
		return expirationDate != null && expirationDate.before(new Date());
	}
}