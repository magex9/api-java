package ca.magex.crm.api.services;

import javax.validation.constraints.NotNull;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public interface CrmPermissionService {

	FilteredPage<Group> findGroups(
		@NotNull GroupsFilter filter,
		@NotNull Paging paging
	);
	
	default GroupsFilter defaultGroupsFilter() {
		return new GroupsFilter();
	};
	
	default Paging defaultGroupsPaging() {
		return new Paging(GroupsFilter.getSortOptions().get(0));
	}

	Group findGroup(
		@NotNull Identifier groupId
	);

	Group findGroupByCode(
		@NotNull String code
	);

	Group createGroup(
		@NotNull String code, 
		@NotNull Localized name
	);

	Group updateGroupName(
		@NotNull Identifier groupId, 
		@NotNull Localized name
	);

	Group enableGroup(
		@NotNull Identifier groupId
	);

	Group disableGroup(
		@NotNull Identifier groupId
	);

	FilteredPage<Role> findRoles(
		@NotNull RolesFilter filter, 
		@NotNull Paging paging
	);
	
	default RolesFilter defaultRolesFilter() {
		return new RolesFilter();
	};
	
	default Paging defaultRolesPaging() {
		return new Paging(RolesFilter.SORT_OPTIONS.get(0));
	}

	Role findRole(
		@NotNull Identifier roleId
	);

	Role findRoleByCode(
		@NotNull String code
	);

	Role createRole(
		@NotNull Identifier groupId, 
		@NotNull String code, 
		@NotNull Localized name
	);

	Role updateRoleName(
		@NotNull Identifier roleId, 
		@NotNull Localized name
	);

	Role enableRole(
		@NotNull Identifier roleId
	);

	Role disableRole(
		@NotNull Identifier roleId
	);
}