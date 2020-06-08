package ca.magex.crm.api.services;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

public interface CrmAuthenticationService {
	
	public static final String SYS_ADMIN = "SYS_ADMIN";

	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String CRM_USER = "CRM_USER";

	public static final String ORG_ADMIN = "ORG_ADMIN";

	public static final String ORG_USER = "ORG_USER";

	boolean isAuthenticated();
	
	User getCurrentUser();
	
	boolean isUserInRole(String role);
	
	Identifier getUserId();
	
	Identifier getPersonId();
	
	Identifier getOrganizationId();
	
}
