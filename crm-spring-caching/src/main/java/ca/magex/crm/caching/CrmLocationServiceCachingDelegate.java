package ca.magex.crm.caching;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;

/**
 * Delegate that intercepts calls and caches the results
 * 
 * @author Jonny
 */
public class CrmLocationServiceCachingDelegate implements CrmLocationService {

	private CrmLocationService delegate;
	private CacheTemplate cacheTemplate;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheTemplate
	 */
	public CrmLocationServiceCachingDelegate(CrmLocationService delegate, CacheTemplate cacheTemplate) {
		this.delegate = delegate;
		this.cacheTemplate = cacheTemplate;
	}
	
	/**
	 * Provides the list of pairs for caching location details
	 * @param details
	 * @return
	 */
	private List<Pair<String, Object>> detailsCacheSupplier(LocationDetails details, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.getInstance().generateDetailsKey(key), details),
				Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), details == null ? null : details.asSummary()));
	}
	
	/**
	 * Provides the list of pairs for caching location summary
	 * @param summary
	 * @param key
	 * @return
	 */
	private List<Pair<String, Object>> summaryCacheSupplier(LocationSummary summary, Identifier key) {
		return List.of(
				Pair.of(CrmCacheKeyGenerator.getInstance().generateSummaryKey(key), summary));
	}

	@Override
	public LocationDetails createLocation(LocationDetails prototype) {
		LocationDetails details = delegate.createLocation(prototype);
		cacheTemplate.put(detailsCacheSupplier(details, details.getLocationId()));
		return details;
	}

	@Override
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String displayName, String reference, MailingAddress address) {
		LocationDetails details = delegate.createLocation(organizationId, displayName, reference, address);
		cacheTemplate.put(detailsCacheSupplier(details, details.getLocationId()));
		return details;
	}

	@Override
	public LocationSummary enableLocation(LocationIdentifier locationId) {
		LocationSummary summary = delegate.enableLocation(locationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(locationId));
		cacheTemplate.put(summaryCacheSupplier(summary, locationId));
		return summary;
	}

	@Override
	public LocationSummary disableLocation(LocationIdentifier locationId) {
		LocationSummary summary = delegate.disableLocation(locationId);
		cacheTemplate.evict(CrmCacheKeyGenerator.getInstance().generateDetailsKey(locationId));
		cacheTemplate.put(summaryCacheSupplier(summary, locationId));
		return summary;
	}

	@Override
	public LocationDetails updateLocationName(LocationIdentifier locationId, String displaysName) {
		LocationDetails details = delegate.updateLocationName(locationId, displaysName);
		cacheTemplate.put(detailsCacheSupplier(details, locationId));
		return details;
	}

	@Override
	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		LocationDetails details = delegate.updateLocationAddress(locationId, address);
		cacheTemplate.put(detailsCacheSupplier(details, locationId));
		return details;
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		return cacheTemplate.get(
				() -> delegate.findLocationSummary(locationId),
				locationId,
				CrmCacheKeyGenerator.getInstance()::generateSummaryKey,
				this::summaryCacheSupplier);
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		return cacheTemplate.get(
				() -> delegate.findLocationDetails(locationId),
				locationId,
				CrmCacheKeyGenerator.getInstance()::generateDetailsKey,
				this::detailsCacheSupplier);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return delegate.countLocations(filter);
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		FilteredPage<LocationSummary> page = delegate.findLocationSummaries(filter, paging);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getLocationId()));
		});
		return page;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(@NotNull LocationsFilter filter) {
		FilteredPage<LocationSummary> page = delegate.findLocationSummaries(filter);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getLocationId()));
		});
		return page;
	}
	
	@Override
	public FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(OrganizationIdentifier organizationId) {
		FilteredPage<LocationSummary> page = delegate.findActiveLocationSummariesForOrg(organizationId);
		page.forEach((summary) -> {
			cacheTemplate.putIfAbsent(summaryCacheSupplier(summary, summary.getLocationId()));
		});
		return page;
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		FilteredPage<LocationDetails> page = delegate.findLocationDetails(filter, paging);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getLocationId()));
		});
		return page;
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(@NotNull LocationsFilter filter) {
		FilteredPage<LocationDetails> page = delegate.findLocationDetails(filter);
		page.forEach((details) -> {
			cacheTemplate.putIfAbsent(detailsCacheSupplier(details, details.getLocationId()));
		});
		return page;
	}
}