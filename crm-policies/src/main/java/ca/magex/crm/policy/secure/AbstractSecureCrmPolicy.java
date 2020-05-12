package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;

public class AbstractSecureCrmPolicy {

	@Autowired CrmUserService userService;
	
	/**
	 * retrieve the current user from the spring security context
	 * @return
	 */
	protected User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		return userService.findUserByUsername(auth.getName());		
	}
	
	/**
	 * returns true if the current user has the given role
	 * @param user
	 * @param role
	 * @return
	 */
	protected boolean isCrmAdmin(User user) {
		return userService.getRoles(user.getUserId())
			.stream()
			.filter((r) -> r.toString().equals("CRM_ADMIN"))
			.findAny()
			.isPresent();
	}
	
	/**
	 * returns true if the current user has the re admin role
	 * @param user
	 * @return
	 */
	protected boolean isReAdmin(User user) {
		return userService.getRoles(user.getUserId())
			.stream()
			.filter((r) -> r.toString().equals("RE_ADMIN"))
			.findAny()
			.isPresent();
	}
}