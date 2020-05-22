package ca.magex.crm.test.restful;

import javax.validation.constraints.NotNull;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public class RestfulPermissionService implements CrmPermissionService {

	@Override
	public FilteredPage<Group> findGroups(@NotNull GroupsFilter filter, @NotNull Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group findGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group createGroup(@NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group updateGroupName(@NotNull Identifier groupId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group enableGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group disableGroup(@NotNull Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<Role> findRoles(@NotNull RolesFilter filter, @NotNull Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role findRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role createRole(@NotNull Identifier groupId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role updateRoleName(@NotNull Identifier roleId, @NotNull Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role enableRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role disableRole(@NotNull Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

}
