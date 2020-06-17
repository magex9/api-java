package ca.magex.crm.api.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public interface CrmPermissionService {

	default Group prototypeGroup(
		@NotNull Localized name
	) {
		return new Group(null, Status.PENDING, name);
	}
	
	default Group createGroup(
		@NotNull Group group
	) {
		return createGroup(group.getName());
	}
	
	Group createGroup(
		@NotNull Localized name
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

	default GroupsFilter defaultGroupsFilter() {
		return new GroupsFilter();
	};
	
	FilteredPage<Group> findGroups(
		@NotNull GroupsFilter filter,
		@NotNull Paging paging
	);
	
	default FilteredPage<Group> findGroups(@NotNull GroupsFilter filter) {
		return findGroups(filter, defaultGroupPaging());
	}
	
	default List<String> findActiveGroupCodes() {
		return findGroups(
			defaultGroupsFilter().withStatus(Status.ACTIVE), 
			GroupsFilter.getDefaultPaging().allItems()
		).stream().map(g -> g.getCode()).collect(Collectors.toList());
	}
	
	default Role prototypeRole(
		@NotNull Identifier groupId, 
		@NotNull Localized name
	) {
		return new Role(null, groupId, Status.PENDING, name);
	}
	
	default Role createRole(
		@NotNull Role role
	) {
		return createRole(role.getGroupId(), role.getName());
	}
	
	Role createRole(
			@NotNull Identifier groupId, 
			@NotNull Localized name
		);

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
	
	FilteredPage<Role> findRoles(
		@NotNull RolesFilter filter, 
		@NotNull Paging paging
	);
	
	default FilteredPage<Role> findRoles(@NotNull RolesFilter filter) {
		return findRoles(filter, defaultRolePaging());
	}
	
	default List<Role> findRoles() {
		return findRoles(
			defaultRolesFilter(), 
			RolesFilter.getDefaultPaging().allItems()
		).getContent();
	}
	
	default List<String> findActiveRoleCodesForGroup(String group) {
		return findRoles(
			defaultRolesFilter()
				.withStatus(Status.ACTIVE)
				.withGroupId(findGroupByCode(group).getGroupId()), 
			RolesFilter.getDefaultPaging().allItems()
		).stream().map(r -> r.getCode()).collect(Collectors.toList());
	}
	
	default RolesFilter defaultRolesFilter() {
		return new RolesFilter();
	};
	
	default Paging defaultGroupPaging() {
		return new Paging(GroupsFilter.getSortOptions().get(0));
	}
	
	default Paging defaultRolePaging() {
		return new Paging(RolesFilter.getSortOptions().get(0));
	}

}