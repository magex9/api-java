package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.OrganizationPolicy;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public class OrganizationPolicyAmnesiaImpl implements OrganizationPolicy {

	public static final Role CRM_ADMIN = new Role(1, "Customer Relationship Management Admin");
	
	public static final Role RE_ADMIN = new Role(2, "Reporting Entity Admin");
	
	public static final Role RE_DEO = new Role(3, "Data Entry Officer");
	
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
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		return false;
	}

	public boolean canViewOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(organizationId))
			return true;
		return false;
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getUser().getRoles().contains(RE_ADMIN))
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
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getUser().getRoles().contains(RE_ADMIN))
			return true;
		return false;
	}

	public boolean canViewLocation(Identifier locationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(service.findLocation(locationId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdateLocation(Identifier locationId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(service.findLocation(locationId).getOrganizationId()))
			return false;
		if (auth.getUser().getRoles().contains(RE_ADMIN))
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
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(service.findPerson(personId).getOrganizationId()))
			return true;
		return false;
	}

	public boolean canUpdatePerson(Identifier personId) {
		if (auth == null)
			return false;
		if (auth.getUser().getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(service.findPerson(personId).getOrganizationId()))
			return false;
		if (auth.getUser().getRoles().contains(RE_ADMIN))
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
