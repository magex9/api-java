package ca.magex.crm.api.services;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmLocationService {
	
	default LocationDetails prototypeLocation(
			Identifier organizationId, 
			String reference, 
			String displayName, 
			MailingAddress address) {
		return new LocationDetails(null, organizationId, Status.PENDING, reference, displayName, address);
	};
	
	default LocationDetails createLocation(LocationDetails prototype) {
		return createLocation(
			prototype.getOrganizationId(),
			prototype.getReference(), 
			prototype.getDisplayName(), 
			prototype.getAddress());
	}

	LocationDetails createLocation(
		Identifier organizationId,
		String reference, 
		String displayName, 
		MailingAddress address
	);

	LocationSummary enableLocation(
		Identifier locationId
	);

	LocationSummary disableLocation(
		Identifier locationId
	);

	LocationDetails updateLocationName(
		Identifier locationId, 
		String displaysName
	);

	LocationDetails updateLocationAddress(
		Identifier locationId, 
		MailingAddress address
	);

	LocationSummary findLocationSummary(
		Identifier locationId
	);

	LocationDetails findLocationDetails(
		Identifier locationId
	);

	long countLocations(
		LocationsFilter filter
	);	

	FilteredPage<LocationDetails> findLocationDetails(
		LocationsFilter filter, 
		Paging paging
	);

	FilteredPage<LocationSummary> findLocationSummaries(
		LocationsFilter filter, 
		Paging paging
	);
	
	default FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter) {
		return findLocationDetails(filter, defaultLocationsPaging());
	}
	
	default FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter) {
		return findLocationSummaries(filter, defaultLocationsPaging());
	}
	
	default FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(Identifier organizationId) {
		return findLocationSummaries(new LocationsFilter(organizationId, null, null, Status.ACTIVE));
	}
	
	default LocationsFilter defaultLocationsFilter() {
		return new LocationsFilter();
	};
	
	default Paging defaultLocationsPaging() {
		return new Paging(LocationsFilter.getSortOptions().get(0));
	}
	
}
