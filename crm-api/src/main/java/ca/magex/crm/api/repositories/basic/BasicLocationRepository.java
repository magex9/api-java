package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmLocationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.LocationIdentifier;

public class BasicLocationRepository implements CrmLocationRepository {

	private CrmStore store;

	public BasicLocationRepository(CrmStore store) {
		this.store = store;
	}

	private Stream<LocationDetails> apply(LocationsFilter filter) {
		return store.getLocations().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		return store.getLocations().get(locationId);
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		LocationDetails details = findLocationDetails(locationId);
		if (details == null) {
			return null;
		}
		return details.asSummary();
	}

	@Override
	public LocationDetails saveLocationDetails(LocationDetails locationToSave) {
		LocationDetails location = locationToSave.withLastModified(System.currentTimeMillis());		
		store.getLocations().put(location.getLocationId(), location);
		store.getNotifier().locationUpdated(location.getLastModified(), location.getLocationId());
		return SerializationUtils.clone(location);
	}

}