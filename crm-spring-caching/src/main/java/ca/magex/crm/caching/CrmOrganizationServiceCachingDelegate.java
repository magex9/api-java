package ca.magex.crm.caching;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmOrganizationServiceCachingDelegate implements CrmOrganizationService {

	private CrmOrganizationService delegate;
	private CacheTemplate cacheTemplate;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmOrganizationServiceCachingDelegate(CrmOrganizationService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}

	/**
	 * Provides the list of pairs for caching organization details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> detailsCacheSupplier(OrganizationDetails details, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.generateDetailsKey(key), details),
				Pair.of(CrmCacheKeyGenerator.generateSummaryKey(key), details == null ? null : details.asSummary()));
	}

	/**
	 * Provides the list of pairs for caching organization summary
	 * @param summary
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(OrganizationSummary summary, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.generateSummaryKey(key), summary));
	}

	@Override
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {
		OrganizationDetails details = delegate.createOrganization(prototype);
		cacheTemplate.put(detailsCacheSupplier(details, details.getOrganizationId()));
		return details;
	}

	@Override
	public OrganizationDetails createOrganization(String displayName, List<Identifier> groupIds) {
		OrganizationDetails details = delegate.createOrganization(displayName, groupIds);
		cacheTemplate.put(detailsCacheSupplier(details, details.getOrganizationId()));
		return details;
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		OrganizationSummary summary = delegate.enableOrganization(organizationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.generateDetailsKey(organizationId));
		cacheTemplate.put(summaryCacheSupplier(summary, organizationId));
		return summary;
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		OrganizationSummary summary = delegate.disableOrganization(organizationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.generateDetailsKey(organizationId));
		cacheTemplate.put(summaryCacheSupplier(summary, organizationId));
		return summary;
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		OrganizationDetails details = delegate.updateOrganizationDisplayName(organizationId, name);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		OrganizationDetails details = delegate.updateOrganizationMainLocation(organizationId, locationId);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		OrganizationDetails details = delegate.updateOrganizationMainContact(organizationId, personId);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<Identifier> groupIds) {
		OrganizationDetails details = delegate.updateOrganizationGroups(organizationId, groupIds);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return cacheTemplate.get(
				() -> delegate.findOrganizationSummary(organizationId),
				organizationId,
				CrmCacheKeyGenerator::generateSummaryKey,
				this::summaryCacheSupplier);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return cacheTemplate.get(
				() -> delegate.findOrganizationDetails(organizationId),
				organizationId,
				CrmCacheKeyGenerator::generateDetailsKey,
				this::detailsCacheSupplier);
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return delegate.countOrganizations(filter);
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		FilteredPage<OrganizationSummary> page = delegate.findOrganizationSummaries(filter, paging);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getOrganizationId()));
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		FilteredPage<OrganizationSummary> page = delegate.findOrganizationSummaries(filter);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getOrganizationId()));
		});
		return page;
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		FilteredPage<OrganizationDetails> page = delegate.findOrganizationDetails(filter, paging);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getOrganizationId()));
		});
		return page;
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		FilteredPage<OrganizationDetails> page = delegate.findOrganizationDetails(filter);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getOrganizationId()));
		});
		return page;
	}
}