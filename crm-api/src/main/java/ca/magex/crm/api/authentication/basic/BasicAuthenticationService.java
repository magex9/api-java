package ca.magex.crm.api.authentication.basic;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

public class BasicAuthenticationService implements CrmAuthenticationService {

	private CrmUserService users;
	
	private CrmPasswordService passwords;
	
	private User currentUser;

	public BasicAuthenticationService(Crm crm) {
		this(crm, new BasicPasswordService());
	}
	
	public BasicAuthenticationService(CrmUserService users, CrmPasswordService passwords) {
		this.users = users;
		this.passwords = passwords;
		this.currentUser = null;
	}
	
	public CrmPasswordService getPasswords() {
		return passwords;
	}
	
	public boolean login(String username, String password) {
		if (!passwords.verifyPassword(username, password))
			throw new IllegalArgumentException("Invalid username or password");
		currentUser = users.findUserByUsername(username);
		return true;
	}
	
	public boolean logout() {
		currentUser = null;
		return true;
	}
	
	@Override
	public boolean isAuthenticated() {
		return currentUser != null;
	}

	@Override
	public User getAuthenticatedUser() {
		if (currentUser == null)
			return null;
		return currentUser;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (currentUser == null)
			return false;
		return currentUser.getRoles().contains(role);
	}

	@Override
	public Identifier getAuthenticatedUserId() {
		if (currentUser == null)
			return null;
		return currentUser.getUserId();
	}

	@Override
	public Identifier getAuthenticatedPersonId() {
		if (currentUser == null)
			return null;
		return currentUser.getPerson().getPersonId();
	}

	@Override
	public Identifier getAuthenticatedOrganizationId() {
		if (currentUser == null)
			return null;
		return currentUser.getPerson().getOrganizationId();
	}

}
