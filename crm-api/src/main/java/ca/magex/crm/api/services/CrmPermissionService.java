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
	
	default GroupsFilter defaultGroupsFilter() {
		return new GroupsFilter();
	};
	
	FilteredPage<Group> findGroups(
		@NotNull GroupsFilter filter,
		@NotNull Paging paging
	);
	
	Group findGroup(
		@NotNull Identifier groupId
	);

	default Group findGroupByCode(
		@NotNull String code
	) {
		return findGroups(
			defaultGroupsFilter().withCode(code), 
			GroupsFilter.getDefaultPaging()
		).getSingleItem();
	};

	Group createGroup(
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
	
	Role findRole(
		@NotNull Identifier roleId
	);

	default Role findRoleByCode(
		@NotNull String code
	) {
		return findRoles(
			defaultRolesFilter().withCode(code), 
			RolesFilter.getDefaultPaging()
		).getSingleItem();
	};

	Role createRole(
		@NotNull Identifier groupId, 
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