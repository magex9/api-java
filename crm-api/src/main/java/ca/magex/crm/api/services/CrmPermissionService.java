package ca.magex.crm.api.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public interface CrmPermissionService {

	Page<Group> findGroups(
		@NotNull Paging paging
	);

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

	Page<Role> findRoles(
		@NotNull Identifier groupId, 
		@NotNull Paging paging
	);

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