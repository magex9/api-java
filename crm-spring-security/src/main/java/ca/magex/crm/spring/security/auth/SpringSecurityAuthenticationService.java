package ca.magex.crm.spring.security.auth;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

@Component
public class SpringSecurityAuthenticationService implements CrmAuthenticationService {

	@Autowired private Crm userService;

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
		return getAuthenticatedUser().isInRole(new AuthenticationRoleIdentifier(role));
	}

	@Override
	public UserIdentifier getAuthenticatedUserId() {
		return getAuthenticatedUser().getUserId();
	}

	@Override
	public PersonIdentifier getAuthenticatedPersonId() {
		return getAuthenticatedUser().getPersonId();
	}

	@Override
	public OrganizationIdentifier getAuthenticatedOrganizationId() {
		return getAuthenticatedUser().getOrganizationId();
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
