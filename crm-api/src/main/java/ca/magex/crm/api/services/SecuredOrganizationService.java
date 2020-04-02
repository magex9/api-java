package ca.magex.crm.api.services;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public final class SecuredOrganizationService implements OrganizationService, OrganizationPolicy {

	private final OrganizationService delegate;
	
	private final OrganizationPolicy policy;
	
	public SecuredOrganizationService(OrganizationService delegate, OrganizationPolicy policy) {
		this.delegate = delegate;
		this.policy = policy;
	}
	
	@Override
	public List<Status> findStatuses() {
		return delegate.findStatuses();
	}
	
	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return delegate.findStatusByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return delegate.findStatusByLocalizedName(locale, name);
	}
	
	@Override
	public List<Role> findRoles() {
		return delegate.findRoles();
	}
	
	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return delegate.findRoleByCode(code);
	}
	
	@Override
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return delegate.findRoleByLocalizedName(locale, name);
	}
	
	@Override
	public List<Country> findCountries() {
		return delegate.findCountries();
	}
	
	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return delegate.findCountryByCode(code);
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return delegate.findCountryByLocalizedName(locale, name);
	}
	
	@Override
	public List<Salutation> findSalutations() {
		return delegate.findSalutations();
	}
	
	@Override
	public Salutation findSalutationByCode(Integer code) throws ItemNotFoundException {
		return delegate.findSalutationByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return delegate.findSalutationByLocalizedName(locale, name);
	}	
	
	public OrganizationDetails createOrganization(String organizationName) {
		if (!canCreateOrganization())
			throw new PermissionDeniedException("createOrganization");
		return delegate.createOrganization(organizationName);
	}

	public OrganizationDetails updateOrganizationName(Identifier organizationId, String name) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationName: " + organizationId);
		return delegate.updateOrganizationName(organizationId, name);
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateMainLocation: " + organizationId);
		return delegate.updateOrganizationMainLocation(organizationId, locationId);
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		if (!canEnableOrganization(organizationId))
			throw new PermissionDeniedException("enableOrganization: " + organizationId);
		return delegate.enableOrganization(organizationId);
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		if (!canDisableOrganization(organizationId))
			throw new PermissionDeniedException("disableOrganization: " + organizationId);
		return delegate.disableOrganization(organizationId);
	}
	
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return delegate.findOrganizationDetails(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return delegate.findOrganizationDetails(organizationId);
	}
	
	public long countOrganizations(OrganizationsFilter filter) {
		return delegate.countOrganizations(filter);
	}
	
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return delegate.findOrganizationDetails(filter, paging);
	}

	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return delegate.findOrganizationSummaries(filter, paging);
	}

	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference,
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return delegate.createLocation(organizationId, locationName, locationReference, address);
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationName: " + locationId);
		return delegate.updateLocationName(locationId, locationName);
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationAddress: " + locationId);
		return delegate.updateLocationAddress(locationId, address);
	}

	public LocationSummary enableLocation(Identifier locationId) {
		if (!canEnableLocation(locationId))
			throw new PermissionDeniedException("enableLocation: " + locationId);
		return delegate.enableLocation(locationId);
	}

	public LocationSummary disableLocation(Identifier locationId) {
		if (!canDisableLocation(locationId))
			throw new PermissionDeniedException("disableLocation: " + locationId);
		return delegate.disableLocation(locationId);
	}

	public LocationSummary findLocationSummary(Identifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return delegate.findLocationSummary(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return delegate.findLocationDetails(locationId);
	}
	
	public long countLocations(LocationsFilter filter) {
		return delegate.countLocations(filter);
	}
	
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return delegate.findLocationDetails(filter, paging);
	}
	
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return delegate.findLocationSummaries(filter, paging);
	}

	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition unit) {
		if (!canCreatePersonForOrganization(organizationId))
			throw new PermissionDeniedException("createPerson: " + organizationId);
		return delegate.createPerson(organizationId, name, address, communication, unit);
	}

	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonName: " + personId);
		return delegate.updatePersonName(personId, name);
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonAddress: " + personId);
		return delegate.updatePersonAddress(personId, address);
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonCommunication: " + personId);
		return delegate.updatePersonCommunication(personId, communication);
	}
	
	public PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition unit) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonBusinessUnit: " + personId);
		return delegate.updatePersonBusinessUnit(personId, unit);
	}

	public PersonSummary enablePerson(Identifier personId) {
		if (!canEnablePerson(personId))
			throw new PermissionDeniedException("enablePerson: " + personId);
		return delegate.enablePerson(personId);
	}

	public PersonSummary disablePerson(Identifier personId) {
		if (!canDisablePerson(personId))
			throw new PermissionDeniedException("disablePerson: " + personId);
		return delegate.disablePerson(personId);
	}

	public PersonSummary findPersonSummary(Identifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return delegate.findPersonSummary(personId);
	}
	
	public PersonDetails findPersonDetails(Identifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return delegate.findPersonDetails(personId);
	}
	
	public long countPersons(PersonsFilter filter) {
		return delegate.countPersons(filter);
	}
	
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return delegate.findPersonDetails(filter, paging);
	}
	
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return delegate.findPersonSummaries(filter, paging);
	}

	public PersonDetails addUserRole(Identifier personId, Role role) {
		if (!canUpdateUserRole(personId))
			throw new PermissionDeniedException("addUserRole: " + personId);
		return delegate.addUserRole(personId, role);
	}

	public PersonDetails removeUserRole(Identifier personId, Role role) {
		if (!canUpdateUserRole(personId))
			throw new PermissionDeniedException("removeUserRole: " + personId);
		return delegate.removeUserRole(personId, role);
	}

	public OrganizationDetails validate(OrganizationDetails organization) {
		return delegate.validate(organization);
	}

	public LocationDetails validate(LocationDetails location) {
		return delegate.validate(location);
	}

	public PersonDetails validate(PersonDetails person) {
		return delegate.validate(person);
	}

	public List<Role> validate(List<Role> roles, Identifier personId) {
		return delegate.validate(roles, personId);
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

}
