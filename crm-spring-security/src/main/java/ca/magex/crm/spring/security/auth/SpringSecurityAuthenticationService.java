package ca.magex.crm.spring.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmUserService;

@Component
@Profile(MagexCrmProfiles.CRM_AUTH)
public class SpringSecurityAuthenticationService implements CrmAuthenticationService {

	@Autowired CrmUserService userService;

	@Override
	public boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication() != null;
	}

	@Override
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		return userService.findUserByUsername(auth.getName());
	}

	@Override
	public boolean isUserInRole(String role) {
		return getCurrentUser().getRoles().contains(role);
	}
}
