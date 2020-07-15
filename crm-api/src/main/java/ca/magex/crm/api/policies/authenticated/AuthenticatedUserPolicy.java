package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.authentication.CrmAuthenticationService.CRM_ADMIN;
import static ca.magex.crm.api.authentication.CrmAuthenticationService.ORG_ADMIN;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.policies.basic.BasicUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class AuthenticatedUserPolicy implements CrmUserPolicy {

	private CrmAuthenticationService auth;

	private CrmUserPolicy delegate;

	private CrmPersonService persons;

	private CrmUserService users;

	/**
	 * Authenticated User Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param persons
	 * @param users
	 */
	public AuthenticatedUserPolicy(
			CrmAuthenticationService auth,
			CrmPersonService persons,
			CrmUserService users) {
		this.auth = auth;
		this.persons = persons;
		this.users = users;
		this.delegate = new BasicUserPolicy(persons, users);
	}

	@Override
	public boolean canCreateUserForPerson(PersonIdentifier personId) {
		if (!delegate.canCreateUserForPerson(personId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated with the organization the person belongs to */
		if (auth.getAuthenticatedOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* current user not associated to the organization of the person */
		return false;
	}
	
	@Override
	public boolean canViewUser(String username) {
		if (!delegate.canViewUser(username)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		UserDetails user = users.findUserByUsername(username);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		return auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId());
	}

	@Override
	public boolean canViewUser(UserIdentifier userId) {
		if (!delegate.canViewUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		UserDetails user = users.findUserDetails(userId);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		return auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId());
	}

	@Override
	public boolean canUpdateUserRole(UserIdentifier userId) {
		if (!delegate.canUpdateUserRole(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		UserDetails user = users.findUserDetails(userId);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		if (auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(UserIdentifier userId) {
		if (!delegate.canUpdateUserPassword(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* current user can update their own password */
		if (auth.getAuthenticatedUserId().equals(userId)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		UserDetails user = users.findUserDetails(userId);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		if (auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canEnableUser(UserIdentifier userId) {
		if (!delegate.canEnableUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		UserDetails user = users.findUserDetails(userId);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		if (auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canDisableUser(UserIdentifier userId) {
		if (!delegate.canDisableUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		UserDetails user = users.findUserDetails(userId);
		PersonSummary person = persons.findPersonSummary(user.getPersonId());
		if (auth.getAuthenticatedOrganizationId().equals(person.getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}
}
