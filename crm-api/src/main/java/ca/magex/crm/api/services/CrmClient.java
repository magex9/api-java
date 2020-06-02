package ca.magex.crm.api.services;

import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.validation.CrmValidation;

public interface CrmClient extends CrmInitializationService, CrmServices, CrmPolicies {
	
	boolean login(String username, String password);
	
	boolean logout();

}
