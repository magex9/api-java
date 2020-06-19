package ca.magex.crm.api.services;

import java.util.List;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public interface CrmRoleService {

	default Role prototypeRole(
		Identifier groupId, 
		Localized name
	) {
		return new Role(null, groupId, Status.PENDING, name);
	}
	
	default Role createRole(
		Role role
	) {
		return createRole(role.getGroupId(), role.getName());
	}
	
	Role createRole(
			Identifier groupId, 
			Localized name
		);

	Role findRole(
		Identifier roleId
	);

	default Role findRoleByCode(
		String code
	) {
		return findRoles(
			defaultRolesFilter().withCode(code), 
			RolesFilter.getDefaultPaging()
		).getSingleItem();
	};

	Role updateRoleName(
		Identifier roleId, 
		Localized name
	);

	Role enableRole(
		Identifier roleId
	);

	Role disableRole(
		Identifier roleId
	);
	
	FilteredPage<Role> findRoles(
		RolesFilter filter, 
		Paging paging
	);
	
	default FilteredPage<Role> findRoles(RolesFilter filter) {
		return findRoles(filter, defaultRolePaging());
	}
	
	default List<Role> findRoles() {
		return findRoles(
			defaultRolesFilter(), 
			RolesFilter.getDefaultPaging().allItems()
		).getContent();
	}
	
	default RolesFilter defaultRolesFilter() {
		return new RolesFilter();
	};
	
	default Paging defaultRolePaging() {
		return new Paging(RolesFilter.getSortOptions().get(0));
	}

}