package ca.magex.crm.amnesia.services;

import java.util.List;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.services.*;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public class OrganizationPolicyAmnesiaImpl implements OrganizationPolicy {

	public static final Role CRM_ADMIN = new Role(1, "Customer Relationship Management Admin");
	
	public static final Role RE_ADMIN = new Role(2, "Reporting Entity Admin");
	
	public static final Role RE_DEO = new Role(3, "Data Entry Officer");
	
	private Person auth;
	
	private OrganizationService service;

	public OrganizationPolicyAmnesiaImpl(OrganizationService service) {
		this.service = service;
	}
	
	public void login(Person auth) {
		this.auth = auth;
	}
	
	public void logout() {
		this.auth = null;
	}
	
	public boolean canCreateOrganization() {
		if (auth == null)
			return false;
		if (auth.getRoles().contains(CRM_ADMIN))
			return true;
		return false;
	}

	public boolean canViewOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(organizationId))
			return true;
		return false;
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		if (auth == null)
			return false;
		if (auth.getRoles().contains(CRM_ADMIN))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getRoles().contains(RE_ADMIN))
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
		if (auth.getRoles().contains(CRM_ADMIN))
			return true;
		if (!auth.getOrganizationId().equals(organizationId))
			return false;
		if (auth.getRoles().contains(RE_ADMIN))
			return true;
		return false;
	}

	public boolean canViewLocation(Identifier locationId) {
		if (auth == null)
			return false;
		if (auth.getRoles().contains(CRM_ADMIN))
			return true;
		if (auth.getOrganizationId().equals(service.getLocation(locationId).getOrganizationId());
			return true;
		return false;
	}

	public boolean canUpdateLocation(Identifier locationId) {
		return false;
	}

	public boolean canEnableLocation(Identifier locationId) {
		return false;
	}

	public boolean canDisableLocation(Identifier locationId) {
		return false;
	}

	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		return false;
	}

	public boolean canViewPerson(Identifier personId) {
		return false;
	}

	public boolean canUpdatePerson(Identifier personId) {
		return false;
	}

	public boolean canEnablePerson(Identifier personId) {
		return false;
	}

	public boolean canDisablePerson(Identifier personId) {
		return false;
	}

	public boolean canUpdateUserRole(Identifier personId) {
		return false;
	}

	public List<Message> validate(Organization organization) {
		return null;
	}

	public List<Message> validate(Location location) {
		return null;
	}

	public List<Message> validate(Person person) {
		return null;
	}

	public List<Message> validate(List<Role> roles) {
		return null;
	}

}
