package ca.magex.crm.spring.security.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.services.CrmUserService;

public class AbstractSpringSecurityPolicy {

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
		return user.getRoles()
				.stream()
				.filter((r) -> r.contentEquals("CRM_ADMIN"))
				.findAny()
				.isPresent();
	}
	
	/**
	 * returns true if the current user has the re admin role
	 * @param user
	 * @return
	 */
	protected boolean isReAdmin(User user) {
		return user.getRoles()
				.stream()
				.filter((r) -> r.contentEquals("RE_ADMIN"))
				.findAny()
				.isPresent();
	}
}