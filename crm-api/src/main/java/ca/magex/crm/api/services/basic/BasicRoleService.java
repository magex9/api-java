package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class BasicRoleService implements CrmRoleService {

	private CrmRepositories repos;

	public BasicRoleService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		return repos.saveRole(new Role(repos.generateId(), groupId, Status.ACTIVE, name));
	}

	@Override
	public Role findRole(Identifier roleId) {
		return repos.findRole(roleId);
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		Role role = repos.findRole(roleId);
		if (role == null) {
			return null;
		}
		return repos.saveRole(role.withName(name));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		Role role = repos.findRole(roleId);
		if (role == null) {
			return null;
		}
		return repos.saveRole(role.withStatus(Status.ACTIVE));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		Role role = repos.findRole(roleId);
		if (role == null) {
			return null;
		}
		return repos.saveRole(role.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		return repos.findRoles(filter, paging);
	}
}