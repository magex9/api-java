package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmLocationRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Location Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastLocationRepository implements CrmLocationRepository {

	private XATransactionAwareHazelcastInstance hzInstance;	
	
	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastLocationRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}
	
	@Override
	public Identifier generateLocationId() {
		return CrmStore.generateId(LocationDetails.class);
	}

	@Override
	public LocationDetails saveLocationDetails(LocationDetails location) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		/* persist a clone of this location, and return the original */
		locations.put(location.getLocationId(), SerializationUtils.clone(location));
		return location;
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			return null;
		}
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return findLocationDetails(locationId).asSummary();
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		List<LocationDetails> allMatchingLocations = locations.values(new CrmFilterPredicate<LocationSummary>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> SerializationUtils.clone(i))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingLocations, paging);
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		List<LocationSummary> allMatchingLocations = locations.values(new CrmFilterPredicate<LocationSummary>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> i.asSummary())
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingLocations, paging);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		return locations.values(new CrmFilterPredicate<LocationSummary>(filter)).size();
	}
}