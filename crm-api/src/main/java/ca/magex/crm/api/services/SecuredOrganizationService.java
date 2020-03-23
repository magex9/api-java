package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

public final class SecuredOrganizationService implements OrganizationService, OrganizationPolicy {

	private final OrganizationService delegate;
	
	private final OrganizationPolicy policy;
	
	public SecuredOrganizationService(OrganizationService delegate, OrganizationPolicy policy) {
		this.delegate = delegate;
		this.policy = policy;
	}
	
	public Organization createOrganization(String organizationName) {
		if (!canCreateOrganization())
			throw new PermissionDeniedException("createOrganization");
		return delegate.createOrganization(organizationName);
	}

	public Organization updateOrganizationName(Identifier organizationId, String name) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationName: " + organizationId);
		return delegate.updateOrganizationName(organizationId, name);
	}

	public Organization updateMainLocation(Identifier organizationId, Identifier locationId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateMainLocation: " + organizationId);
		return delegate.updateMainLocation(organizationId, locationId);
	}

	public Organization enableOrganization(Identifier organizationId) {
		if (!canEnableOrganization(organizationId))
			throw new PermissionDeniedException("enableOrganization: " + organizationId);
		return delegate.enableOrganization(organizationId);
	}

	public Organization disableOrganization(Identifier organizationId) {
		if (!canDisableOrganization(organizationId))
			throw new PermissionDeniedException("disableOrganization: " + organizationId);
		return delegate.disableOrganization(organizationId);
	}
	
	public Organization findOrganization(Identifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return delegate.findOrganization(organizationId);
	}
	
	public Organization findOrganizations(OrganizationsFilter filter) {
		return delegate.findOrganizations(filter);
	}

	public Location createLocation(Identifier organizationId, String locationName, String locationReference,
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return delegate.createLocation(organizationId, locationName, locationReference, address);
	}

	public Location updateLocationName(Identifier locationId, String locationName) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationName: " + locationId);
		return delegate.updateLocationName(locationId, locationName);
	}

	public Location updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationAddress: " + locationId);
		return delegate.updateLocationAddress(locationId, address);
	}

	public Location enableLocation(Identifier locationId) {
		if (!canEnableLocation(locationId))
			throw new PermissionDeniedException("enableLocation: " + locationId);
		return delegate.enableLocation(locationId);
	}

	public Location disableLocation(Identifier locationId) {
		if (!canDisableLocation(locationId))
			throw new PermissionDeniedException("disableLocation: " + locationId);
		return delegate.disableLocation(locationId);
	}

	public Location findLocation(Identifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return delegate.findLocation(locationId);
	}
	
	public Location findLocations(LocationsFilter filter) {
		return delegate.findLocations(filter);
	}

	public Person createPerson(Identifier organizationId, PersonName name, MailingAddress address, String email, String jobTitle,
			Language language, Telephone homePhone, Integer faxNumber) {
		if (!canCreatePersonForOrganization(organizationId))
			throw new PermissionDeniedException("createPerson: " + organizationId);
		return delegate.createPerson(organizationId, name, address, email, jobTitle, language, homePhone, faxNumber);
	}

	public Person updatePersonName(Identifier personId, PersonName name) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonName: " + personId);
		return delegate.updatePersonName(personId, name);
	}

	public Person updatePersonAddress(Identifier personId, MailingAddress address) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonAddress: " + personId);
		return delegate.updatePersonAddress(personId, address);
	}

	public Person updatePersonCommunication(Identifier personId, String email, String jobTitle, Language language,
			Telephone homePhone, Integer faxNumber) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonCommunication: " + personId);
		return delegate.updatePersonCommunication(personId, email, jobTitle, language, homePhone, faxNumber);
	}

	public Person enablePerson(Identifier personId) {
		if (!canEnablePerson(personId))
			throw new PermissionDeniedException("enablePerson: " + personId);
		return delegate.enablePerson(personId);
	}

	public Person disablePerson(Identifier personId) {
		if (!canDisablePerson(personId))
			throw new PermissionDeniedException("disablePerson: " + personId);
		return delegate.disablePerson(personId);
	}

	public Person findPerson(Identifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return delegate.findPerson(personId);
	}
	
	public Person findPersons(PersonsFilter filter) {
		return delegate.findPersons(filter);
	}

	public Person addUserRole(Identifier personId, Role role) {
		if (!canUpdateUserRole(personId))
			throw new PermissionDeniedException("addUserRole: " + personId);
		return delegate.addUserRole(personId, role);
	}

	public Person removeUserRole(Identifier personId, Role role) {
		if (!canUpdateUserRole(personId))
			throw new PermissionDeniedException("removeUserRole: " + personId);
		return delegate.removeUserRole(personId, role);
	}

	public boolean canCreateOrganization() {
		return policy.canCreateOrganization();
	}

	public boolean canViewOrganization(Identifier organizationId) {
		return policy.canViewOrganization(organizationId);
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		return policy.canUpdateOrganization(organizationId);
	}

	public boolean canEnableOrganization(Identifier organizationId) {
		return policy.canDisableOrganization(organizationId);
	}

	public boolean canDisableOrganization(Identifier organizationId) {
		return policy.canDisableOrganization(organizationId);
	}

	public boolean canCreateLocationForOrganization(Identifier locationId) {
		return policy.canCreateLocationForOrganization(locationId);
	}

	public boolean canViewLocation(Identifier locationId) {
		return policy.canViewLocation(locationId);
	}

	public boolean canUpdateLocation(Identifier locationId) {
		return policy.canUpdateLocation(locationId);
	}

	public boolean canEnableLocation(Identifier locationId) {
		return policy.canEnableLocation(locationId);
	}

	public boolean canDisableLocation(Identifier locationId) {
		return policy.canDisableLocation(locationId);
	}

	public boolean canCreatePersonForOrganization(Identifier personId) {
		return policy.canCreatePersonForOrganization(personId);
	}

	public boolean canViewPerson(Identifier personId) {
		return policy.canViewPerson(personId);
	}

	public boolean canUpdatePerson(Identifier personId) {
		return policy.canUpdatePerson(personId);
	}

	public boolean canEnablePerson(Identifier personId) {
		return policy.canEnablePerson(personId);
	}

	public boolean canDisablePerson(Identifier personId) {
		return policy.canDisablePerson(personId);
	}

	public boolean canUpdateUserRole(Identifier personId) {
		return policy.canUpdateUserRole(personId);
	}

	public List<Message> validate(Organization organization) {
		return policy.validate(organization);
	}

	public List<Message> validate(Location location) {
		return policy.validate(location);
	}

	public List<Message> validate(Person person) {
		return policy.validate(person);
	}

	public List<Message> validate(List<Role> roles) {
		return policy.validate(roles);
	}

}
