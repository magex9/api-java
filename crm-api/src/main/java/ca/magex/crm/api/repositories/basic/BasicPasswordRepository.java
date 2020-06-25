package ca.magex.crm.api.repositories.basic;

import org.apache.commons.lang3.RandomStringUtils;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.store.CrmPasswordStore;

/**
 * Basic Password Repository implementation backed by the CrmPasswordStore
 * 
 * @author Jonny
 */
public class BasicPasswordRepository implements CrmPasswordRepository {

	/* remove vowels so there's no chance the random password could be a curse word */
	public static final String CHARS = "bcdfghjkmnpqrstvwxzBCDFGHJKLMNPQRSTVWXZ";
	
	private CrmPasswordStore passwordStore;
	
	public BasicPasswordRepository(CrmPasswordStore passwordStore) {
		this.passwordStore = passwordStore;
	}
	
	@Override
	public String generateTemporaryPassword() {
		return RandomStringUtils.random(10, CHARS);
	}

	@Override
	public void savePasswordDetails(CrmPasswordDetails passwordDetails) {
		passwordStore.getPasswords().put(passwordDetails.getUsername(), passwordDetails);
	}

	@Override
	public CrmPasswordDetails findPasswordDetails(String username) {
		return passwordStore.getPasswords().get(username);
	}
}
