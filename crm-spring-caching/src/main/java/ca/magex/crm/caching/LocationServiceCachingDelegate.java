package ca.magex.crm.caching;

import javax.validation.constraints.NotNull;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

@Service("CachingLocationService")
public class LocationServiceCachingDelegate implements CrmLocationService {

	private CrmLocationService delegate;
	private CacheManager cacheManager;

	/**
	 * Wraps the delegate service using the given cacheManager
	 * 
	 * @param delegate
	 * @param cacheManager
	 */
	public LocationServiceCachingDelegate(CrmLocationService delegate, CacheManager cacheManager) {
		this.delegate = delegate;
		this.cacheManager = cacheManager;
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "locations", key = "'Details_'.concat(#result.locationId)", unless = "#result == null"),
			@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#result.locationId)", unless = "#result == null")
	})
	public LocationDetails createLocation(LocationDetails prototype) {
		return delegate.createLocation(prototype);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "locations", key = "'Details_'.concat(#result.locationId)", unless = "#result == null"),
			@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#result.locationId)", unless = "#result == null")
	})
	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address) {
		return delegate.createLocation(organizationId, displayName, reference, address);
	}

	@Override
	@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#locationId)")
	@CacheEvict(cacheNames = "locations", key = "'Details_'.concat(#locationId)")
	public LocationSummary enableLocation(Identifier locationId) {
		return delegate.enableLocation(locationId);
	}

	@Override
	@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#locationId)")
	@CacheEvict(cacheNames = "locations", key = "'Details_'.concat(#locationId)")
	public LocationSummary disableLocation(Identifier locationId) {
		return delegate.disableLocation(locationId);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "locations", key = "'Details_'.concat(#locationId)"),
			@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#locationId)")
	})
	public LocationDetails updateLocationName(Identifier locationId, String displaysName) {
		return delegate.updateLocationName(locationId, displaysName);
	}

	@Override
	@Caching(put = {
			@CachePut(cacheNames = "locations", key = "'Details_'.concat(#locationId)"),
			@CachePut(cacheNames = "locations", key = "'Summary_'.concat(#locationId)")
	})
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return delegate.updateLocationAddress(locationId, address);
	}

	@Override
	@Cacheable(cacheNames = "locations", key = "'Summary_'.concat(#locationId)")
	public LocationSummary findLocationSummary(Identifier locationId) {
		return delegate.findLocationSummary(locationId);
	}

	@Override
	@Cacheable(cacheNames = "locations", key = "'Details_'.concat(#locationId)")
	public LocationDetails findLocationDetails(Identifier locationId) {
		return delegate.findLocationDetails(locationId);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return delegate.countLocations(filter);
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		FilteredPage<LocationDetails> page = delegate.findLocationDetails(filter, paging);
		Cache locationsCache = cacheManager.getCache("locations");
		page.forEach((details) -> {
			locationsCache.putIfAbsent("Details_" + details.getLocationId(), details);
			locationsCache.putIfAbsent("Summary_" + details.getLocationId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(@NotNull LocationsFilter filter) {
		FilteredPage<LocationDetails> page = delegate.findLocationDetails(filter);
		Cache locationsCache = cacheManager.getCache("locations");
		page.forEach((details) -> {
			locationsCache.putIfAbsent("Details_" + details.getLocationId(), details);
			locationsCache.putIfAbsent("Summary_" + details.getLocationId(), details);
		});
		return page;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		FilteredPage<LocationSummary> page = delegate.findLocationSummaries(filter, paging);
		Cache locationsCache = cacheManager.getCache("locations");
		page.forEach((summary) -> {
			locationsCache.putIfAbsent("Summary_" + summary.getLocationId(), summary);
		});
		return page;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(@NotNull LocationsFilter filter) {
		FilteredPage<LocationSummary> page = delegate.findLocationSummaries(filter);
		Cache locationsCache = cacheManager.getCache("locations");
		page.forEach((summary) -> {
			locationsCache.putIfAbsent("Summary_" + summary.getLocationId(), summary);
		});
		return page;
	}

	@Override
	public FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(Identifier organizationId) {
		FilteredPage<LocationSummary> page = delegate.findActiveLocationSummariesForOrg(organizationId);
		Cache locationsCache = cacheManager.getCache("locations");
		page.forEach((summary) -> {
			locationsCache.putIfAbsent("Summary_" + summary.getLocationId(), summary);
		});
		return page;
	}
}