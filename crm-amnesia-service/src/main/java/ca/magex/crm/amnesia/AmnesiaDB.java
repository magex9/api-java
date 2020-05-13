package ca.magex.crm.amnesia;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.IdGenerator;
import ca.magex.crm.amnesia.services.AmnesiaOrganizationService;
import ca.magex.crm.amnesia.services.AmnesiaPasswordService;
import ca.magex.crm.amnesia.services.AmnesiaPermissionService;
import ca.magex.crm.amnesia.services.AmnesiaPersonService;
import ca.magex.crm.amnesia.services.AmnesiaUserService;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.resource.CrmRoleInitializer;

@Repository
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaDB implements CrmPasswordService {
	
	public static final String SYSTEM_ADMIN = "SYS_ADMIN";
	
	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String RE_ADMIN = "RE_ADMIN";
	
	private IdGenerator idGenerator;

	private Identifier systemId;
	
	private AmnesiaPasswordService passwords;
	
	private Map<Identifier, Serializable> data;
	
	private Map<String, Group> groupsByCode;
	
	private Map<String, Role> rolesByCode;
	
	private Map<String, User> usersByUsername;
	
	public AmnesiaDB(PasswordEncoder passwordEncoder) {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
		passwords = new AmnesiaPasswordService();
		groupsByCode = new HashMap<String, Group>();
		rolesByCode = new HashMap<String, Role>();
		usersByUsername = new HashMap<String, User>();
	}
	
	public boolean isInitialized() {
		return systemId != null;
	}
	
	public Identifier initialize(String organization, PersonName name, String email, String username, String password) {
		if (systemId == null) {
			CrmRoleInitializer.initialize(new AmnesiaPermissionService(this));
			Identifier organizationId = new AmnesiaOrganizationService(this).createOrganization(organization, List.of("SYS", "CRM")).getOrganizationId();
			Identifier personId = new AmnesiaPersonService(this).createPerson(organizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
			systemId = new AmnesiaUserService(this, new BCryptPasswordEncoder()).createUser(personId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN")).getUserId();
			passwords.generateTemporaryPassword(username);
			passwords.updatePassword(username, new BCryptPasswordEncoder().encode(password));
		}
		return systemId;
	}
	
	public void reset() {
		data = new HashMap<Identifier, Serializable>();
		passwords = new AmnesiaPasswordService();
	}
	
	public Identifier generateId() {
		return idGenerator.generate();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Stream<T> findByType(Class<T> cls) {
		return data.values().stream().filter(c -> c.getClass().equals(cls)).map(c -> (T)c);
	}

	public OrganizationDetails findOrganization(Identifier organizationId) {
		Serializable obj = data.get(organizationId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + organizationId);
		if (!(obj instanceof OrganizationDetails))
			throw new BadRequestException(organizationId, "error", "class", "Expected OrganizationDetails but got: " + obj.getClass().getName());
		return (OrganizationDetails)SerializationUtils.clone(obj);
	}

	public OrganizationDetails saveOrganization(OrganizationDetails organization) {
		data.put(organization.getOrganizationId(), organization);
		return organization;
	}

	public LocationDetails findLocation(Identifier locationId) {
		Serializable obj = data.get(locationId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + locationId);
		if (!(obj instanceof LocationDetails))
			throw new BadRequestException(locationId, "error", "class", "Expected LocationDetails but got: " + obj.getClass().getName());
		return (LocationDetails)SerializationUtils.clone(obj);
	}

	public LocationDetails saveLocation(LocationDetails location) {
		data.put(location.getLocationId(), location);
		return location;
	}

	public PersonDetails findPerson(Identifier personId) {
		Serializable obj = data.get(personId);
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + personId);
		if (!(obj instanceof PersonDetails))
			throw new BadRequestException(personId, "error", "class", "Expected PersonDetails but got: " + obj.getClass().getName());
		return (PersonDetails)SerializationUtils.clone(obj);
	}

	public PersonDetails savePerson(PersonDetails person) {
		data.put(person.getPersonId(), person);
		return person;
	}
	
	public User findUser(Identifier userId) {
		return (User)findById(userId, User.class);
	}	
	
	public User findUserByUsername(String username) {
		return usersByUsername.get(username);
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
			throw new ItemNotFoundException("Unable to find: " + identifier);
		if (!(cls.equals(obj.getClass())))
			throw new BadRequestException(identifier, "error", "class", "Expected " + cls.getName() + " but got: " + obj.getClass().getName());
		return (T)SerializationUtils.clone(obj);
	}
	
	@Override
	public String getEncodedPassword(String username) {
		return passwords.getEncodedPassword(username);
	}

	@Override
	public boolean isTempPassword(String username) {
		return passwords.isTempPassword(username);
	}

	@Override
	public boolean isExpiredPassword(String username) {
		return passwords.isExpiredPassword(username);
	}

	@Override
	public boolean verifyPassword(String username, String rawPassword) {
		return passwords.verifyPassword(username, rawPassword);
	}
	
	@Override
	public String generateTemporaryPassword(@NotNull String username) {
		return passwords.generateTemporaryPassword(username);
	}

	@Override
	public void updatePassword(String username, String encodedPassword) {
		passwords.updatePassword(username, encodedPassword);
	}
	
	public void dump() {
		data.keySet()
			.stream()
			.sorted((x, y) -> x.toString().compareTo(y.toString()))
			.forEach(key -> System.out.println(key + " => " + data.get(key).toString()));
	}
	
}