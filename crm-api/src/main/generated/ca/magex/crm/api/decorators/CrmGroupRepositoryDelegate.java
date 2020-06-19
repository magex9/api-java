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
public class CrmGroupRepositoryDelegate implements ca.magex.crm.api.repositories.CrmGroupRepository {
	
	private ca.magex.crm.api.repositories.CrmGroupRepository delegate;
	
	public CrmGroupRepositoryDelegate(ca.magex.crm.api.repositories.CrmGroupRepository delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public ca.magex.crm.api.system.FilteredPage<ca.magex.crm.api.roles.Group> findGroups(ca.magex.crm.api.filters.GroupsFilter filter, ca.magex.crm.api.filters.Paging paging) {
		return delegate.findGroups(filter, paging);
	}
	
	@Override
	public long countGroups(ca.magex.crm.api.filters.GroupsFilter filter) {
		return delegate.countGroups(filter);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group findGroup(ca.magex.crm.api.system.Identifier groupId) {
		return delegate.findGroup(groupId);
	}
	
	@Override
	public ca.magex.crm.api.roles.Group saveGroup(ca.magex.crm.api.roles.Group group) {
		return delegate.saveGroup(group);
	}
	
}
