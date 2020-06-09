package ca.magex.crm.caching;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CachingOrganizationService implements CrmOrganizationService {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private CrmOrganizationService delegate;
	private CacheManager cacheManager;
		
	public CachingOrganizationService(CrmOrganizationService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}
	
	@Override
	@Caching( put = {
			@CachePut(cacheNames = "organizations", key="'Details_'.concat(#result.organizationId)", unless="#result == null"),
			@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#result.organizationId)", unless="#result == null")
	})
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		LOG.debug("createOrganization(" + displayName + "," + groups + ")");
		return delegate.createOrganization(displayName, groups);
	}

	@Override
	@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	@CacheEvict(cacheNames = "organizations", key="'Details_'.concat(#result.organizationId)")
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		LOG.debug("enableOrganization(" + organizationId + ")");
		return delegate.enableOrganization(organizationId);
	}

	@Override
	@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	@CacheEvict(cacheNames = "organizations", key="'Details_'.concat(#result.organizationId)")
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		LOG.debug("disableOrganization(" + organizationId + ")");
		return delegate.disableOrganization(organizationId);
	}

	@Override
	@Caching( put = {
			@CachePut(cacheNames = "organizations", key="'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		LOG.debug("updateOrganizationDisplayName(" + organizationId + "," + name + ")");
		return delegate.updateOrganizationDisplayName(organizationId, name);
	}

	@Override
	@Caching( put = {
			@CachePut(cacheNames = "organizations", key="'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		LOG.debug("updateOrganizationMainLocation(" + organizationId + "," + locationId + ")");
		return delegate.updateOrganizationMainLocation(organizationId, locationId);
	}

	@Override
	@Caching( put = {
			@CachePut(cacheNames = "organizations", key="'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		LOG.debug("updateOrganizationMainContact(" + organizationId + "," + personId + ")");
		return delegate.updateOrganizationMainContact(organizationId, personId);
	}

	@Override
	@Caching( put = {
			@CachePut(cacheNames = "organizations", key="'Details_'.concat(#organizationId)"),
			@CachePut(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	})
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		LOG.debug("updateOrganizationGroups(" + organizationId + "," + groups + ")");
		return delegate.updateOrganizationGroups(organizationId, groups);
	}

	@Override
	@Cacheable(cacheNames = "organizations", key="'Summary_'.concat(#organizationId)")
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		LOG.debug("findOrganizationSummary(" + organizationId + ")");
		return delegate.findOrganizationSummary(organizationId);
	}
	
	@Override
	@Cacheable(cacheNames = "organizations", key="'Details_'.concat(#organizationId)")
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {		
		LOG.debug("findOrganizationDetails(" + organizationId + ")");
		return delegate.findOrganizationDetails(organizationId);
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		LOG.debug("countOrganizations(" + filter + ")");
		return delegate.countOrganizations(filter);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		LOG.debug("findOrganizationDetails(" + filter + "," + paging + ")");
		FilteredPage<OrganizationDetails> page = delegate.findOrganizationDetails(filter, paging);
		Cache organizationsCache = cacheManager.getCache("organizations");
		page.forEach((details) -> {
			organizationsCache.putIfAbsent("Details_" + details.getOrganizationId(), details);
			organizationsCache.putIfAbsent("Summary_" + details.getOrganizationId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		LOG.debug("findOrganizationSummaries(" + filter + "," + paging + ")");
		FilteredPage<OrganizationSummary> page = delegate.findOrganizationSummaries(filter, paging);
		Cache organizationsCache = cacheManager.getCache("organizations");		
		page.forEach((summary) -> {
			organizationsCache.putIfAbsent("Summary_" + summary.getOrganizationId(), summary);
		});
		return page;
	}
}