package ca.magex.crm.api.policies.authenticated;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmUserService;

/**
 * Base Authenticated Policy with common functionality
 * 
 * @author Jonny
 */
public abstract class BaseAuthenticatedPolicy {
	
	private CrmAuthenticationService authenticationService;
	private CrmUserService userService;
	
	protected BaseAuthenticatedPolicy(CrmAuthenticationService authenticationService, CrmUserService userService) {
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	/**
	 * retrieve the current user from the spring security context
	 * @return
	 */
	protected User getCurrentUser() {
		return authenticationService.getCurrentUser();
	}
	
	/**
	 * returns true if the current user has the given role
	 * @param user
	 * @param role
	 * @return
	 */
	protected boolean isCrmAdmin(User user) {
		return userService.findUser(user.getUserId()).getRoles().contains("CRM_ADMIN");			
	}
	
	/**
	 * returns true if the current user has the re admin role
	 * @param user
	 * @return
	 */
	protected boolean isReAdmin(User user) {
		return userService.findUser(user.getUserId()).getRoles().contains("RE_ADMIN");		
	}
}