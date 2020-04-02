package ca.magex.crm.amnesia.services;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.OrganizationPolicy;
import ca.magex.crm.api.system.Identifier;

public class OrganizationPolicyAmnesiaImpl implements OrganizationPolicy {

	private PersonDetails auth;
	
	private OrganizationServiceAmnesiaImpl service;

	public OrganizationPolicyAmnesiaImpl(OrganizationServiceAmnesiaImpl service) {
		this.service = service;
	}
	
	public void login(PersonDetails auth) {
		this.auth = auth;
	}
	
	public void logout() {
		this.auth = null;
	}
	
	public boolean canCreateOrganization() {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		return false;
	}

	public boolean canViewOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (auth.getOrganizationId().equals(organizationId))
			return true;
		return false;
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("RE_ADMIN")))
			return true;
		return false;
	}

	public boolean canEnableOrganization(Identifier organizationId) {
		return canCreateOrganization();
	}

	public boolean canDisableOrganization(Identifier organizationId) {
		return canCreateOrganization();
	}

	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("RE_ADMIN")))
			return true;
		return false;
	}

	public boolean canViewLocation(Identifier locationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (auth.getOrganizationId().equals(service.findLocationDetails(locationId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdateLocation(Identifier locationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (auth.getOrganizationId().equals(service.findLocationDetails(locationId).getOrganizationId()))
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("RE_ADMIN")))
			return true;
		return false;
	}

	public boolean canEnableLocation(Identifier locationId) {
		return canUpdateLocation(locationId);
	}

	public boolean canDisableLocation(Identifier locationId) {
		return canUpdateLocation(locationId);
	}

	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return canCreateLocationForOrganization(organizationId);
	}

	public boolean canViewPerson(Identifier personId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (auth.getOrganizationId().equals(service.findPersonDetails(personId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdatePerson(Identifier personId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("CRM_ADMIN")))
			return true;
		if (auth.getOrganizationId().equals(service.findPersonDetails(personId).getOrganizationId()))
			return false;
		if (auth.getUser().getRoles().contains(service.findRoleByCode("RE_ADMIN")))
			return true;
		return false;
	}

	public boolean canEnablePerson(Identifier personId) {
		return canUpdatePerson(personId);
	}

	public boolean canDisablePerson(Identifier personId) {
		return canUpdatePerson(personId);
	}

	public boolean canUpdateUserRole(Identifier personId) {
		return canUpdatePerson(personId);
	}

}
