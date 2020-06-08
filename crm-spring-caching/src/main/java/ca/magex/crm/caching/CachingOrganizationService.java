package ca.magex.crm.caching;

import java.util.List;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

@Component("caching")
public class CachingOrganizationService implements CrmOrganizationService {
	
	private CrmOrganizationService delegate;
	
	public CachingOrganizationService(CrmOrganizationService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	@CachePut(cacheNames = "organizations", key="#result.organizationId")
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		System.out.println("createOrganization(" + displayName + "," + groups + ")");
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
	@Cacheable(cacheNames = "organizations", key="#organizationId")
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		System.out.println("findOrganizationDetails(" + organizationId + ")");
		try {
			return delegate.findOrganizationDetails(organizationId);
		}
		catch(ItemNotFoundException ine) {
			return null;
		}
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
		return findOrganizationSummaries(filter, paging);
	}
}