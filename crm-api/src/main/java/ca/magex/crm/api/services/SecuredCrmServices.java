package ca.magex.crm.api.services;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
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
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

public final class SecuredCrmServices implements Crm {

	private final CrmLookupService lookupService;
	
	private final CrmValidation validationService;
	
	private final CrmOrganizationService organizationService;
	
	private final CrmOrganizationPolicy organizationPolicy;
	
	private final CrmLocationService locationService;
	
	private final CrmLocationPolicy locationPolicy;
	
	private final CrmPersonService personService;
	
	private final CrmPersonPolicy personPolicy;
	
	private final CrmUserService userService;
	
	private final CrmUserPolicy userPolicy;
	
	public SecuredCrmServices(CrmLookupService lookupService, 
			CrmOrganizationService organizationService, CrmOrganizationPolicy organizationPolicy,
			CrmLocationService locationService, CrmLocationPolicy locationPolicy, 
			CrmPersonService personService, CrmPersonPolicy personPolicy,
			CrmUserService userService, CrmUserPolicy userPolicy) {
		super();
		this.validationService = new StructureValidationService(lookupService, organizationService, locationService);
		this.lookupService = lookupService;
		this.organizationService = organizationService;
		this.organizationPolicy = organizationPolicy;
		this.locationService = locationService;
		this.locationPolicy = locationPolicy;
		this.personService = personService;
		this.personPolicy = personPolicy;
		this.userService = userService;
		this.userPolicy = userPolicy;
	}
	
	@Override
	public List<Status> findStatuses() {
		return lookupService.findStatuses();
	}
	
	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return lookupService.findStatusByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findStatusByLocalizedName(locale, name);
	}
	
	@Override
	public List<Role> findRoles() {
		return lookupService.findRoles();
	}
	
	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return lookupService.findRoleByCode(code);
	}
	
	@Override
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findRoleByLocalizedName(locale, name);
	}
	
	@Override
	public List<Country> findCountries() {
		return lookupService.findCountries();
	}
	
	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return lookupService.findCountryByCode(code);
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findCountryByLocalizedName(locale, name);
	}
	
	@Override
	public List<Salutation> findSalutations() {
		return lookupService.findSalutations();
	}
	
	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return lookupService.findSalutationByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findSalutationByLocalizedName(locale, name);
	}	
	
	@Override
	public List<Language> findLanguages() {
		return lookupService.findLanguages();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return lookupService.findLanguageByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findLanguageByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return lookupService.findBusinessSectors();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return lookupService.findBusinessSectorByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findBusinessSectorByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return lookupService.findBusinessUnits();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return lookupService.findBusinessUnitByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return lookupService.findBusinessUnitByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return lookupService.findBusinessClassifications();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return lookupService.findBusinessClassificationByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return lookupService.findBusinessClassificationByLocalizedName(locale, name);
	}
	
	public OrganizationDetails createOrganization(String organizationDisplayName) {
		if (!canCreateOrganization())
			throw new PermissionDeniedException("createOrganization");
		return organizationService.createOrganization(organizationDisplayName);
	}

	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationDisplayName: " + organizationId);
		return organizationService.updateOrganizationDisplayName(organizationId, name);
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateMainLocation: " + organizationId);
		return organizationService.updateOrganizationMainLocation(organizationId, locationId);
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		if (!canEnableOrganization(organizationId))
			throw new PermissionDeniedException("enableOrganization: " + organizationId);
		return organizationService.enableOrganization(organizationId);
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		if (!canDisableOrganization(organizationId))
			throw new PermissionDeniedException("disableOrganization: " + organizationId);
		return organizationService.disableOrganization(organizationId);
	}
	
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return organizationService.findOrganizationDetails(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return organizationService.findOrganizationDetails(organizationId);
	}
	
	public long countOrganizations(OrganizationsFilter filter) {
		return organizationService.countOrganizations(filter);
	}
	
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return organizationService.findOrganizationDetails(filter, paging);
	}

	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return organizationService.findOrganizationSummaries(filter, paging);
	}

	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference,
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return locationService.createLocation(organizationId, displayName, reference, address);
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationName: " + locationId);
		return locationService.updateLocationName(locationId, locationName);
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationAddress: " + locationId);
		return locationService.updateLocationAddress(locationId, address);
	}

	public LocationSummary enableLocation(Identifier locationId) {
		if (!canEnableLocation(locationId))
			throw new PermissionDeniedException("enableLocation: " + locationId);
		return locationService.enableLocation(locationId);
	}

	public LocationSummary disableLocation(Identifier locationId) {
		if (!canDisableLocation(locationId))
			throw new PermissionDeniedException("disableLocation: " + locationId);
		return locationService.disableLocation(locationId);
	}

	public LocationSummary findLocationSummary(Identifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return locationService.findLocationSummary(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return locationService.findLocationDetails(locationId);
	}
	
	public long countLocations(LocationsFilter filter) {
		return locationService.countLocations(filter);
	}
	
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return locationService.findLocationDetails(filter, paging);
	}
	
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return locationService.findLocationSummaries(filter, paging);
	}

	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		if (!canCreatePersonForOrganization(organizationId))
			throw new PermissionDeniedException("createPerson: " + organizationId);
		return personService.createPerson(organizationId, name, address, communication, position);
	}

	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonName: " + personId);
		return personService.updatePersonName(personId, name);
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonAddress: " + personId);
		return personService.updatePersonAddress(personId, address);
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonCommunication: " + personId);
		return personService.updatePersonCommunication(personId, communication);
	}
	
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonBusinessPosition: " + personId);
		return personService.updatePersonBusinessPosition(personId, position);
	}

	public PersonSummary enablePerson(Identifier personId) {
		if (!canEnablePerson(personId))
			throw new PermissionDeniedException("enablePerson: " + personId);
		return personService.enablePerson(personId);
	}

	public PersonSummary disablePerson(Identifier personId) {
		if (!canDisablePerson(personId))
			throw new PermissionDeniedException("disablePerson: " + personId);
		return personService.disablePerson(personId);
	}

	public PersonSummary findPersonSummary(Identifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return personService.findPersonSummary(personId);
	}
	
	public PersonDetails findPersonDetails(Identifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return personService.findPersonDetails(personId);
	}
	
	public long countPersons(PersonsFilter filter) {
		return personService.countPersons(filter);
	}
	
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		// TODO filter results of the find based on the policy
		return personService.findPersonDetails(filter, paging);
	}
	
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return personService.findPersonSummaries(filter, paging);
	}
	
	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		if (!canCreateUserForPerson(personId))
			throw new PermissionDeniedException("createUser: " + personId);
		return userService.createUser(personId, username, roles);
	}
	
	@Override
	public User findUserById(Identifier userId) {
		if (!canViewUser(userId))
			throw new PermissionDeniedException("findUserById: " + userId);
		return userService.findUserById(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {		
		User user = userService.findUserByUsername(username);
		if (!canViewUser(user.getUserId()))
			throw new PermissionDeniedException("findUserByUsername: " + username);
		return user;
	}
	
	@Override
	public User setUserRoles(Identifier userId, List<String> roles) {
		if (!canUpdateUserRole(userId))
			throw new PermissionDeniedException("setUserRoles: " + userId);
		return userService.setUserRoles(userId, roles);
	}

	@Override
	public User addUserRole(Identifier userId, String role) {
		if (!canUpdateUserRole(userId))
			throw new PermissionDeniedException("addUserRole: " + userId);
		return userService.addUserRole(userId, role);
	}
	
	@Override
	public User setUserPassword(Identifier userId, String password) {
		if (!canUpdateUserPassword(userId))
			throw new PermissionDeniedException("setUserPassword: " + userId);
		return userService.setUserPassword(userId, password);
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		if (!canUpdateUserRole(userId))
			throw new PermissionDeniedException("removeUserRole: " + userId);
		return userService.removeUserRole(userId, role);
	}

	public OrganizationDetails validate(OrganizationDetails organization) {
		return validationService.validate(organization);
	}

	public LocationDetails validate(LocationDetails location) {
		return validationService.validate(location);
	}

	public PersonDetails validate(PersonDetails person) {
		return validationService.validate(person);
	}

	public List<String> validate(List<String> roles, Identifier personId) {
		return validationService.validate(roles, personId);
	}

	public boolean canCreateOrganization() {
		return organizationPolicy.canCreateOrganization();
	}

	public boolean canViewOrganization(Identifier organizationId) {
		return organizationPolicy.canViewOrganization(organizationId);
	}

	public boolean canUpdateOrganization(Identifier organizationId) {
		return organizationPolicy.canUpdateOrganization(organizationId);
	}

	public boolean canEnableOrganization(Identifier organizationId) {
		return organizationPolicy.canEnableOrganization(organizationId);
	}

	public boolean canDisableOrganization(Identifier organizationId) {
		return organizationPolicy.canDisableOrganization(organizationId);
	}

	public boolean canCreateLocationForOrganization(Identifier locationId) {
		return locationPolicy.canCreateLocationForOrganization(locationId);
	}

	public boolean canViewLocation(Identifier locationId) {
		return locationPolicy.canViewLocation(locationId);
	}

	public boolean canUpdateLocation(Identifier locationId) {
		return locationPolicy.canUpdateLocation(locationId);
	}

	public boolean canEnableLocation(Identifier locationId) {
		return locationPolicy.canEnableLocation(locationId);
	}

	public boolean canDisableLocation(Identifier locationId) {
		return locationPolicy.canDisableLocation(locationId);
	}

	public boolean canCreatePersonForOrganization(Identifier personId) {
		return personPolicy.canCreatePersonForOrganization(personId);
	}

	public boolean canViewPerson(Identifier personId) {
		return personPolicy.canViewPerson(personId);
	}

	public boolean canUpdatePerson(Identifier personId) {
		return personPolicy.canUpdatePerson(personId);
	}

	public boolean canEnablePerson(Identifier personId) {
		return personPolicy.canEnablePerson(personId);
	}

	public boolean canDisablePerson(Identifier personId) {
		return personPolicy.canDisablePerson(personId);
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		return userPolicy.canCreateUserForPerson(personId);
	}
	
	@Override
	public boolean canViewUser(Identifier userId) {
		return userPolicy.canViewUser(userId);
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		return userPolicy.canUpdateUserRole(userId);
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		return userPolicy.canUpdateUserPassword(userId);
	}
}
