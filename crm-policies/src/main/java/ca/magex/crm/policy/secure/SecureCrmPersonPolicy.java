package ca.magex.crm.policy.secure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;

@Component
@Profile(MagexCrmProfiles.CRM_AUTH)
public class SecureCrmPersonPolicy extends AbstractSecureCrmPolicy implements CrmPersonPolicy {

	@Autowired private CrmPersonService personService;

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		if (currentUser.getOrganizationId().equals(organizationId)) {
			return isReAdmin(currentUser);
		}
		/* this person is not part of the users organization */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure this person belongs to the same organization as the current user */
		PersonDetails person = personService.findPersonDetails(personId);
		return currentUser.getOrganizationId().equals(person.getOrganizationId());
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* can always update yourself */
		if (currentUser.getPersonId().equals(personId)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getOrganizationId().equals(person.getOrganizationId())) {
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
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (currentUser.getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
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
		if (currentUser.getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* this person is not part of the users organization */
		return false;
	}
}