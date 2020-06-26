package ca.magex.crm.api.repositories;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public interface CrmLocationRepository {

	public static final String CONTEXT = "/locations";
	
	default Identifier generateLocationId() {
		return CrmStore.generateId(CONTEXT);
	}
	
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging); 

	public FilteredPage<LocationSummary> findLocationSummary(LocationsFilter filter, Paging paging); 

	public long countLocations(LocationsFilter filter); 
	
	public LocationDetails findLocationDetails(Identifier locationId);

	public LocationSummary findLocationSummary(Identifier locationId);

	public LocationDetails saveLocationDetails(LocationDetails location);
	
}
