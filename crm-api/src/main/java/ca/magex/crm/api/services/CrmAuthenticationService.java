package ca.magex.crm.api.services;

import ca.magex.crm.api.roles.User;

public interface CrmAuthenticationService {

	boolean isAuthenticated();
	
	User getCurrentUser();
	
	boolean isUserInRole(String role);
	
}
