package ca.magex.crm.caching;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

@Service("CachingOrganizationService")
public class CachingOrganizationServiceDelegate implements CrmOrganizationService {

	private CrmOrganizationService delegate;
	private CacheManager cacheManager;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public CachingOrganizationServiceDelegate(CrmOrganizationService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}
	
	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#result.organizationId)", unless = "#result == null"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#result.organizationId)", unless = "#result == null")
	})
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {
		return delegate.createOrganization(prototype);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#result.organizationId)", unless = "#result == null"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#result.organizationId)", unless = "#result == null")
	})
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		return delegate.createOrganization(displayName, groups);
	}

	@Override
	@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	@CacheEvict(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)")
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		return delegate.enableOrganization(organizationId);
	}

	@Override
	@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	@CacheEvict(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)")
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		return delegate.disableOrganization(organizationId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return delegate.updateOrganizationDisplayName(organizationId, name);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return delegate.updateOrganizationMainLocation(organizationId, locationId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		return delegate.updateOrganizationMainContact(organizationId, personId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		return delegate.updateOrganizationGroups(organizationId, groups);
	}

	@Override
	@Cacheable(cacheNames = "organizations", key = "'Summary_'.concat(#organizationId)")
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return delegate.findOrganizationSummary(organizationId);
	}

	@Override
	@Cacheable(cacheNames = "organizations", key = "'Details_'.concat(#organizationId)")
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return delegate.findOrganizationDetails(organizationId);
	}
	
	

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return delegate.countOrganizations(filter);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		FilteredPage<OrganizationDetails> page = delegate.findOrganizationDetails(filter, paging);
		Cache organizationsCache = cacheManager.getCache("organizations");
		page.forEach((details) -> {
			organizationsCache.putIfAbsent("Details_" + details.getOrganizationId(), details);
			organizationsCache.putIfAbsent("Summary_" + details.getOrganizationId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		FilteredPage<OrganizationDetails> page = delegate.findOrganizationDetails(filter);
		Cache organizationsCache = cacheManager.getCache("organizations");
		page.forEach((details) -> {
			organizationsCache.putIfAbsent("Details_" + details.getOrganizationId(), details);
			organizationsCache.putIfAbsent("Summary_" + details.getOrganizationId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		FilteredPage<OrganizationSummary> page = delegate.findOrganizationSummaries(filter, paging);
		Cache organizationsCache = cacheManager.getCache("organizations");
		page.forEach((summary) -> {
			organizationsCache.putIfAbsent("Summary_" + summary.getOrganizationId(), summary);
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(@NotNull OrganizationsFilter filter) {
		FilteredPage<OrganizationSummary> page = delegate.findOrganizationSummaries(filter);
		Cache organizationsCache = cacheManager.getCache("organizations");
		page.forEach((summary) -> {
			organizationsCache.putIfAbsent("Summary_" + summary.getOrganizationId(), summary);
		});
		return page;
	}
}