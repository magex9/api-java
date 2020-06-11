package ca.magex.crm.api.services;

import ca.magex.crm.api.policies.CrmPolicies;

public interface CrmClient extends CrmInitializationService, CrmServices, CrmPolicies {
	
	boolean login(String username, String password);
	
	boolean logout();

}
