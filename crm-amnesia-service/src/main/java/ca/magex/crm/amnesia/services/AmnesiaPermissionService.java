package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import ca.magex.crm.api.system.Lang;
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
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		List<Group> allMatchingGroups = db.findByType(Group.class)
			.filter(g -> filter.getCode() == null || StringUtils.equalsIgnoreCase(filter.getCode(), g.getCode()))
			.filter(g -> filter.getEnglishName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.ENGLISH),filter.getEnglishName()))
			.filter(g -> filter.getFrenchName() == null || StringUtils.containsIgnoreCase(g.getName(Lang.FRENCH),filter.getFrenchName()))
			.filter(g -> filter.getStatus() == null || filter.getStatus().equals(g.getStatus()))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingGroups, paging);
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
	public Group createGroup(Localized name) {
		return db.saveGroup(validate(new Group(db.generateId(), Status.ACTIVE, name)));
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return db.saveGroup(validate(db.findGroup(groupId).withName(name)));
	}
	
	@Override
	public Group enableGroup(Identifier groupId) {
		return db.saveGroup(validate(findGroup(groupId).withStatus(Status.ACTIVE)));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		return db.saveGroup(validate(findGroup(groupId).withStatus(Status.INACTIVE)));
	}

	private Group validate(Group group) {
		return db.getValidation().validate(group);
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		List<Role> allRoles = db.findByType(Role.class)
			.filter(r -> filter.getGroupId() == null || filter.getGroupId().equals(r.getGroupId()))
			.filter(r -> filter.getCode() == null || StringUtils.equalsIgnoreCase(filter.getCode(), r.getCode()))
			.filter(r -> filter.getEnglishName() == null || StringUtils.containsIgnoreCase(r.getName(Lang.ENGLISH),filter.getEnglishName()))
			.filter(r -> filter.getFrenchName() == null || StringUtils.containsIgnoreCase(r.getName(Lang.FRENCH),filter.getFrenchName()))
			.filter(r -> filter.getStatus() == null || filter.getStatus().equals(r.getStatus()))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allRoles, paging);
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
	public Role createRole(Identifier groupId, Localized name) {
		return db.saveRole(validate(new Role(db.generateId(), groupId, Status.ACTIVE, name)));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return db.saveRole(validate(db.findRole(roleId).withName(name)));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		return db.saveRole(validate(findRole(roleId).withStatus(Status.ACTIVE)));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		return db.saveRole(validate(findRole(roleId).withStatus(Status.INACTIVE)));
	}

	private Role validate(Role role) {
		return db.getValidation().validate(role);
	}

}