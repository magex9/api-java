package ca.magex.crm.api.decorators;

/**
 * AUTO-GENERATED: This file is auto-generated by ca.magex.json.javadoc.JavadocDelegationBuilder
 * 
 * Logging and delegate decorators for the CRM services and policies
 * 
 * This delegate may be extended so that implementations can be kept clean if they don't need to implement every single field.
 * 
 * @author magex
 */
public class CrmGroupServiceDelegate implements ca.magex.crm.api.services.CrmGroupService {
	
	private ca.magex.crm.api.services.CrmGroupService delegate;
	
	public CrmGroupServiceDelegate(ca.magex.crm.api.services.CrmGroupService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public ca.magex.crm.api.roles.Group prototypeGroup(ca.magex.crm.api.system.Localized name) {
		return delegate.prototypeGroup(name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group createGroup(ca.magex.crm.api.roles.Group group) {
		return delegate.createGroup(group);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group createGroup(ca.magex.crm.api.system.Localized name) {
		return delegate.createGroup(name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group findGroup(ca.magex.crm.api.system.Identifier groupId) {
		return delegate.findGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group findGroupByCode(String code) {
		return delegate.findGroupByCode(code);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group updateGroupName(ca.magex.crm.api.system.Identifier groupId, ca.magex.crm.api.system.Localized name) {
		return delegate.updateGroupName(groupId, name);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group enableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return delegate.enableGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group disableGroup(ca.magex.crm.api.system.Identifier groupId) {
		return delegate.disableGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.filters.GroupsFilter defaultGroupsFilter() {
		return delegate.defaultGroupsFilter();
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Group> findGroups(ca.magex.crm.api.filters.GroupsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return delegate.findGroups(filter, paging);
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Group> findGroups(ca.magex.crm.api.filters.GroupsFilter filter) {
		return delegate.findGroups(filter);
	}
	
	@Override
	public java.util.List<String> findActiveGroupCodes() {
		return delegate.findActiveGroupCodes();
	}
	
	@Override
	public ca.magex.crm.api.filters.Paging defaultGroupPaging() {
		return delegate.defaultGroupPaging();
	}
	
}
