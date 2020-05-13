package ca.magex.crm.api.services;

import ca.magex.crm.api.policies.CrmPolicies;

public interface Crm extends CrmInitializationService, CrmServices, CrmPolicies, CrmValidation {
	
	public static final long SERIAL_UID_VERSION = 1l;

	public static final long PREVIOUS_API_VERSION = 1;
	
	public static final String PREVIOUS_API_PREFIX = "/v" + PREVIOUS_API_VERSION;
	
	public static final long CURRENT_API_VERSION = 1;
	
	public static final String CURRENT_API_PREFIX = "/v" + PREVIOUS_API_VERSION;
	
}
