package ca.magex.crm.spring.security.auth;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

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
	public UserDetails getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		return userService.findUserByUsername(auth.getName());
	}

	@Override
	public boolean isUserInRole(String role) {
		UserDetails authUser = getAuthenticatedUser();
		if (authUser == null) {
			return false;
		}
		return authUser.isInRole(new AuthenticationRoleIdentifier(role));
	}

	@Override
	public UserIdentifier getAuthenticatedUserId() {
		UserDetails authUser = getAuthenticatedUser();
		if (authUser == null) {
			return null;
		}
		return authUser.getUserId();
	}

	@Override
	public PersonIdentifier getAuthenticatedPersonId() {
		UserDetails authUser = getAuthenticatedUser();
		if (authUser == null) {
			return null;
		}
		return authUser.getPersonId();
	}

	@Override
	public OrganizationIdentifier getAuthenticatedOrganizationId() {
		UserDetails authUser = getAuthenticatedUser();
		if (authUser == null) {
			return null;
		}
		return authUser.getOrganizationId();
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
