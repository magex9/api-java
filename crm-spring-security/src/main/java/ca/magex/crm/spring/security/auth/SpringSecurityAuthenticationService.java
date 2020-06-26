package ca.magex.crm.spring.security.auth;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityAuthenticationService implements CrmAuthenticationService {

	private CrmUserService userService;
	
	public SpringSecurityAuthenticationService(CrmUserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken;
	}

	@Override
	public User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return userService.findUserByUsername(auth.getName());
	}

	@Override
	public boolean isUserInRole(String role) {
		return getAuthenticatedUser().getRoles().contains(role);
	}

	@Override
	public Identifier getAuthenticatedUserId() {
		return getAuthenticatedUser().getUserId();
	}

	@Override
	public Identifier getAuthenticatedPersonId() {
		return getAuthenticatedUser().getPerson().getPersonId();
	}

	@Override
	public Identifier getAuthenticatedOrganizationId() {
		return getAuthenticatedUser().getPerson().getOrganizationId();
	}

	@Override
	public boolean login(String username, String password) {
		throw new NotImplementedException("unimplemented");
	}

	@Override
	public boolean logout() {
		throw new NotImplementedException("unimplemented");
	}
}
