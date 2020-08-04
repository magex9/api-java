package ca.magex.crm.api;

import static ca.magex.crm.api.services.CrmLocationService.validateLocationDetails;
import static ca.magex.crm.api.services.CrmOptionService.validateOption;
import static ca.magex.crm.api.services.CrmOrganizationService.validateOrganizationDetails;
import static ca.magex.crm.api.services.CrmPersonService.validatePersonDetails;
import static ca.magex.crm.api.services.CrmUserService.validateUser;

import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.PermissionDeniedException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.policies.CrmConfigurationPolicy;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

@Transactional
public class Crm extends CrmPoliciesAdapter implements CrmServices, CrmPolicies {
	
	public static final long SERIAL_UID_VERSION = 1l;

	public static final String SCHEMA_BASE = "http://api.magex.ca/crm/schema";
	
	public static final String REST_BASE = "http://api.magex.ca/crm/rest";
	
	private final CrmConfigurationService configurationService;

	private final CrmOptionService optionService;
	
	private final CrmOrganizationService organizationService;
	
	private final CrmLocationService locationService;
	
	private final CrmPersonService personService;
	
	private final CrmUserService userService;
	
	public Crm(Crm crm) {
		this(crm, crm);
	}
	
	public Crm(CrmServices services, CrmPolicies policies) {
		this(services, policies, services, policies, services, policies, services, policies,
				services, policies, services, policies);
	}
	
	public Crm(
			CrmConfigurationService configurationService, 
			CrmOptionService optionService,
			CrmOrganizationService organizationService,
			CrmLocationService locationService, 
			CrmPersonService personService,
			CrmUserService userService,
			CrmPolicies policies) {
		this(
			configurationService,
			policies,
			optionService,
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
	
	public Crm(CrmConfigurationService configurationService, CrmConfigurationPolicy configurationPolicy,
			CrmOptionService optionService, CrmOptionPolicy optionPolicy,
			CrmOrganizationService organizationService, CrmOrganizationPolicy organizationPolicy,
			CrmLocationService locationService, CrmLocationPolicy locationPolicy, 
			CrmPersonService personService, CrmPersonPolicy personPolicy,
			CrmUserService userService, CrmUserPolicy userPolicy) {
		super(configurationPolicy, optionPolicy, organizationPolicy, locationPolicy, personPolicy, userPolicy);
		this.configurationService = configurationService;
		this.optionService = optionService;
		this.organizationService = organizationService;
		this.locationService = locationService;
		this.personService = personService;
		this.userService = userService;
	}
	
	@Override
	public boolean isInitialized() {
		return configurationService.isInitialized();
	}

	@Override
	public UserDetails initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (isInitialized())
			throw new DuplicateItemFoundException("The system is already initialized");
		return configurationService.initializeSystem(organization, name, email, username, password); 
	}
	
	@Override
	public boolean reset() {
		return configurationService.reset();
	}
	
	@Override
	public void dump(OutputStream os) {
		configurationService.dump(os);
	}

	public OrganizationDetails validate(OrganizationDetails organization) {
		List<Message> messages = validateOrganizationDetails(this, organization);
		if (!messages.isEmpty())
			throw new BadRequestException("Validation Errors", messages);
		return organization;
	}
	
	public OrganizationDetails createOrganization(String displayName, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds) {
		if (!canCreateOrganization())
			throw new PermissionDeniedException("createOrganization");
		return organizationService.createOrganization(
			validate(prototypeOrganization(displayName, authenticationGroupIds, businessGroupIds)));
	}

	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationDisplayName: " + organizationId);
		return organizationService.updateOrganizationDisplayName(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withDisplayName(name)).getDisplayName());
	}

	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateMainLocation: " + organizationId);
		return organizationService.updateOrganizationMainLocation(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withMainLocationId(locationId)).getMainLocationId());
	}
	
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("updateOrganizationMainContact: " + organizationId);
		return organizationService.updateOrganizationMainContact(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withMainContactId(personId)).getMainContactId());
	}
	
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("setGroups: " + organizationId + ", " + authenticationGroupIds);
		return organizationService.updateOrganizationAuthenticationGroups(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withAuthenticationGroupIds(authenticationGroupIds)).getAuthenticationGroupIds());
	}
	
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> businessGroupIds) {
		if (!canUpdateOrganization(organizationId))
			throw new PermissionDeniedException("setGroups: " + organizationId + ", " + businessGroupIds);
		return organizationService.updateOrganizationBusinessGroups(organizationId, 
			validate(organizationService.findOrganizationDetails(organizationId).withBusinessGroupIds(businessGroupIds)).getBusinessGroupIds());
	}

	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		if (!canEnableOrganization(organizationId))
			throw new PermissionDeniedException("enableOrganization: " + organizationId);
		return organizationService.enableOrganization(
			validate(organizationService.findOrganizationDetails(organizationId).withStatus(Status.ACTIVE)).getOrganizationId());
	}

	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		if (!canDisableOrganization(organizationId))
			throw new PermissionDeniedException("disableOrganization: " + organizationId);
		return organizationService.disableOrganization(
			validate(organizationService.findOrganizationDetails(organizationId).withStatus(Status.INACTIVE)).getOrganizationId());
	}
	
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return organizationService.findOrganizationSummary(organizationId);
	}
	
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		if (!canViewOrganization(organizationId))
			throw new PermissionDeniedException("findOrganization: " + organizationId);
		return organizationService.findOrganizationDetails(organizationId);
	}
	
	public long countOrganizations(OrganizationsFilter filter) {
		return organizationService.countOrganizations(filter);
	}
	
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return applyPolicy(organizationService.findOrganizationDetails(filter, paging), (o) -> canViewOrganization(o.getOrganizationId()));
	}

	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return applyPolicy(organizationService.findOrganizationSummaries(filter, paging), (o) -> canViewOrganization(o.getOrganizationId()));
	}

	public LocationDetails validate(LocationDetails location) {
		List<Message> messages = validateLocationDetails(this, location);
		if (!messages.isEmpty())
			throw new BadRequestException("Location has validation errors", messages);
		return location;
	}
	
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String reference, String displayName, 
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return locationService.createLocation(validate(prototypeLocation(organizationId, reference, displayName, address)));
	}

	public LocationDetails updateLocationName(LocationIdentifier locationId, String displayName) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationName: " + locationId);
		return locationService.updateLocationName(locationId, validate(locationService.findLocationDetails(locationId).withDisplayName(displayName)).getDisplayName());
	}

	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		if (!canUpdateLocation(locationId))
			throw new PermissionDeniedException("updateLocationAddress: " + locationId);
		return locationService.updateLocationAddress(locationId, validate(findLocationDetails(locationId).withAddress(address)).getAddress());
	}

	public LocationSummary enableLocation(LocationIdentifier locationId) {
		if (!canEnableLocation(locationId))
			throw new PermissionDeniedException("enableLocation: " + locationId);
		return locationService.enableLocation(
			validate(locationService.findLocationDetails(locationId).withStatus(Status.ACTIVE)).getLocationId());
	}

	public LocationSummary disableLocation(LocationIdentifier locationId) {
		if (!canDisableLocation(locationId))
			throw new PermissionDeniedException("disableLocation: " + locationId);
		return locationService.disableLocation(
			validate(locationService.findLocationDetails(locationId).withStatus(Status.INACTIVE)).getLocationId());
	}

	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return locationService.findLocationSummary(locationId);
	}
	
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		if (!canViewLocation(locationId))
			throw new PermissionDeniedException("findLocation: " + locationId);
		return locationService.findLocationDetails(locationId);
	}
	
	public long countLocations(LocationsFilter filter) {
		return locationService.countLocations(filter);
	}
	
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return applyPolicy(locationService.findLocationDetails(filter, paging), (o) -> canViewLocation(o.getLocationId()));
	}
	
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return applyPolicy(locationService.findLocationSummaries(filter, paging), (o) -> canViewLocation(o.getLocationId()));
	}

	public PersonDetails validate(PersonDetails person) {
		List<Message> messages = validatePersonDetails(this, person);
		if (!messages.isEmpty())
			throw new BadRequestException("Person has validation errors", messages);
		return person;
	}

	public PersonDetails createPerson(OrganizationIdentifier organizationId, PersonName name, MailingAddress address, Communication communication, List<BusinessRoleIdentifier> roleIds) {
		if (!canCreatePersonForOrganization(organizationId))
			throw new PermissionDeniedException("createPerson: " + organizationId);
		return personService.createPerson(validate(prototypePerson(organizationId, name, address, communication, roleIds)));
	}

	public PersonDetails updatePersonName(PersonIdentifier personId, PersonName name) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonName: " + personId);
		return personService.updatePersonName(personId, validate(findPersonDetails(personId).withLegalName(name)).getLegalName());
	}

	public PersonDetails updatePersonAddress(PersonIdentifier personId, MailingAddress address) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonAddress: " + personId);
		return personService.updatePersonAddress(personId, validate(findPersonDetails(personId).withAddress(address)).getAddress());
	}

	public PersonDetails updatePersonCommunication(PersonIdentifier personId, Communication communication) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonCommunication: " + personId);
		return personService.updatePersonCommunication(personId, validate(findPersonDetails(personId).withCommunication(communication)).getCommunication());
	}
	
	@Override
	public PersonDetails updatePersonRoles(PersonIdentifier personId, List<BusinessRoleIdentifier> roleIds) {
		if (!canUpdatePerson(personId))
			throw new PermissionDeniedException("updatePersonBusinessPosition: " + personId);
		return personService.updatePersonRoles(personId, validate(findPersonDetails(personId).withBusinessRoleIds(roleIds)).getBusinessRoleIds());
	}

	public PersonSummary enablePerson(PersonIdentifier personId) {
		if (!canEnablePerson(personId))
			throw new PermissionDeniedException("enablePerson: " + personId);
		return personService.enablePerson(
			validate(personService.findPersonDetails(personId).withStatus(Status.ACTIVE)).getPersonId());
	}

	public PersonSummary disablePerson(PersonIdentifier personId) {
		if (!canDisablePerson(personId))
			throw new PermissionDeniedException("disablePerson: " + personId);
		return personService.disablePerson(
			validate(personService.findPersonDetails(personId).withStatus(Status.INACTIVE)).getPersonId());
	}

	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return personService.findPersonSummary(personId);
	}
	
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		if (!canViewPerson(personId))
			throw new PermissionDeniedException("findPerson: " + personId);
		return personService.findPersonDetails(personId);
	}
	
	public long countPersons(PersonsFilter filter) {
		return personService.countPersons(filter);
	}
	
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return applyPolicy(personService.findPersonDetails(filter, paging), (o) -> canViewPerson(o.getPersonId()));
	}
	
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return applyPolicy(personService.findPersonSummaries(filter, paging), (o) -> canViewPerson(o.getPersonId()));
	}

	public UserDetails validate(UserDetails user) {
		List<Message> messages = validateUser(this, user);
		if (!messages.isEmpty())
			throw new BadRequestException("User has validation errors", messages);
		return user;
	}
	
	@Override
	public UserDetails createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> roles) {
		if (!canCreateUserForPerson(personId))
			throw new PermissionDeniedException("createUser: " + personId);
		return userService.createUser(validate(prototypeUser(personId, username, roles)));
	}

	@Override
	public UserSummary enableUser(UserIdentifier userId) {
		if (!canEnableUser(userId))
			throw new PermissionDeniedException("enableUser: " + userId);
		return userService.enableUser(
			validate(userService.findUserDetails(userId).withStatus(Status.ACTIVE)).getUserId());
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		if (!canDisableUser(userId))
			throw new PermissionDeniedException("enableUser: " + userId);
		return userService.disableUser(
			validate(userService.findUserDetails(userId).withStatus(Status.INACTIVE)).getUserId());
	}
	
	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		return applyPolicy(userService.findUserSummaries(filter, paging), (o) -> canViewUser(o.getUserId()));
	}
	
	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		return applyPolicy(userService.findUserDetails(filter, paging), (o) -> canViewUser(o.getUserId()));
	}
	
	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		if (!canViewUser(userId))
			throw new PermissionDeniedException("findUser: " + userId);
		return userService.findUserSummary(userId);
	}
	
	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		if (!canViewUser(userId))
			throw new PermissionDeniedException("findUser: " + userId);
		return userService.findUserDetails(userId);
	}
	
	@Override
	public UserDetails findUserByUsername(String username) {
		if (!canViewUser(username))
			throw new PermissionDeniedException("findUserByUsername: " + username);
		return userService.findUserByUsername(username);
	}

	@Override
	public UserDetails updateUserRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> roles) {
		if (!canUpdateUserRole(userId))
			throw new PermissionDeniedException("setRoles: " + userId);
		return userService.updateUserRoles(userId, validate(findUserDetails(userId).withAuthenticationRoleIds(roles)).getAuthenticationRoleIds());
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		return userService.changePassword(userId, currentPassword, newPassword);
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		if (!canUpdateUserPassword(userId)) {
			throw new PermissionDeniedException("resetPassword:" + userId);
		}
		return userService.resetPassword(userId);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return userService.countUsers(filter);
	}

	public Option validate(Option option) {
		List<Message> messages = validateOption(this, option);
		if (!messages.isEmpty())
			throw new BadRequestException("Organization has validation errors", messages);
		return option;
	}

	@Override
	public Option createOption(OptionIdentifier parentId, Type type, Localized name) {
		if (!canCreateOption(type))
			throw new PermissionDeniedException("createOption: " + type.getCode() + ", " + name);
		Option parent = (parentId == null) ? null : findOption(parentId); 
		return optionService.createOption(validate(prototypeOption(parent, type, name)));
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		if (!canViewOption(optionId))
			throw new PermissionDeniedException("findOption: " + optionId);
		return optionService.findOption(optionId);
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		if (!canViewOption(type, optionCode))
			throw new PermissionDeniedException("findOptionByCode: " + type + ", " + optionCode);
		return optionService.findOptionByCode(type, optionCode);
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		if (!canUpdateOption(optionId))
			throw new PermissionDeniedException("updateOptionName: " + optionId + ", " + name);
		return optionService.updateOptionName(optionId, validate(findOption(optionId).withName(name)).getName());
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		if (!canEnableOption(optionId))
			throw new PermissionDeniedException("enableOption: " + optionId);
		return optionService.enableOption(
				validate(optionService.findOption(optionId).withStatus(Status.ACTIVE)).getOptionId());
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		if (!canDisableOption(optionId))
			throw new PermissionDeniedException("disableOption: " + optionId);
		return optionService.disableOption(
				validate(optionService.findOption(optionId).withStatus(Status.INACTIVE)).getOptionId());
	}
	
	@Override
	public long countOptions(OptionsFilter filter) {
		return optionService.countOptions(filter);
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		return applyPolicy(optionService.findOptions(filter, paging), (o) -> canViewOption(o.getOptionId()));
	}
	
	private <T> FilteredPage<T> applyPolicy(FilteredPage<T> results, Function<T, Boolean> policy) {
		results.getContent().forEach(o -> {
			if (!policy.apply(o))
				throw new PermissionDeniedException("Cannot view results: " + results.getFilter());
		});
		return results;
	}
	
}
