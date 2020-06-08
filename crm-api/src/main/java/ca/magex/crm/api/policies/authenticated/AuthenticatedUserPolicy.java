package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;
import static ca.magex.crm.api.services.CrmAuthenticationService.ORG_ADMIN;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.policies.basic.BasicUserPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
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
	public boolean canCreateUserForPerson(Identifier personId) {
		if (!delegate.canCreateUserForPerson(personId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated with the organization the person belongs to */
		if (auth.getOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* current user not associated to the organization of the person */
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		if (!delegate.canViewUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		return auth.getOrganizationId().equals(users.findUser(userId).getPerson().getOrganizationId());
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		if (!delegate.canUpdateUserRole(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (auth.getOrganizationId().equals(users.findUser(userId).getPerson().getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		if (!delegate.canUpdateUserPassword(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* current user can update their own password */
		if (auth.getUserId().equals(userId)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (auth.getOrganizationId().equals(users.findUser(userId).getPerson().getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		if (!delegate.canEnableUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (auth.getOrganizationId().equals(users.findUser(userId).getPerson().getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		if (!delegate.canDisableUser(userId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (auth.getOrganizationId().equals(users.findUser(userId).getPerson().getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}
}
