package ca.magex.crm.api.store;

import java.util.Map;

import ca.magex.crm.api.authentication.CrmPasswordDetails;

/**
 * Represents a Password Store for CrmPasswordDetails
 * 
 * @author Jonny
 */
public interface CrmPasswordStore {

	public Map<String, CrmPasswordDetails> getPasswords();
	
	default public void reset() {
		getPasswords().clear();
	}
}
