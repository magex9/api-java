package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PermissionsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPermissionService implements CrmPermissionService {
	
	private AmnesiaDB db;
	
	public AmnesiaPermissionService(AmnesiaDB db) {
		this.db = db;
	}

	@Override
	public List<Group> findGroups() {
		return db.findByType(Group.class).collect(Collectors.toList());
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return db.findById(groupId, Group.class);
	}

	@Override
	public Group createGroup(Localized name) {
		return db.saveGroup(new Group(db.generateId(), Status.ACTIVE, name));
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return db.saveGroup(db.findGroup(groupId).withName(name));
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		return db.saveGroup(findGroup(groupId).withStatus(Status.ACTIVE));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		return db.saveGroup(findGroup(groupId).withStatus(Status.INACTIVE));
	}

	@Override
	public List<Role> findRoles(Identifier groupId) {
		return db.findByType(Role.class)
			.filter(r -> r.getGroupId().equals(groupId))
			.collect(Collectors.toList());
	}

	@Override
	public Role findRole(Identifier roleId) {
		return db.findById(roleId, Role.class);
	}

	@Override
	public Role findRoleByCode(String code) {
		return findRole(new Identifier(code));
	}

	@Override
	public Role createRole(Identifier groupId, String code, Localized name) {
		return db.saveRole(new Role(db.generateId(), groupId, code, Status.ACTIVE, name));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return db.saveRole(db.findRole(roleId).withName(name));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		return db.saveRole(findRole(roleId).withStatus(Status.ACTIVE));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		return db.saveRole(findRole(roleId).withStatus(Status.INACTIVE));
	}
	
	public Stream<Permission> apply(PermissionsFilter filter) {
		return db.findByType(Permission.class)
			.filter(p -> filter.getUserId() != null ? p.getUserId().equals(filter.getUserId()) : true)
			.filter(p -> filter.getRoleId() != null ? p.getRoleId().equals(filter.getRoleId()) : true)
			.filter(p -> filter.getStatus() != null ? p.getStatus().equals(filter.getStatus()) : true);
	}

	@Override
	public long countPermissions(PermissionsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public Page<Permission> findPermissions(PermissionsFilter filter, Paging paging) {
		List<Permission> allMatchingOrgs = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

}