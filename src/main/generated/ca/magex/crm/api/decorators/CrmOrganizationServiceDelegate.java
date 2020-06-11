package ca.magex.crm.api.decorators;

import ca.magex.crm.api.services.CrmOrganizationService;

import java.util.List;
import javax.validation.constraints.NotNull;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmOrganizationServiceDelegate implements CrmOrganizationService {
	
	private CrmOrganizationService delegate;
	
	public CrmOrganizationServiceDelegate(CrmOrganizationService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public OrganizationDetails prototypeOrganization(String displayName, List<String> groups) {
		return delegate.prototypeOrganization(displayName, groups);
	}
	
	@Override
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {
		return delegate.createOrganization(prototype);
	}
	
	@Override
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		return delegate.createOrganization(displayName, groups);
	}
	
	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		return delegate.enableOrganization(organizationId);
	}
	
	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		return delegate.disableOrganization(organizationId);
	}
	
	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return delegate.updateOrganizationDisplayName(organizationId, name);
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return delegate.updateOrganizationMainLocation(organizationId, locationId);
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		return delegate.updateOrganizationMainContact(organizationId, personId);
	}
	
	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		return delegate.updateOrganizationGroups(organizationId, groups);
	}
	
	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return delegate.findOrganizationSummary(organizationId);
	}
	
	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return delegate.findOrganizationDetails(organizationId);
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return delegate.countOrganizations(filter);
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		return delegate.findOrganizationDetails(filter, paging);
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		return delegate.findOrganizationSummaries(filter, paging);
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		return delegate.findOrganizationDetails(filter);
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		return delegate.findOrganizationSummaries(filter);
	}
	
	@Override
	public OrganizationsFilter defaultOrganizationsFilter() {
		return delegate.defaultOrganizationsFilter();
	}
	
}
