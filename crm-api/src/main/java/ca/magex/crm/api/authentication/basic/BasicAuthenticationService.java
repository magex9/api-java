package ca.magex.crm.api.authentication.basic;

import java.util.Stack;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class BasicAuthenticationService implements CrmAuthenticationService {

	private CrmOptionService options;
	
	private CrmUserService users;
	
	private CrmPasswordService passwords;
	
	private CrmPersonService persons;
	
	private Stack<UserDetails> currentUser;

	public BasicAuthenticationService(CrmServices crm, CrmUserRepository userRepo, CrmPasswordRepository passwordRepo) {
		this(crm, crm, crm, new BasicPasswordService(userRepo, passwordRepo));
	}
	
	public BasicAuthenticationService(CrmOptionService options, CrmUserService users, CrmPersonService persons, CrmPasswordService passwords) {
		this.options = options;
		this.users = users;
		this.persons = persons;
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
		currentUser.push(users.findUserDetailsByUsername(username));
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
	public UserDetails getAuthenticatedUser() {
		return currentUser.peek();
	}

	@Override
	public boolean isUserInRole(String role) {
		if (!isAuthenticated())
			return false;
		Option authenticationOption = options.findOptionByCode(Type.AUTHENTICATION_ROLE, role);
		return currentUser.peek().getAuthenticationRoleIds().contains(authenticationOption.getOptionId());
	}

	@Override
	public UserIdentifier getAuthenticatedUserId() {
		if (!isAuthenticated())
			return null;
		return currentUser.peek().getUserId();
	}

	@Override
	public PersonIdentifier getAuthenticatedPersonId() {
		if (!isAuthenticated())
			return null;
		return currentUser.peek().getPersonId();
	}

	@Override
	public OrganizationIdentifier getAuthenticatedOrganizationId() {
		if (!isAuthenticated())
			return null;
		return persons.findPersonSummary(currentUser.peek().getPersonId()).getOrganizationId();
	}
}