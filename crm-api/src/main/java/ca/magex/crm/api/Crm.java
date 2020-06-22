package ca.magex.crm.api;

import java.io.OutputStream;
import java.util.List;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
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
import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.policies.CrmConfigurationPolicy;
import ca.magex.crm.api.policies.CrmGroupPolicy;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.CrmOptionPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.CrmRolePolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.CrmValidation;

public class Crm extends CrmPoliciesAdapter implements CrmServices, CrmPolicies {
	
	public static final long SERIAL_UID_VERSION = 1l;

	private final CrmConfigurationService configurationService;

	private final CrmLookupService lookupService;
	
	private final CrmOptionService optionService;
	
	private final CrmGroupService groupService;
	
	private final CrmRoleService roleService;
	
	private final CrmOrganizationService organizationService;
	
	private final CrmLocationService locationService;
	
	private final CrmPersonService personService;
	
	private final CrmUserService userService;
	
	private final CrmValidation validation;
	
	public Crm(Crm crm) {
		this(crm, crm);
	}
	
	public Crm(
			CrmServices services,
			CrmPolicies policies) {
		this(
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies,
			services,
			policies
		);
	}
	
	public Crm(
			CrmConfigurationService configurationService, 
			CrmLookupService lookupService,
			CrmOptionService optionService,
			CrmGroupService groupsService, 
			CrmRoleService rolesService, 
			CrmOrganizationService organizationService,
			CrmLocationService locationService, 
			CrmPersonService personService,
			CrmUserService userService,
			CrmPolicies policies) {
		this(
			configurationService,
			policies,
			lookupService,
			policies,
			optionService,
			policies,
			groupsService,
			policies,
			rolesService,
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
			CrmLookupService lookupService, CrmLookupPolicy lookupPolicy,
			CrmOptionService optionService, CrmOptionPolicy optionPolicy,
			CrmGroupService groupService, CrmGroupPolicy groupPolicy, 
			CrmRoleService roleService, CrmRolePolicy rolePolicy, 
			CrmOrganizationService organizationService, CrmOrganizationPolicy organizationPolicy,
			CrmLocationService locationService, CrmLocationPolicy locationPolicy, 
			CrmPersonService personService, CrmPersonPolicy personPolicy,
			CrmUserService userService, CrmUserPolicy userPolicy) {
		super(configurationPolicy, lookupPolicy, optionPolicy, groupPolicy, rolePolicy, organizationPolicy, locationPolicy, personPolicy, userPolicy);
		this.configurationService = configurationService;
		this.lookupService = lookupService;
		this.optionService = optionService;
		this.groupService = groupService;
		this.roleService = roleService;
		this.organizationService = organizationService;
		this.locationService = locationService;
		this.personService = personService;
		this.userService = userService;
		this.validation = new CrmValidation(this);
	}
	
	@Override
	public boolean isInitialized() {
		return configurationService.isInitialized();
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
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
		return organizationService.findOrganizationSummary(organizationId);
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
	
	public LocationDetails createLocation(Identifier organizationId, String reference, String displayName, 
			MailingAddress address) {
		if (!canCreateLocationForOrganization(organizationId))
			throw new PermissionDeniedException("createLocation: " + organizationId);
		return locationService.createLocation(validate(prototypeLocation(organizationId, reference, displayName, address)));
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
		return groupService.createGroup(validate(prototypeGroup(name)));
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return groupService.findGroups(filter, paging);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		if (!canViewGroup(groupId))
			throw new PermissionDeniedException("findGroup: " + groupId);
		return groupService.findGroup(groupId);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		if (!canViewGroup(code))
			throw new PermissionDeniedException("findGroupByCode: " + code);
		return groupService.findGroupByCode(code);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		if (!canUpdateGroup(groupId))
			throw new PermissionDeniedException("updateGroupName: " + groupId);
		return groupService.updateGroupName(groupId, validate(findGroup(groupId).withName(name)).getName());
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		if (!canEnableGroup(groupId))
			throw new PermissionDeniedException("enableGroup: " + groupId);
		return groupService.enableGroup(
			validate(findGroup(groupId).withStatus(Status.ACTIVE)).getGroupId());
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		if (!canDisableGroup(groupId))
			throw new PermissionDeniedException("disableGroup: " + groupId);
		return groupService.disableGroup(
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
		return roleService.findRoles(filter, paging);
	}

	@Override
	public Role findRole(Identifier roleId) {
		if (!canViewRole(roleId))
			throw new PermissionDeniedException("findRole: " + roleId);
		return roleService.findRole(roleId);
	}

	@Override
	public Role findRoleByCode(String code) {
		if (!canViewRole(code))
			throw new PermissionDeniedException("findRoleByCode: " + code);
		return roleService.findRoleByCode(code);
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		if (!canCreateRole(groupId))
			throw new PermissionDeniedException("createRole: " + groupId);
		return roleService.createRole(validate(prototypeRole(groupId, name)));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		if (!canUpdateRole(roleId))
			throw new PermissionDeniedException("updateRoleName: " + roleId);
		return roleService.updateRoleName(roleId, validate(findRole(roleId).withName(name)).getName());
	}

	@Override
	public Role enableRole(Identifier roleId) {
		if (!canEnableRole(roleId))
			throw new PermissionDeniedException("enableRole: " + roleId);
		return roleService.enableRole(
			validate(findRole(roleId).withStatus(Status.ACTIVE)).getRoleId());
	}

	@Override
	public Role disableRole(Identifier roleId) {
		if (!canDisableRole(roleId))
			throw new PermissionDeniedException("disableRole: " + roleId);
		return roleService.disableRole(
			validate(findRole(roleId).withStatus(Status.INACTIVE)).getRoleId());
	}

	public Lookup validate(Lookup lookup) {
		List<Message> messages = validation.validate(lookup);
		if (!messages.isEmpty())
			throw new BadRequestException("Lookup has validation errors", messages);
		return lookup;
	}

	@Override
	public Lookup createLookup(Localized name, Option parent) {
		if (!canCreateLookup())
			throw new PermissionDeniedException("createLookup: " + name + ", " + parent);
		return lookupService.createLookup(validate(prototypeLookup(name, parent)));
	}

	@Override
	public Lookup findLookup(Identifier lookupId) {
		if (!canViewLookup(lookupId))
			throw new PermissionDeniedException("findLookup: " + lookupId);
		return lookupService.findLookup(lookupId);
	}

	@Override
	public Lookup findLookupByCode(String lookupCode) {
		if (!canViewLookup(lookupCode))
			throw new PermissionDeniedException("findLookupByCode: " + lookupCode);
		return lookupService.findLookupByCode(lookupCode);
	}

	@Override
	public Lookup updateLookupName(Identifier lookupId, Localized name) {
		if (!canUpdateLookup(lookupId))
			throw new PermissionDeniedException("updateLookupName: " + lookupId + ", " + name);
		return lookupService.updateLookupName(lookupId, validate(findLookup(lookupId).withName(name)).getName());
	}

	@Override
	public Lookup enableLookup(Identifier lookupId) {
		if (!canEnableLookup(lookupId))
			throw new PermissionDeniedException("enableLookup: " + lookupId);
		return lookupService.enableLookup(
				validate(lookupService.findLookup(lookupId).withStatus(Status.ACTIVE)).getLookupId());
	}

	@Override
	public Lookup disableLookup(Identifier lookupId) {
		if (!canDisableLookup(lookupId))
			throw new PermissionDeniedException("disableLookup: " + lookupId);
		return lookupService.disableLookup(
				validate(lookupService.findLookup(lookupId).withStatus(Status.INACTIVE)).getLookupId());
	}

	@Override
	public FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging) {
		return lookupService.findLookups(filter, paging);
	}

	public Option validate(Option option) {
		List<Message> messages = validation.validate(option);
		if (!messages.isEmpty())
			throw new BadRequestException("Organization has validation errors", messages);
		return option;
	}

	@Override
	public Option createOption(Identifier lookupId, Localized name) {
		if (!canCreateOption(lookupId))
			throw new PermissionDeniedException("createOption: " + lookupId + ", " + name);
		return optionService.createOption(validate(prototypeOption(lookupId, name)));
	}

	@Override
	public Option findOption(Identifier optionId) {
		if (!canViewOption(optionId))
			throw new PermissionDeniedException("findOption: " + optionId);
		return optionService.findOption(optionId);
	}

	@Override
	public Option findOptionByCode(Identifier lookupId, String optionCode) {
		if (!canViewOption(lookupId, optionCode))
			throw new PermissionDeniedException("findOptionByCode: " + lookupId + ", " + optionCode);
		return optionService.findOptionByCode(lookupId, optionCode);
	}

	@Override
	public Option updateOptionName(Identifier optionId, Localized name) {
		if (!canUpdateOption(optionId))
			throw new PermissionDeniedException("updateOptionName: " + optionId + ", " + name);
		return optionService.updateOptionName(optionId, validate(findOption(optionId).withName(name)).getName());
	}

	@Override
	public Option enableOption(Identifier optionId) {
		if (!canEnableOption(optionId))
			throw new PermissionDeniedException("enableOption: " + optionId);
		return optionService.enableOption(
				validate(optionService.findOption(optionId).withStatus(Status.ACTIVE)).getOptionId());
	}

	@Override
	public Option disableOption(Identifier optionId) {
		if (!canDisableOption(optionId))
			throw new PermissionDeniedException("disableOption: " + optionId);
		return optionService.disableOption(
				validate(optionService.findOption(optionId).withStatus(Status.INACTIVE)).getOptionId());
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		return optionService.findOptions(filter, paging);
	}
	
}
