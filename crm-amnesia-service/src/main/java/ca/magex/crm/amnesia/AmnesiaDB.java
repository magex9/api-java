package ca.magex.crm.amnesia;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ca.magex.crm.amnesia.generator.AmnesiaBase58IdGenerator;
import ca.magex.crm.amnesia.generator.IdGenerator;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;

@Repository
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaDB implements CrmPasswordService {
	
	public static final String SYSTEM_ADMIN = "SYS_ADMIN";
	
	public static final String CRM_ADMIN = "CRM_ADMIN";

	public static final String RE_ADMIN = "RE_ADMIN";
	
	private IdGenerator idGenerator;
	
	private Map<Identifier, Serializable> data;
	
	private Map<String, String> passwords;
	
	public AmnesiaDB(PasswordEncoder passwordEncoder) {
		idGenerator = new AmnesiaBase58IdGenerator();
		data = new HashMap<Identifier, Serializable>();
		passwords = new HashMap<String, String>();
	}
	
	public void reset() {
		data = new HashMap<Identifier, Serializable>();
		passwords = new HashMap<String, String>();
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
	
	public User findUser(String username) {
		Serializable obj = data.get(new Identifier(username));
		if (obj == null)
			throw new ItemNotFoundException("Unable to find: " + username);
		if (!(obj instanceof User))
			throw new BadRequestException(new Identifier(username), "error", "class", "Expected User but got: " + obj.getClass().getName());
		return (User)SerializationUtils.clone(obj);
	}
	
	public User findUser(Identifier userId) {
		return (User)findById(userId, User.class);
	}	
	
	public User saveUser(User user) {
		data.put(user.getUserId(), user);
		return user;
	}
	
	public Group findGroup(Identifier groupId) {
		return (Group)findById(groupId, Group.class);
	}
	
	public Group saveGroup(Group group) {
		data.put(group.getGroupId(), group);
		return group;
	}
	
	public Role findRole(Identifier roleId) {
		return (Role)findById(roleId, Role.class);
	}
	
	public Role saveRole(Role role) {
		data.put(role.getRoleId(), role);
		return role;
	}
	
	public Permission findPermission(Identifier roleId) {
		return (Permission)findById(roleId, Permission.class);
	}
	
	public Permission savePermission(Permission permission) {
		data.put(permission.getPermissionId(), permission);
		return permission;
	}
	
	public List<Permission> findPermissions(Identifier userId) {
		return findByType(Permission.class)
			.filter(p -> p.getUserId().equals(userId))
			.collect(Collectors.toList());
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
		return passwords.get(username);
	}

	@Override
	public boolean isTempPassword(String username) {
		return false;
	}

	@Override
	public boolean isExpiredPassword(String username) {
		return false;
	}

	@Override
	public boolean verifyPassword(String username, String encodedPassword) {
		return passwords.get(username).equals(encodedPassword);
	}

	@Override
	public boolean updatePassword(String username, String password) {
		/* only store the encoded password */
		passwords.put(username, password);
		return true;
	}
}