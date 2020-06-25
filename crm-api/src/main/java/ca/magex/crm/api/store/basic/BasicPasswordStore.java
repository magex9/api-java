package ca.magex.crm.api.store.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.store.CrmPasswordStore;

public class BasicPasswordStore implements CrmPasswordStore {
	
	private Map<String, CrmPasswordDetails> getPasswords;

	public BasicPasswordStore() {
		this.getPasswords = new ConcurrentHashMap<>();
	}
	
	@Override
	public Map<String, CrmPasswordDetails> getPasswords() {
		return getPasswords;
	}

}
