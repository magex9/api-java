package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaUserService implements CrmUserService {

	private AmnesiaDB db;
	
	private PasswordEncoder passwordEncoder;

	public AmnesiaUserService(AmnesiaDB db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		User user = db.saveUser(new User(db.generateId(), username, db.findPerson(personId), Status.ACTIVE));
		setRoles(user.getUserId(), roles);
		return user;
	}

	@Override
	public User enableUser(Identifier userId) {
		return db.saveUser(db.findUser(userId).withStatus(Status.ACTIVE));
	}

	@Override
	public User disableUser(Identifier userId) {
		return db.saveUser(db.findUser(userId).withStatus(Status.INACTIVE));
	}

	@Override
	public User findUser(Identifier userId) {
		return db.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		return db.findByType(User.class).filter(u -> u.getUsername().equals(username)).findAny().get();
	}
	
	@Override
	public List<String> getRoles(Identifier userId) {
		return db.findUserRoles(db.findUser(userId).getUsername());
	}

	@Override
	public User addUserRole(Identifier userId, String role) {
		Identifier roleId = db.findRoleByCode(role).getRoleId();
		List<Permission> permissions = db.findPermissions(userId);
		if (!permissions.stream().anyMatch(p -> p.getRoleId().equals(roleId)))
			db.savePermission(new Permission(db.generateId(), userId, roleId, Status.ACTIVE));
		return db.findUser(userId);
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		Identifier roleId = db.findRoleByCode(role).getRoleId();
		List<Permission> permissions = db.findPermissions(userId).stream().filter(p -> p.getRoleId().equals(roleId)).collect(Collectors.toList());
		for (Permission permission : permissions) {
			db.savePermission(permission.withStatus(Status.INACTIVE));
		}
		return db.findUser(userId);
	}

	@Override
	public User setRoles(Identifier userId, List<String> roles) {
		List<String> updates = new ArrayList<String>(roles);
		for (Permission permission : db.findPermissions(userId)) {
			String role = db.findRole(permission.getRoleId()).getCode();
			if (updates.contains(role)) {
				db.savePermission(permission.withStatus(Status.ACTIVE));
			} else {
				db.savePermission(permission.withStatus(Status.INACTIVE));
			}
			updates.remove(role);
		}
		for (String role : updates) {
			db.savePermission(new Permission(db.generateId(), userId, db.findRoleByCode(role).getRoleId(), Status.ACTIVE));
		}
		return db.findUser(userId);
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		db.updatePassword(userId.toString(), passwordEncoder.encode(newPassword));
		return true;
	}

	@Override
	public boolean resetPassword(Identifier userId) {
		db.updatePassword(userId.toString(), passwordEncoder.encode(db.generateId().toString()));
		return true;
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return applyFilter(filter).count();
	}

	@Override
	public Page<User> findUsers(UsersFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(applyFilter(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}
	
	private Stream<User> applyFilter(UsersFilter filter) {
		return db.findByType(User.class)
			.filter(user -> StringUtils.isNotBlank(filter.getRole()) ? getRoles(user.getUserId()).contains(filter.getRole()) : true)
			.filter(user -> filter.getStatus() != null ? filter.getStatus().equals(user.getStatus()) : true)
			.filter(user -> filter.getPersonId() != null ? filter.getPersonId().equals(user.getPerson().getPersonId()) : true)
			.filter(user -> filter.getOrganizationId() != null ? filter.getOrganizationId().equals(user.getPerson().getOrganizationId()) : true);
	}
	
}
