package ca.magex.crm.api.authentication.basic;

import java.util.Stack;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

public class BasicAuthenticationService implements CrmAuthenticationService {

	private CrmUserService users;
	
	private CrmPasswordService passwords;
	
	private Stack<User> currentUser;

	public BasicAuthenticationService(Crm crm, CrmPasswordRepository repo) {
		this(crm, new BasicPasswordService(repo));
	}
	
	public BasicAuthenticationService(CrmUserService users, CrmPasswordService passwords) {
		this.users = users;
		this.passwords = passwords;
		this.currentUser = new Stack<>();
	}
	
	public CrmPasswordService getPasswords() {
		return passwords;
	}
	
	@Override
	public boolean login(String username, String password) {
		if (!passwords.verifyPassword(username, password))
			throw new IllegalArgumentException("Invalid username or password");
		currentUser.push(users.findUserByUsername(username));
		return true;
	}
	
	@Override
	public boolean logout() {
		currentUser.pop();
		return true;
	}
	
	@Override
	public boolean isAuthenticated() {
		return !currentUser.isEmpty();
	}

	@Override
	public User getAuthenticatedUser() {
		return currentUser.peek();
	}

	@Override
	public boolean isUserInRole(String role) {
		if (!isAuthenticated())
			return false;
		return currentUser.peek().getRoles().contains(role);
	}

	@Override
	public Identifier getAuthenticatedUserId() {
		if (!isAuthenticated())
			return null;
		return currentUser.peek().getUserId();
	}

	@Override
	public Identifier getAuthenticatedPersonId() {
		if (!isAuthenticated())
			return null;
		return currentUser.peek().getPerson().getPersonId();
	}

	@Override
	public Identifier getAuthenticatedOrganizationId() {
		if (!isAuthenticated())
			return null;
		return currentUser.peek().getPerson().getOrganizationId();
	}

}
