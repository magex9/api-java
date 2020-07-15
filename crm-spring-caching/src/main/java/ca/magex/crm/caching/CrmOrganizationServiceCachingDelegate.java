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
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
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
				Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), details),
				Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), details == null ? null : details.asSummary()));
	}

	/**
	 * Provides the list of pairs for caching organization summary
	 * @param summary
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(OrganizationSummary summary, Identifier key) {
		if (summary == null) {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), null),
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), null));
		} else {
			return List.of(
					Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), summary));
		}
	}

	@Override
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {
		OrganizationDetails details = delegate.createOrganization(prototype);
		cacheTemplate.put(detailsCacheSupplier(details, details.getOrganizationId()));
		return details;
	}

	@Override
	public OrganizationDetails createOrganization(String displayName, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds) {
		OrganizationDetails details = delegate.createOrganization(displayName, authenticationGroupIds, businessGroupIds);
		cacheTemplate.put(detailsCacheSupplier(details, details.getOrganizationId()));
		return details;
	}

	@Override
	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		OrganizationSummary summary = delegate.enableOrganization(organizationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(organizationId));
		cacheTemplate.put(summaryCacheSupplier(summary, organizationId));
		return summary;
	}

	@Override
	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		OrganizationSummary summary = delegate.disableOrganization(organizationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(organizationId));
		cacheTemplate.put(summaryCacheSupplier(summary, organizationId));
		return summary;
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		OrganizationDetails details = delegate.updateOrganizationDisplayName(organizationId, name);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId) {
		OrganizationDetails details = delegate.updateOrganizationMainLocation(organizationId, locationId);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId) {
		OrganizationDetails details = delegate.updateOrganizationMainContact(organizationId, personId);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		OrganizationDetails details = delegate.updateOrganizationAuthenticationGroups(organizationId, authenticationGroupIds);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}
	
	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> businessGroupIds) {
		OrganizationDetails details = delegate.updateOrganizationBusinessGroups(organizationId, businessGroupIds);
		cacheTemplate.put(detailsCacheSupplier(details, organizationId));
		return details;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		return cacheTemplate.get(
				() -> delegate.findOrganizationSummary(organizationId),
				organizationId,
				CrmCacheKeyGenerator.getInstance()::generateSummaryKey,
				this::summaryCacheSupplier);
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		return cacheTemplate.get(
				() -> delegate.findOrganizationDetails(organizationId),
				organizationId,
				CrmCacheKeyGenerator.getInstance()::generateDetailsKey,
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