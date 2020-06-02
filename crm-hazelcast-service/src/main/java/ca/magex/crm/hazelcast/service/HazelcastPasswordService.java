package ca.magex.crm.hazelcast.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.validation.CrmValidation;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPasswordService implements CrmPasswordService {

	public static String HZ_PASSWORDS_KEY = "passwords";

	@Autowired private HazelcastInstance hzInstance;
	@Autowired private PasswordEncoder passwordEncoder;

	@Autowired @Lazy private CrmValidation validationService; // needs to be lazy because it depends on other services

	private long expiration = TimeUnit.DAYS.toMillis(365);

	@Override
	public String getEncodedPassword(String username) {
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.getCipherText();
	}

	@Override
	public boolean isExpiredPassword(String username) {
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return isExpired(passwordDetails.getExpiration());
	}

	@Override
	public boolean isTempPassword(String username) {
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordDetails.isTemporary();
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
		PasswordDetails passwordDetails = passwords.get(username);
		if (passwordDetails == null) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return passwordEncoder.matches(rawPassword, passwordDetails.getCipherText());
	}

	@Override
	public String generateTemporaryPassword(String username) {
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
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
		Map<String, PasswordDetails> passwords = hzInstance.getMap(HZ_PASSWORDS_KEY);
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