package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
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
		return db.saveUser(new User(new Identifier(username), db.findPerson(personId), Status.ACTIVE));
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
	public List<Identifier> getRoles(Identifier userId) {
		return db.findPermissions(userId).stream()
			.filter(p -> p.getStatus().equals(Status.ACTIVE))
			.map(p -> db.findRole(p.getRoleId()).getRoleId())
			.collect(Collectors.toList());
	}

	@Override
	public User addUserRole(Identifier userId, Identifier roleId) {
		List<Permission> permissions = db.findPermissions(userId);
		if (!permissions.stream().anyMatch(p -> p.getRoleId().equals(roleId)))
			db.savePermission(new Permission(db.generateId(), userId, roleId, Status.ACTIVE));
		return db.findUser(userId);
	}

	@Override
	public User removeUserRole(Identifier userId, Identifier roleId) {
		List<Permission> permissions = db.findPermissions(userId).stream().filter(p -> p.getRoleId().equals(roleId)).collect(Collectors.toList());
		for (Permission permission : permissions) {
			db.savePermission(permission.withStatus(Status.INACTIVE));
		}
		return db.findUser(userId);
	}

	@Override
	public User setRoles(Identifier userId, List<Identifier> roleIds) {
		List<Identifier> updates = new ArrayList<Identifier>(roleIds);
		for (Permission permission : db.findPermissions(userId)) {
			if (updates.contains(permission.getRoleId())) {
				db.savePermission(permission.withStatus(Status.ACTIVE));
			} else {
				db.savePermission(permission.withStatus(Status.INACTIVE));
			}
			updates.remove(permission.getRoleId());
		}
		for (Identifier roleId : updates) {
			db.savePermission(new Permission(db.generateId(), userId, roleId, Status.ACTIVE));
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<User> findUsers(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
