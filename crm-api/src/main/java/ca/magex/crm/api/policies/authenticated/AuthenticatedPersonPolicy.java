package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.basic.BasicPersonPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedPersonPolicy extends BaseAuthenticatedPolicy implements CrmPersonPolicy {

	private CrmPersonService personService;	
	private CrmPersonPolicy basicPolicy;
	
	public AuthenticatedPersonPolicy(
			CrmAuthenticationService authenticationService,
			CrmPersonService personService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.personService = personService;
		this.basicPolicy = new BasicPersonPolicy();
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canCreatePersonForOrganization(organizationId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser) && basicPolicy.canCreatePersonForOrganization(organizationId);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canViewPerson(personId);
		}
		/* ensure this person belongs to the same organization as the current user */
		PersonDetails person = personService.findPersonDetails(personId);
		return currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId()) && basicPolicy.canViewPerson(personId);
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canUpdatePerson(personId);
		}
		/* can always update yourself */
		if (currentUser.getPerson().getPersonId().equals(personId)) {
			return basicPolicy.canUpdatePerson(personId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canUpdatePerson(personId);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canEnablePerson(personId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canEnablePerson(personId);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canDisablePerson(personId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canDisablePerson(personId);
		}
		/* this person is not part of the users organization */
		return false;
	}
}