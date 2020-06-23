package ca.magex.crm.spring.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityAuthenticationService implements CrmAuthenticationService {

	@Autowired
	@Qualifier("PrincipalUserService")
	private CrmUserService userService;

	@Override
	public boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
	}

	@Override
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return userService.findUserByUsername(auth.getName());
	}

	@Override
	public boolean isUserInRole(String role) {
		return getCurrentUser().getRoles().contains(role);
	}

	@Override
	public Identifier getUserId() {
		return getCurrentUser().getUserId();
	}

	@Override
	public Identifier getPersonId() {
		return getCurrentUser().getPerson().getPersonId();
	}

	@Override
	public Identifier getOrganizationId() {
		return getCurrentUser().getPerson().getOrganizationId();
	}
}
