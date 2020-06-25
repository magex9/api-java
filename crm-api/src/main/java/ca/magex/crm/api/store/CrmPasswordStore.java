package ca.magex.crm.api.store;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.authentication.CrmPasswordDetails;

/**
 * Represents a Password Store for CrmPasswordDetails
 * 
 * @author Jonny
 */
public interface CrmPasswordStore {

	/* remove vowels so there's no chance the random password could be a curse word */
	public static final String CHARS = "bcdfghjkmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ";
	
	public static String generatePassword() {
		return RandomStringUtils.random(10, CHARS);
	}
	
	public Map<String, CrmPasswordDetails> getPasswords();
	
	default public void reset() {
		getPasswords().clear();
	}
}
