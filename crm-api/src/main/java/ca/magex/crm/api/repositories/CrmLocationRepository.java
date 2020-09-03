package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.LocationIdentifier;

/**
 * Repository interface used for saving/retrieving a Location
 * 
 * @author Jonny
 */
public interface CrmLocationRepository {

	/**
	 * returns the next identifier to be assigned to a new Location
	 * 
	 * @return
	 */
	default LocationIdentifier generateLocationId() {
		return new LocationIdentifier(CrmStore.generateId());
	}

	/**
	 * Save the given location to the repository
	 * 
	 * @param location
	 * @return
	 */
	public LocationDetails saveLocationDetails(LocationDetails location);

	/**
	 * returns the full location details associated with the given locationId, 
	 * or null if the locationId does not exist
	 * 
	 * @param locationId
	 * @return
	 */
	public LocationDetails findLocationDetails(LocationIdentifier locationId);

	/**
	 * returns the location summary associated with the given locationId, 
	 * or null if the locationId does not exist
	 * 
	 * @param locationId
	 * @return
	 */
	public LocationSummary findLocationSummary(LocationIdentifier locationId);

	/**
	 * returns the paged results with the full location details for any location that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging);

	/**
	 * returns the paged results with the location summary for any location that matches the given filter
	 * 
	 * @param filter
	 * @param paging
	 * @return
	 */
	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging);

	/**
	 * returns the number of locations that match the given filter
	 * 
	 * @param filter
	 * @return
	 */
	public long countLocations(LocationsFilter filter);
}