package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.policy.DefaultCrmPersonPolicy;

@Component
@Primary
@Profile(value = {
		MagexCrmProfiles.CRM_AUTH_EMBEDDED,
		MagexCrmProfiles.CRM_AUTH_REMOTE
})
public class SecureCrmPersonPolicy extends AbstractSecureCrmPolicy implements CrmPersonPolicy {

	@Autowired private CrmPersonService personService;
	
	private DefaultCrmPersonPolicy defaultPolicy = new DefaultCrmPersonPolicy();

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return defaultPolicy.canCreatePersonForOrganization(organizationId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		if (currentUser.getPerson().getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser) && defaultPolicy.canCreatePersonForOrganization(organizationId);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return defaultPolicy.canViewPerson(personId);
		}
		/* ensure this person belongs to the same organization as the current user */
		PersonDetails person = personService.findPersonDetails(personId);
		return currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId()) && defaultPolicy.canViewPerson(personId);
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* can always update yourself */
		if (currentUser.getPerson().getPersonId().equals(personId)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return defaultPolicy.canEnablePerson(personId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser) && defaultPolicy.canEnablePerson(personId);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* this person is not part of the users organization */
		return false;
	}
}