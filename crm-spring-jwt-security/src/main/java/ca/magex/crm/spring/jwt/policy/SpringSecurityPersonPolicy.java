package ca.magex.crm.spring.jwt.policy;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmPersonPolicy;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityPersonPolicy extends AbstractSpringSecurityPolicy implements CrmPersonPolicy {

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		if (personDetails.getOrganizationId().equals(organizationId)) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* can always view yourself */
		if (personDetails.getPersonId().equals(personId)) {
			return true;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		PersonDetails person = personService.findPersonDetails(personId);
		/* return true if the person belongs to the org */
		return personDetails.getOrganizationId().equals(person.getOrganizationId());
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* can always update yourself */
		if (personDetails.getPersonId().equals(personId)) {
			return true;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (personDetails.getOrganizationId().equals(person.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (personDetails.getOrganizationId().equals(person.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (personDetails.getOrganizationId().equals(person.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canUpdateUserRole(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (personDetails.getOrganizationId().equals(person.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier personId) {
		PersonDetails personDetails = getCurrentUser();
		/* can always update your own password */
		if (personDetails.getPersonId().equals(personId)) {
			return true;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.equals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		PersonDetails person = personService.findPersonDetails(personId);
		if (personDetails.getOrganizationId().equals(person.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.equals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

}
