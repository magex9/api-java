package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Service("PrincipalPermissionService")
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaPermissionService implements CrmPermissionService {

	private AmnesiaDB db;

	public AmnesiaPermissionService(AmnesiaDB db) {
		this.db = db;
	}

	@Override
	public Group createGroup(Localized name) {
		return db.saveGroup(new Group(db.generateId(), Status.ACTIVE, name));
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return db.findById(groupId, Group.class);
	}

	@Override
	public Group findGroupByCode(String code) {
		return db.findGroupByCode(code);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		Group group = db.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return db.saveGroup(group.withName(name));
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		Group group = db.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return db.saveGroup(group.withStatus(Status.ACTIVE));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		Group group = db.findGroup(groupId);
		if (group == null) {
			return null;
		}
		return db.saveGroup(group.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		List<Group> allMatchingGroups = db.findByType(Group.class)
				.filter(g -> filter.apply(g))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingGroups, paging);
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		return db.saveRole(new Role(db.generateId(), groupId, Status.ACTIVE, name));
	}

	@Override
	public Role findRole(Identifier roleId) {
		return db.findById(roleId, Role.class);
	}

	@Override
	public Role findRoleByCode(String code) {
		return db.findRoleByCode(code);
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		Role role = db.findRole(roleId);
		if (role == null) {
			return null;
		}
		return db.saveRole(role.withName(name));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		Role role = db.findRole(roleId);
		if (role == null) {
			return null;
		}
		return db.saveRole(role.withStatus(Status.ACTIVE));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		Role role = db.findRole(roleId);
		if (role == null) {
			return null;
		}
		return db.saveRole(role.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		List<Role> allRoles = db.findByType(Role.class)
				.filter(r -> filter.apply(r))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allRoles, paging);
	}
}