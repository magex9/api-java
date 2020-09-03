package ca.magex.crm.api;

import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmServices;

public interface CrmClient extends CrmServices, CrmPolicies {
	
	boolean login(String username, String password);
	
	boolean logout();

}
