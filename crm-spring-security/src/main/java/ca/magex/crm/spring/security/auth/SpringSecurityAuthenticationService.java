package ca.magex.crm.spring.security.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Profile(MagexCrmProfiles.CRM_AUTH)
public class SpringSecurityAuthenticationService implements CrmAuthenticationService {

	private CrmUserService userService;
	
	public SpringSecurityAuthenticationService(CrmUserService userService) {
		this.userService = userService;
	}

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
