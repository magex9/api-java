package ca.magex.crm.amnesia.services;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
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
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.CrmValidation;
import ca.magex.crm.resource.CrmLookupLoader;

public class AmnesiaServices implements CrmServices, CrmInitializationService, CrmValidation {

	private final AmnesiaDB db;
	
	public AmnesiaServices() {
		this(new AmnesiaDB(new AmnesiaPasswordEncoder(), new CrmLookupLoader()));
		db.initialize("Amnesia", new PersonName("3", "Tom", "Tim", "Tam"), "ttt@amnesia.ca", "admin", "admin");
	}
	
	public AmnesiaServices(AmnesiaDB db) {
		this.db = db;
	}
	
	public AmnesiaDB db() {
		return db;
	}
	
	@Override
	public boolean isInitialized() {
		return db.getInitialization().isInitialized();
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (isInitialized())
			throw new RuntimeException("The system is already initialized");
		return db.getInitialization().initializeSystem(organization, name, email, username, password); 
	}
	
	@Override
	public boolean reset() {
		return db.getInitialization().reset();
	}
	
	@Override
	public void dump(OutputStream os) {
		db.getInitialization().dump(os);
	}
	
	@Override
	public List<Status> findStatuses() {
		return db.getLookups().findStatuses();
	}
	
	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findStatusByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findStatusByLocalizedName(locale, name);
	}
	
	@Override
	public List<Country> findCountries() {
		return db.getLookups().findCountries();
	}
	
	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findCountryByCode(code);
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findCountryByLocalizedName(locale, name);
	}
	
	@Override
	public List<Province> findProvinces(String country) {
		return db.getLookups().findProvinces(country);
	}
	
	@Override
	public Province findProvinceByCode(String province, String country) {
		return db.getLookups().findProvinceByCode(province, country);
	}
	
	@Override
	public Province findProvinceByLocalizedName(Locale locale, String province,
			String country) {
		return db.getLookups().findProvinceByLocalizedName(locale, province, country);
	}
	
	@Override
	public List<Salutation> findSalutations() {
		return db.getLookups().findSalutations();
	}
	
	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findSalutationByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findSalutationByLocalizedName(locale, name);
	}	
	
	@Override
	public List<Language> findLanguages() {
		return db.getLookups().findLanguages();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findLanguageByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findLanguageByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return db.getLookups().findBusinessSectors();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findBusinessSectorByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findBusinessSectorByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return db.getLookups().findBusinessUnits();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findBusinessUnitByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return db.getLookups().findBusinessUnitByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return db.getLookups().findBusinessClassifications();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return db.getLookups().findBusinessClassificationByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return db.getLookups().findBusinessClassificationByLocalizedName(locale, name);
	}
	
	@Override
	public OrganizationDetails createOrganization(String organizationDisplayName, List<String> groups) {
		return db.getOrganizations().createOrganization(organizationDisplayName, groups);
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return db.getOrganizations().updateOrganizationDisplayName(organizationId, name);
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return db.getOrganizations().updateOrganizationMainLocation(organizationId, locationId);
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		return db.getOrganizations().updateOrganizationMainContact(organizationId, personId);
	}
	
	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> group) {
		return db.getOrganizations().updateOrganizationGroups(organizationId, group);
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		return db.getOrganizations().enableOrganization(organizationId);
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		return db.getOrganizations().disableOrganization(organizationId);
	}
	
	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return db.getOrganizations().findOrganizationDetails(organizationId);
	}
	
	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return db.getOrganizations().findOrganizationDetails(organizationId);
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return db.getOrganizations().countOrganizations(filter);
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return db.getOrganizations().findOrganizationDetails(filter, paging);
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return db.getOrganizations().findOrganizationSummaries(filter, paging);
	}

	@Override
	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference,
			MailingAddress address) {
		return db.getLocations().createLocation(organizationId, displayName, reference, address);
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		return db.getLocations().updateLocationName(locationId, locationName);
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return db.getLocations().updateLocationAddress(locationId, address);
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		return db.getLocations().enableLocation(locationId);
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return db.getLocations().disableLocation(locationId);
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return db.getLocations().findLocationSummary(locationId);
	}
	
	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		return db.getLocations().findLocationDetails(locationId);
	}
	
	@Override
	public long countLocations(LocationsFilter filter) {
		return db.getLocations().countLocations(filter);
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return db.getLocations().findLocationDetails(filter, paging);
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return db.getLocations().findLocationSummaries(filter, paging);
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return db.getPersons().createPerson(organizationId, name, address, communication, position);
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		return db.getPersons().updatePersonName(personId, name);
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return db.getPersons().updatePersonAddress(personId, address);
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return db.getPersons().updatePersonCommunication(personId, communication);
	}
	
	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return db.getPersons().updatePersonBusinessPosition(personId, position);
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return db.getPersons().enablePerson(personId);
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return db.getPersons().disablePerson(personId);
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return db.getPersons().findPersonSummary(personId);
	}
	
	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return db.getPersons().findPersonDetails(personId);
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		return db.getPersons().countPersons(filter);
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return db.getPersons().findPersonDetails(filter, paging);
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return db.getPersons().findPersonSummaries(filter, paging);
	}
	
	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		return db.getUsers().createUser(personId, username, roles);
	}

	@Override
	public User enableUser(Identifier userId) {
		return db.getUsers().enableUser(userId);
	}

	@Override
	public User disableUser(Identifier userId) {
		return db.getUsers().disableUser(userId);
	}

	@Override
	public User findUser(Identifier userId) {
		return db.getUsers().findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		return db.getUsers().findUserByUsername(username);
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roleIds) {
		return db.getUsers().updateUserRoles(userId, roleIds);
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		return db.getUsers().changePassword(userId, currentPassword, newPassword);
	}

	@Override
	public String resetPassword(Identifier userId) {
		return db.getUsers().resetPassword(userId);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return db.getUsers().countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return db.getUsers().findUsers(filter, paging);
	}

	@Override
	public Group validate(Group group) {
		return db.getValidation().validate(group);
	}
	
	@Override
	public Role validate(Role role) throws BadRequestException {
		return db.getValidation().validate(role);
	}
	
	@Override
	public OrganizationDetails validate(OrganizationDetails organization) {
		return db.getValidation().validate(organization);
	}

	@Override
	public LocationDetails validate(LocationDetails location) {
		return db.getValidation().validate(location);
	}

	@Override
	public PersonDetails validate(PersonDetails person) {
		return db.getValidation().validate(person);
	}
	
	@Override
	public User validate(User user) {
		return db.getValidation().validate(user);
	}

	@Override
	public List<String> validate(List<String> roles, Identifier personId) {
		return db.getValidation().validate(roles, personId);
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return db.getPermissions().findGroups(filter, paging);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return db.getPermissions().findGroup(groupId);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		return db.getPermissions().findGroupByCode(code);
	}

	@Override
	public Group createGroup(Localized name) {
		return db.getPermissions().createGroup(name);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return db.getPermissions().updateGroupName(groupId, name);
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		return db.getPermissions().enableGroup(groupId);
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		return db.getPermissions().disableGroup(groupId);
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		return db.getPermissions().findRoles(filter, paging);
	}

	@Override
	public Role findRole(Identifier roleId) {
		return db.getPermissions().findRole(roleId);
	}

	@Override
	public Role findRoleByCode(String code) {
		return db.getPermissions().findRoleByCode(code);
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		return db.getPermissions().createRole(groupId, name);
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return db.getPermissions().updateRoleName(roleId, name);
	}

	@Override
	public Role enableRole(Identifier roleId) {
		return db.getPermissions().enableRole(roleId);
	}

	@Override
	public Role disableRole(Identifier roleId) {
		return db.getPermissions().disableRole(roleId);
	}
	
}
