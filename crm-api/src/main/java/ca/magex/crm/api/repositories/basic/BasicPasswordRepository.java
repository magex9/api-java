package ca.magex.crm.api.repositories.basic;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.store.CrmPasswordStore;

/**
 * Basic Password Repository implementation backed by the CrmPasswordStore
 * 
 * @author Jonny
 */
public class BasicPasswordRepository implements CrmPasswordRepository {

	
	
	private CrmPasswordStore passwordStore;
	
	public BasicPasswordRepository(CrmPasswordStore passwordStore) {
		this.passwordStore = passwordStore;
	}
	
	@Override
	public String generateTemporaryPassword() {
		return CrmPasswordStore.generatePassword();
	}

	@Override
	public CrmPasswordDetails savePasswordDetails(CrmPasswordDetails passwordDetails) {
		passwordStore.getPasswords().put(passwordDetails.getUsername(), SerializationUtils.clone(passwordDetails));
		return passwordDetails;
	}

	@Override
	public CrmPasswordDetails findPasswordDetails(String username) {
		return passwordStore.getPasswords().get(username);
	}
}
