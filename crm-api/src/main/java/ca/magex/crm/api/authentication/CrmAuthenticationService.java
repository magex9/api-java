package ca.magex.crm.api.authentication;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmAuthenticationService {
	
	public static final String SYS_ADMIN = "SYS_ADMIN";

	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String CRM_USER = "CRM_USER";

	public static final String ORG_ADMIN = "ORG_ADMIN";

	public static final String ORG_USER = "ORG_USER";
	
	public boolean login(String username, String password);
	
	public boolean logout();

	boolean isAuthenticated();
	
	User getAuthenticatedUser();
	
	UserIdentifier getAuthenticatedUserId();
	
	PersonIdentifier getAuthenticatedPersonId();
	
	OrganizationIdentifier getAuthenticatedOrganizationId();
	
	boolean isUserInRole(String role);
	
}
