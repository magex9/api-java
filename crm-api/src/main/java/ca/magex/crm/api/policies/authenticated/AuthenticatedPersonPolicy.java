package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;
import static ca.magex.crm.api.services.CrmAuthenticationService.ORG_ADMIN;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.basic.BasicPersonPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedPersonPolicy implements CrmPersonPolicy {

	private CrmAuthenticationService auth;

	private CrmPersonPolicy delegate;

	private CrmPersonService persons;

	/**
	 * Authenticated Person Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param organizations
	 * @param persons
	 * @param userService
	 */
	public AuthenticatedPersonPolicy(
			CrmAuthenticationService auth,
			CrmOrganizationService organizations,
			CrmPersonService persons) {
		this.auth = auth;
		this.persons = persons;
		this.delegate = new BasicPersonPolicy(organizations, persons);
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		if (!delegate.canCreatePersonForOrganization(organizationId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* if the current user is associated to the organization, then return true if RE Admin */
		if (auth.getOrganizationId().equals(organizationId)) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		if (!delegate.canViewPerson(personId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to */
		return auth.getOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId());
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		if (!delegate.canUpdatePerson(personId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* can always update yourself */
		if (auth.getPersonId().equals(personId)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (auth.getOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		if (!delegate.canEnablePerson(personId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (auth.getOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user is not associated with the organization */
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		if (!delegate.canDisablePerson(personId)) {
			return false;
		}
		/* cannot disable yourself!! */
		if (auth.getPersonId().equals(personId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure the current user is associated to the organization this person belongs to and they are an RE Admin */
		if (auth.getOrganizationId().equals(persons.findPersonSummary(personId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user is not associated with the organization */
		return false;
	}
}