package ca.magex.crm.api.repositories;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmRoleRepository {
	
	public Identifier generateRoleId();

	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging); 

	public long countRoles(RolesFilter filter); 

	public Role findRole(Identifier roleId);

	public Role saveRole(Role role);
	
}
