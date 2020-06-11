package ca.magex.crm.api.decorators;

import ca.magex.crm.api.services.CrmPermissionService;

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

public class CrmPermissionServiceDelegate implements CrmPermissionService {
	
	private CrmPermissionService delegate;
	
	public CrmPermissionServiceDelegate(CrmPermissionService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public Group prototypeGroup(Localized name) {
		return delegate.prototypeGroup(name);
	}
	
	@Override
	public Group createGroup(Group group) {
		return delegate.createGroup(group);
	}
	
	@Override
	public Group createGroup(Localized name) {
		return delegate.createGroup(name);
	}
	
	@Override
	public Group findGroup(Identifier groupId) {
		return delegate.findGroup(groupId);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		return delegate.findGroupByCode(code);
	}
	
	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return delegate.updateGroupName(groupId, name);
	}
	
	@Override
	public Group enableGroup(Identifier groupId) {
		return delegate.enableGroup(groupId);
	}
	
	@Override
	public Group disableGroup(Identifier groupId) {
		return delegate.disableGroup(groupId);
	}
	
	@Override
	public GroupsFilter defaultGroupsFilter() {
		return delegate.defaultGroupsFilter();
	}
	
	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return delegate.findGroups(filter, paging);
	}
	
	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter) {
		return delegate.findGroups(filter);
	}
	
	@Override
	public List<String> findActiveGroupCodes() {
		return delegate.findActiveGroupCodes();
	}
	
	@Override
	public Role prototypeRole(Identifier groupId, Localized name) {
		return delegate.prototypeRole(groupId, name);
	}
	
	@Override
	public Role createRole(Role role) {
		return delegate.createRole(role);
	}
	
	@Override
	public Role createRole(Identifier groupId, Localized name) {
		return delegate.createRole(groupId, name);
	}
	
	@Override
	public Role findRole(Identifier roleId) {
		return delegate.findRole(roleId);
	}
	
	@Override
	public Role findRoleByCode(String code) {
		return delegate.findRoleByCode(code);
	}
	
	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return delegate.updateRoleName(roleId, name);
	}
	
	@Override
	public Role enableRole(Identifier roleId) {
		return delegate.enableRole(roleId);
	}
	
	@Override
	public Role disableRole(Identifier roleId) {
		return delegate.disableRole(roleId);
	}
	
	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		return delegate.findRoles(filter, paging);
	}
	
	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter) {
		return delegate.findRoles(filter);
	}
	
	@Override
	public List<Role> findRoles() {
		return delegate.findRoles();
	}
	
	@Override
	public List<String> findActiveRoleCodesForGroup(String group) {
		return delegate.findActiveRoleCodesForGroup(group);
	}
	
	@Override
	public RolesFilter defaultRolesFilter() {
		return delegate.defaultRolesFilter();
	}
	
	@Override
	public Paging defaultGroupPaging() {
		return delegate.defaultGroupPaging();
	}
	
	@Override
	public Paging defaultRolePaging() {
		return delegate.defaultRolePaging();
	}
	
}
