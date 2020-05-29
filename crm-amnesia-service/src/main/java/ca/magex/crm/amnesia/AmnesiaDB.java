package ca.magex.crm.amnesia;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.AmnesiaIdGenerator;
import ca.magex.crm.amnesia.services.AmnesiaInitializationService;
import ca.magex.crm.amnesia.services.AmnesiaLocationService;
import ca.magex.crm.amnesia.services.AmnesiaLookupService;
import ca.magex.crm.amnesia.services.AmnesiaOrganizationService;
import ca.magex.crm.amnesia.services.AmnesiaPasswordService;
import ca.magex.crm.amnesia.services.AmnesiaPermissionService;
import ca.magex.crm.amnesia.services.AmnesiaPersonService;
import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.amnesia.services.AmnesiaUserService;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.validation.StructureValidationService;
import ca.magex.crm.resource.CrmLookupLoader;
import ca.magex.crm.resource.CrmRoleInitializer;

@Repository
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaDB {

	private AmnesiaIdGenerator idGenerator;

	private Identifier systemId;
	
	private PasswordEncoder passwordEncoder;
	
	private StructureValidationService validation;

	private AmnesiaInitializationService initialization;
	
	private AmnesiaLookupService lookups;
	
	private AmnesiaPermissionService permissions;
	
	private AmnesiaOrganizationService organizations;
	
	private AmnesiaLocationService locations;
	
	private AmnesiaPersonService persons;
	
	private AmnesiaUserService users;
	
	private AmnesiaPasswordService passwords;

	private Map<String, PasswordDetails> passwordData;
	
	private Map<Identifier, Serializable> data;
	
	private Map<String, Group> groupsByCode;
	
	private Map<String, Role> rolesByCode;
	
	private Map<String, User> usersByUsername;
	
	private AmnesiaServices services;
	
	public AmnesiaDB(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		idGenerator = new AmnesiaBase58IdGenerator();
		lookups = new AmnesiaLookupService(new CrmLookupLoader()).initialize();
		initialization = new AmnesiaInitializationService(this);
		permissions = new AmnesiaPermissionService(this);
		organizations = new AmnesiaOrganizationService(this);
		locations = new AmnesiaLocationService(this);
		persons = new AmnesiaPersonService(this);
		users = new AmnesiaUserService(this);
		passwords = new AmnesiaPasswordService(this);
		services = new AmnesiaServices(this);
		validation = new StructureValidationService(lookups, permissions, organizations, locations, persons);
		data = new HashMap<Identifier, Serializable>();
		passwordData = new HashMap<String, PasswordDetails>();
		groupsByCode = new HashMap<String, Group>();
		rolesByCode = new HashMap<String, Role>();
		usersByUsername = new HashMap<String, User>();
	}
	
	public CrmServices getServices() {
		return services;
	}
	
	public CrmInitializationService getInitialization() {
		return initialization;
	}
	
	public CrmLookupService getLookups() {
		return lookups;
	}
	
	public AmnesiaPermissionService getPermissions() {
		return permissions;
	}
	
	public AmnesiaOrganizationService getOrganizations() {
		return organizations;
	}
	
	public AmnesiaLocationService getLocations() {
		return locations;
	}
	
	public AmnesiaPersonService getPersons() {
		return persons;
	}
	
	public AmnesiaUserService getUsers() {
		return users;
	}
	
	public StructureValidationService getValidation() {
		return validation;
	}
	
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}
	
	public boolean isInitialized() {
		return systemId != null;
	}
	
	public Identifier initialize(String organization, PersonName name, String email, String username, String password) {
		if (systemId == null) {
			CrmRoleInitializer.initialize(permissions);
			Identifier organizationId = organizations.createOrganization(organization, List.of("SYS", "CRM")).getOrganizationId();
			Identifier personId = persons.createPerson(organizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
			systemId = users.createUser(personId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN")).getUserId();			
			passwords.generateTemporaryPassword(username);
			passwords.updatePassword(username, passwordEncoder.encode(password));
		}
		return systemId;
	}
	
	public void reset() {
		data.clear();
		passwordData.clear();
		groupsByCode.clear();
		rolesByCode.clear();
		usersByUsername.clear();
		systemId = null;
	}
	
	public Identifier generateId() {
		return idGenerator.generate();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Stream<T> findByType(Class<T> cls) {
		return data.values().stream().filter(c -> c.getClass().equals(cls)).map(c -> (T)c);
	}
	
	public PasswordDetails findPassword(String username) {
		return passwordData.get(username);
	}
	
	public void savePassword(String username, PasswordDetails passwordDetails) {
		this.passwordData.put(username, passwordDetails);
	}
	
	public OrganizationDetails findOrganization(Identifier organizationId) {
		Serializable obj = data.get(organizationId);
		if (obj == null)
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		if (!(obj instanceof OrganizationDetails))
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		return (OrganizationDetails)SerializationUtils.clone(obj);
	}
	
	public OrganizationDetails saveOrganization(OrganizationDetails organization) {
		data.put(organization.getOrganizationId(), organization);
		return organization;
	}
	
	public LocationDetails findLocation(Identifier locationId) {
		Serializable obj = data.get(locationId);
		if (obj == null)
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		if (!(obj instanceof LocationDetails))
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		return (LocationDetails)SerializationUtils.clone(obj);
	}

	public LocationDetails saveLocation(LocationDetails location) {
		data.put(location.getLocationId(), location);
		return location;
	}

	public PersonDetails findPerson(Identifier personId) {
		Serializable obj = data.get(personId);
		if (obj == null)
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		if (!(obj instanceof PersonDetails))
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		return (PersonDetails)SerializationUtils.clone(obj);
	}

	public PersonDetails savePerson(PersonDetails person) {
		data.put(person.getPersonId(), person);
		return person;
	}
	
	public User findUser(Identifier userId) {
		Serializable obj = data.get(userId);
		if (obj == null)
			throw new ItemNotFoundException("User ID '" + userId + "'");
		if (!(obj instanceof User))
			throw new ItemNotFoundException("User ID '" + userId + "'");
		return (User)SerializationUtils.clone(obj);
	}
	
	public User saveUser(User user) {
		data.put(user.getUserId(), user);
		usersByUsername.put(user.getUsername(), user);
		return user;
	}

	public Group findGroup(Identifier groupId) {
		return (Group)findById(groupId, Group.class);
	}
	
	public Group findGroupByCode(String group) {
		if (!groupsByCode.containsKey(group))
			throw new ItemNotFoundException("Group Code '" + group + "'");
		return groupsByCode.get(group);
	}
	
	public Group saveGroup(Group group) {
		data.put(group.getGroupId(), group);
		groupsByCode.put(group.getCode(), group);
		return group;
	}
	
	public Role findRole(Identifier roleId) {
		return (Role)findById(roleId, Role.class);
	}
	
	public Role findRoleByCode(String role) {
		if (!rolesByCode.containsKey(role))
			throw new ItemNotFoundException("Role Code '" + role + "'");
		return rolesByCode.get(role);
	}
	
	public Role saveRole(Role role) {
		data.put(role.getRoleId(), role);
		rolesByCode.put(role.getCode(), role);
		return role;
	}	
	
	@SuppressWarnings("unchecked")
	public <T> T findById(Identifier identifier, Class<T> cls) {
		Serializable obj = data.get(identifier);
		if (obj == null)
			throw new ItemNotFoundException(cls.getSimpleName() + " ID '" + identifier + "'");
		if (!(cls.equals(obj.getClass())))
			throw new ItemNotFoundException(cls.getSimpleName() + " ID '" + identifier + "'");
		return (T)SerializationUtils.clone(obj);
	}
	
	public void dump() {
		dump(System.out);
	}
	
	public void dump(OutputStream os) {
		data.keySet()
			.stream()
			.sorted((x, y) -> x.toString().compareTo(y.toString()))
			.forEach(key -> {
				try {
					os.write(new String(key + " => " + data.get(key) + "\n").getBytes());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
	}
	
}