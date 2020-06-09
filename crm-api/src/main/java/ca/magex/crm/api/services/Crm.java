package ca.magex.crm.api.services;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

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
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
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
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.CrmValidation;

public class Crm implements CrmInitializationService, CrmServices, CrmPolicies {
	
	public static final long SERIAL_UID_VERSION = 1l;

	public static final long PREVIOUS_API_VERSION = 1;
	
	public static final String PREVIOUS_API_PREFIX = "/v" + PREVIOUS_API_VERSION;
	
	public static final long CURRENT_API_VERSION = 1;
	
	public static final String CURRENT_API_PREFIX = "/v" + PREVIOUS_API_VERSION;
	
	private final CrmInitializationService initializationService;

	private final CrmLookupService lookupService;
	
	private final CrmLookupPolicy lookupPolicy;
	
	private final CrmPermissionService permissionsService;
	
	private final CrmPermissionPolicy permissionsPolicy;
	
	private final CrmOrganizationService organizationService;
	
	private final CrmOrganizationPolicy organizationPolicy;
	
	private final CrmLocationService locationService;
	
	private final CrmLocationPolicy locationPolicy;
	
	private final CrmPersonService personService;
	
	private final CrmPersonPolicy personPolicy;
	
	private final CrmUserService userService;
	
	private final CrmUserPolicy userPolicy;
	
	private final CrmValidation validation;

	public Crm(Crm crm) {
		this(
			crm.initializationService,
			crm.lookupService,
			crm.lookupPolicy,
			crm.permissionsService,
			crm.permissionsPolicy,
			crm.organizationService,
			crm.organizationPolicy,
			crm.locationService,
			crm.locationPolicy,
			crm.personService,
			crm.personPolicy,
			crm.userService,
			crm.userPolicy
		);
	}
	
	public Crm(
			CrmInitializationService initializationService, 
			CrmLookupService lookupService,
			CrmPermissionService permissionsService, 
			CrmOrganizationService organizationService,
			CrmLocationService locationService, 
			CrmPersonService personService,
			CrmUserService userService,
			CrmPolicies policies) {
		this(
				initializationService,
				lookupService,
				policies,
				permissionsService,
				policies,
				organizationService,
				policies,
				locationService,
				policies,
				personService,
				policies,
				userService,
				policies
		);
	}
	
	public Crm(CrmInitializationService initializationService, 
			CrmLookupService lookupService, CrmLookupPolicy lookupPolicy,
			CrmPermissionService permissionsService, CrmPermissionPolicy permissionsPolicy, 
			CrmOrganizationService organizationService, CrmOrganizationPolicy organizationPolicy,
			CrmLocationService locationService, CrmLocationPolicy locationPolicy, 
			CrmPersonService personService, CrmPersonPolicy personPolicy,
			CrmUserService userService, CrmUserPolicy userPolicy) {
		super();
		this.initializationService = initializationService;		
		this.lookupService = lookupService;
		this.lookupPolicy = lookupPolicy;
		this.organizationService = organizationService;
		this.organizationPolicy = organizationPolicy;
		this.locationService = locationService;
		this.locationPolicy = locationPolicy;
		this.personService = personService;
		this.personPolicy = personPolicy;
		this.userService = userService;
		this.userPolicy = userPolicy;
		this.permissionsService = permissionsService;
		this.permissionsPolicy = permissionsPolicy;
		this.validation = new CrmValidation(this);
	}
	
	@Override
	public boolean isInitialized() {
		return initializationService.isInitialized();
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (isInitialized())
			throw new DuplicateItemFoundException("The system is already initialized");
		return initializationService.initializeSystem(organization, name, email, username, password); 
	}
	
	@Override
	public boolean reset() {
		return initializationService.reset();
	}
	
	@Override
	public void dump(OutputStream os) {
		initializationService.dump(os);
	}
	
	@Override
	public List<Status> findStatuses() {
		return lookupService.findStatuses();
	}
	
	@Override
	public Status findStatusByCode(String code) {
		if (!lookupPolicy.canViewStatusLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findStatusByCode");
		}
		return lookupService.findStatusByCode(code);
	}
	
	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewStatusLookup(name, locale)) {
			throw new PermissionDeniedException("findStatusByLocalizedName");
		}
		return lookupService.findStatusByLocalizedName(locale, name);
	}
	
	@Override
	public List<Country> findCountries() {
		return lookupService.findCountries();
	}
	
	@Override
	public Country findCountryByCode(String code) {
		if (!lookupPolicy.canViewCountryLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findCountryByCode");
		}
		return lookupService.findCountryByCode(code);
	}
	
	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewCountryLookup(name, locale)) {
			throw new PermissionDeniedException("findCountryByLocalizedName");
		}
		return lookupService.findCountryByLocalizedName(locale, name);
	}
	
	@Override
	public List<Province> findProvinces(String country) {
		return lookupService.findProvinces(country);
	}
	
	@Override
	public Province findProvinceByCode(String province, String country) {
		if (!lookupPolicy.canViewProvinceLookup(country, province, Lang.ROOT)) {
			throw new PermissionDeniedException("findProvinceByCode");
		}
		return lookupService.findProvinceByCode(province, country);
	}
	
	@Override
	public Province findProvinceByLocalizedName(Locale locale, String province,String country) {
		if (!lookupPolicy.canViewProvinceLookup(country, province, locale)) {
			throw new PermissionDeniedException("findProvinceByLocalizedName");
		}
		return lookupService.findProvinceByLocalizedName(locale, province, country);
	}
	
	@Override
	public List<Salutation> findSalutations() {
		return lookupService.findSalutations();
	}
	
	@Override
	public Salutation findSalutationByCode(String code) {
		if (!lookupPolicy.canViewSalutationLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findSalutationByCode");
		}
		return lookupService.findSalutationByCode(code);
	}
	
	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewSalutationLookup(name, locale)) {
			throw new PermissionDeniedException("findSalutationByLocalizedName");
		}
		return lookupService.findSalutationByLocalizedName(locale, name);
	}	
	
	@Override
	public List<Language> findLanguages() {
		return lookupService.findLanguages();
	}

	@Override
	public Language findLanguageByCode(String code) {
		if (!lookupPolicy.canViewLanguageLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findLanguageByCode");
		}
		return lookupService.findLanguageByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewLanguageLookup(name, locale)) {
			throw new PermissionDeniedException("findLanguageByLocalizedName");
		}
		return lookupService.findLanguageByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return lookupService.findBusinessSectors();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) {
		if (!lookupPolicy.canViewBusinessSectorLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findBusinessSectorByCode");
		}
		return lookupService.findBusinessSectorByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewBusinessSectorLookup(name, locale)) {
			throw new PermissionDeniedException("findBusinessSectorByLocalizedName");
		}
		return lookupService.findBusinessSectorByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return lookupService.findBusinessUnits();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) {
		if (!lookupPolicy.canViewBusinessUnitLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findBusinessUnitByCode");
		}
		return lookupService.findBusinessUnitByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) {
		if (!lookupPolicy.canViewBusinessUnitLookup(name, locale)) {
			throw new PermissionDeniedException("findBusinessUnitByLocalizedName");
		}
		return lookupService.findBusinessUnitByLocalizedName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return lookupService.findBusinessClassifications();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) {
		if (!lookupPolicy.canViewBusinessClassificationLookup(code, Lang.ROOT)) {
			throw new PermissionDeniedException("findBusinessClassificationByCode");
		}
		return lookupService.findBusinessClassificationByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) {	
		if (!lookupPolicy.canViewBusinessClassificationLookup(name, locale)) {
			throw new PermissionDeniedException("findBusinessClassificationByLocalizedName");
		}
		return lookupService.findBusinessClassificationByLocalizedName(locale, name);
	}

	public OrganizationDetails validate(OrganizationDetails organization) {
		List<Message> messages = validation.validate(organization);
		if (!messages.isEmpty())
			throw new BadRequestException("Organization has validation errors", messages);
		return organization;
	}
	
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		if (!canCreateOrganization())
			throw new PermissionDeniedException("createOrganization");
		return organizationService.createOrganization(
			validate(prototypeOrganization(displayName, groups)));
	}

	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationDisplayName: " + organizationId);
		return organizationService.updateOrganizationDisplayName(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withDisplayName(name)).getDisplayName());
	}

	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateMainLocation: " + organizationId);
		return organizationService.updateOrganizationMainLocation(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withMainLocationId(locationId)).getMainLocationId());
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationMainContact: " + organizationId);
		return organizationService.updateOrganizationMainContact(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withMainContactId(personId)).getMainContactId());
	}
	
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("setGroups: " + organizationId + ", " + groups);
		return organizationService.updateOrganizationGroups(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withGroups(groups)).getGroups());
	}

	public OrganizationSummary enableOrganization(Identifier organizationId) {
		if (!canEnableOrganization(organizationId))
			throw new PermissionDeniedException("enableOrganization: " + organizationId);
		return organizationService.enableOrganization(
			validate(organizationService.findOrganizationDetails(organizationId).withStatus(Status.ACTIVE)).getOrganizationId());
	}

	public OrganizationSummary disableOrganization(Identifier organizationId) {
		if (!canDisableOrganization(organizationId))
			throw new PermissionDeniedException("disableOrganization: " + organizationId);
		return organizationService.disableOrganization(
			validate(organizationService.findOrganizationDetails(organizationId).withStatus(Status.INACTIVE)).getOrganizationId());
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
	
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return organizationService.findOrganizationDetails(filter, paging);
	}

	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return organizationService.findOrganizationSummaries(filter, paging);
	}

	public LocationDetails validate(LocationDetails location) {
		List<Message> messages = validation.validate(location);
		if (!messages.isEmpty())
			throw new BadRequestException("Location has validation errors", messages);
		return location;
	}
	
	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference,
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return locationService.createLocation(validate(prototypeLocation(organizationId, displayName, reference, address)));
	}

	public LocationDetails updateLocationName(Identifier locationId, String displayName) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationName: " + locationId);
		return locationService.updateLocationName(locationId, validate(locationService.findLocationDetails(locationId).withDisplayName(displayName)).getDisplayName());
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationAddress: " + locationId);
		return locationService.updateLocationAddress(locationId, validate(findLocationDetails(locationId).withAddress(address)).getAddress());
	}

	public LocationSummary enableLocation(Identifier locationId) {
		if (!canEnableLocation(locationId))
			throw new PermissionDeniedException("enableLocation: " + locationId);
		return locationService.enableLocation(
			validate(locationService.findLocationDetails(locationId).withStatus(Status.ACTIVE)).getLocationId());
	}

	public LocationSummary disableLocation(Identifier locationId) {
		if (!canDisableLocation(locationId))
			throw new PermissionDeniedException("disableLocation: " + locationId);
		return locationService.disableLocation(
			validate(locationService.findLocationDetails(locationId).withStatus(Status.INACTIVE)).getLocationId());
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
	
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return locationService.findLocationDetails(filter, paging);
	}
	
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return locationService.findLocationSummaries(filter, paging);
	}

	public PersonDetails validate(PersonDetails person) {
		List<Message> messages = validation.validate(person);
		if (!messages.isEmpty())
			throw new BadRequestException("Person has validation errors", messages);
		return person;
	}

	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		if (!canCreatePersonForOrganization(organizationId))
			throw new PermissionDeniedException("createPerson: " + organizationId);
		return personService.createPerson(validate(prototypePerson(organizationId, name, address, communication, position)));
	}

	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonName: " + personId);
		return personService.updatePersonName(personId, validate(findPersonDetails(personId).withLegalName(name)).getLegalName());
	}

	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonAddress: " + personId);
		return personService.updatePersonAddress(personId, validate(findPersonDetails(personId).withAddress(address)).getAddress());
	}

	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonCommunication: " + personId);
		return personService.updatePersonCommunication(personId, validate(findPersonDetails(personId).withCommunication(communication)).getCommunication());
	}
	
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonBusinessPosition: " + personId);
		return personService.updatePersonBusinessPosition(personId, validate(findPersonDetails(personId).withPosition(position)).getPosition());
	}

	public PersonSummary enablePerson(Identifier personId) {
		if (!canEnablePerson(personId))
			throw new PermissionDeniedException("enablePerson: " + personId);
		return personService.enablePerson(
			validate(personService.findPersonDetails(personId).withStatus(Status.ACTIVE)).getPersonId());
	}

	public PersonSummary disablePerson(Identifier personId) {
		if (!canDisablePerson(personId))
			throw new PermissionDeniedException("disablePerson: " + personId);
		return personService.disablePerson(
			validate(personService.findPersonDetails(personId).withStatus(Status.INACTIVE)).getPersonId());
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
	
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return personService.findPersonDetails(filter, paging);
	}
	
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return personService.findPersonSummaries(filter, paging);
	}

	public User validate(User user) {
		List<Message> messages = validation.validate(user);
		if (!messages.isEmpty())
			throw new BadRequestException("User has validation errors", messages);
		return user;
	}
	
	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		if (!canCreateUserForPerson(personId))
			throw new PermissionDeniedException("createUser: " + personId);
		return userService.createUser(validate(prototypeUser(personId, username, roles)));
	}

	@Override
	public User enableUser(Identifier userId) {
		if (!canEnableUser(userId))
			throw new PermissionDeniedException("enableUser: " + userId);
		return userService.enableUser(
			validate(userService.findUser(userId).withStatus(Status.ACTIVE)).getUserId());
	}

	@Override
	public User disableUser(Identifier userId) {
		if (!canDisableUser(userId))
			throw new PermissionDeniedException("enableUser: " + userId);
		return userService.disableUser(
			validate(userService.findUser(userId).withStatus(Status.INACTIVE)).getUserId());
	}

	@Override
	public User findUser(Identifier userId) {
		if (!canViewUser(userId))
			throw new PermissionDeniedException("findUser: " + userId);
		return userService.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		if (!canViewUser(username))
			throw new PermissionDeniedException("findUserByUsername: " + username);
		return userService.findUserByUsername(username);
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		if (!canUpdateUserRole(userId))
			throw new PermissionDeniedException("setRoles: " + userId);
		return userService.updateUserRoles(userId, validate(findUser(userId).withRoles(roles)).getRoles());
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		return userService.changePassword(userId, currentPassword, newPassword);
	}

	@Override
	public String resetPassword(Identifier userId) {
		if (!canUpdateUserPassword(userId)) {
			throw new PermissionDeniedException("resetPassword:" + userId);
		}
		return userService.resetPassword(userId);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return userService.countUsers(filter);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return userService.findUsers(filter, paging);
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		return userPolicy.canEnableUser(userId);
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		return userPolicy.canDisableUser(userId);
	}
	
	@Override
	public boolean canViewUser(String username) {
		return userPolicy.canViewUser(username);
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

	public Group validate(Group group) {
		List<Message> messages = validation.validate(group);
		if (!messages.isEmpty())
			throw new BadRequestException("Group has validation errors", messages);
		return group;
	}

	@Override
	public Group createGroup(Localized name) {
		if (!canCreateGroup())
			throw new PermissionDeniedException("createGroup: " + name);
		return permissionsService.createGroup(validate(prototypeGroup(name)));
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return permissionsService.findGroups(filter, paging);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		if (!canViewGroup(groupId))
			throw new PermissionDeniedException("findGroup: " + groupId);
		return permissionsService.findGroup(groupId);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		if (!canViewGroup(code))
			throw new PermissionDeniedException("findGroupByCode: " + code);
		return permissionsService.findGroupByCode(code);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		if (!canUpdateGroup(groupId))
			throw new PermissionDeniedException("updateGroupName: " + groupId);
		return permissionsService.updateGroupName(groupId, validate(findGroup(groupId).withName(name)).getName());
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		if (!canEnableGroup(groupId))
			throw new PermissionDeniedException("enableGroup: " + groupId);
		return permissionsService.enableGroup(
			validate(findGroup(groupId).withStatus(Status.ACTIVE)).getGroupId());
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		if (!canDisableGroup(groupId))
			throw new PermissionDeniedException("disableGroup: " + groupId);
		return permissionsService.disableGroup(
			validate(findGroup(groupId).withStatus(Status.INACTIVE)).getGroupId());
	}

	public Role validate(Role role) {
		List<Message> messages = validation.validate(role);
		if (!messages.isEmpty())
			throw new BadRequestException("Role has validation errors", messages);
		return role;
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		if (!canViewRoles())
			throw new PermissionDeniedException("findRoles: " + filter);
		return permissionsService.findRoles(filter, paging);
	}

	@Override
	public Role findRole(Identifier roleId) {
		if (!canViewRole(roleId))
			throw new PermissionDeniedException("findRole: " + roleId);
		return permissionsService.findRole(roleId);
	}

	@Override
	public Role findRoleByCode(String code) {
		if (!canViewRole(code))
			throw new PermissionDeniedException("findRoleByCode: " + code);
		return permissionsService.findRoleByCode(code);
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		if (!canCreateRole(groupId))
			throw new PermissionDeniedException("createRole: " + groupId);
		return permissionsService.createRole(validate(prototypeRole(groupId, name)));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		if (!canUpdateRole(roleId))
			throw new PermissionDeniedException("updateRoleName: " + roleId);
		return permissionsService.updateRoleName(roleId, validate(findRole(roleId).withName(name)).getName());
	}

	@Override
	public Role enableRole(Identifier roleId) {
		if (!canEnableRole(roleId))
			throw new PermissionDeniedException("enableRole: " + roleId);
		return permissionsService.enableRole(
			validate(findRole(roleId).withStatus(Status.ACTIVE)).getRoleId());
	}

	@Override
	public Role disableRole(Identifier roleId) {
		if (!canDisableRole(roleId))
			throw new PermissionDeniedException("disableRole: " + roleId);
		return permissionsService.disableRole(
			validate(findRole(roleId).withStatus(Status.INACTIVE)).getRoleId());
	}

	@Override
	public boolean canCreateGroup() {
		return permissionsPolicy.canCreateGroup();
	}

	@Override
	public boolean canViewGroup(String group) {
		return permissionsPolicy.canViewGroup(group);
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		return permissionsPolicy.canViewGroup(groupId);
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		return permissionsPolicy.canUpdateGroup(groupId);
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		return permissionsPolicy.canEnableGroup(groupId);
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		return permissionsPolicy.canDisableGroup(groupId);
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		return permissionsPolicy.canCreateRole(groupId);
	}
	
	@Override
	public boolean canViewRoles() {
		return permissionsPolicy.canViewRoles();
	}

	@Override
	public boolean canViewRole(String code) {
		return permissionsPolicy.canViewRole(code);
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		return permissionsPolicy.canViewRole(roleId);
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		return permissionsPolicy.canUpdateRole(roleId);
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		return permissionsPolicy.canEnableRole(roleId);
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		return permissionsPolicy.canDisableRole(roleId);
	}
	
	@Override
	public boolean canViewBusinessClassificationLookup(String classificationLookup, Locale locale) {
		return lookupPolicy.canViewBusinessClassificationLookup(classificationLookup, locale);
	}
	
	@Override
	public boolean canViewBusinessSectorLookup(String sectorLookup, Locale locale) {
		return lookupPolicy.canViewBusinessSectorLookup(sectorLookup, locale);
	}
	
	@Override
	public boolean canViewBusinessUnitLookup(String unitLookup, Locale locale) {
		return lookupPolicy.canViewBusinessUnitLookup(unitLookup, locale);
	}
	
	@Override
	public boolean canViewCountryLookup(String CountryLookup, Locale locale) {
		return lookupPolicy.canViewCountryLookup(CountryLookup, locale);
	}
	
	@Override
	public boolean canViewLanguageLookup(String languageLookup, Locale locale) {
		return lookupPolicy.canViewLanguageLookup(languageLookup, locale);
	}
	
	@Override
	public boolean canViewProvinceLookup(String countryLookup, String provinceLookup, Locale locale) {
		return lookupPolicy.canViewProvinceLookup(countryLookup, provinceLookup, locale);
	}
	
	@Override
	public boolean canViewSalutationLookup(String salutationLookup, Locale locale) {
		return lookupPolicy.canViewSalutationLookup(salutationLookup, locale);
	}
	
	@Override
	public boolean canViewStatusLookup(String StatusLookup, Locale locale) {
		return lookupPolicy.canViewStatusLookup(StatusLookup, locale);
	}
}
