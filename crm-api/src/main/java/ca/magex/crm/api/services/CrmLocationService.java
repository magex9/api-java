package ca.magex.crm.api.services;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public interface CrmLocationService {

	LocationDetails createLocation(
		@NotNull Identifier organizationId, 
		@NotNull String displayName, 
		@NotNull String reference, 
		@NotNull MailingAddress address
	);

	LocationSummary enableLocation(
		@NotNull Identifier locationId
	);

	LocationSummary disableLocation(
		@NotNull Identifier locationId
	);

	LocationDetails updateLocationName(
		@NotNull Identifier locationId, 
		@NotNull String displaysName
	);

	LocationDetails updateLocationAddress(
		@NotNull Identifier locationId, 
		@NotNull MailingAddress address
	);

	LocationSummary findLocationSummary(
		@NotNull Identifier locationId
	);

	LocationDetails findLocationDetails(
		@NotNull Identifier locationId
	);

	long countLocations(
		@NotNull LocationsFilter filter
	);	

	Page<LocationDetails> findLocationDetails(
		@NotNull LocationsFilter filter, 
		@NotNull Paging paging
	);

	Page<LocationSummary> findLocationSummaries(
		@NotNull LocationsFilter filter, 
		@NotNull Paging paging
	);
	
	default Page<LocationDetails> findLocationDetails(@NotNull LocationsFilter filter) {
		return findLocationDetails(filter, defaultLocationsPaging());
	}
	
	default Page<LocationSummary> findLocationSummaries(@NotNull LocationsFilter filter) {
		return findLocationSummaries(filter, defaultLocationsPaging());
	}
	
	default Page<LocationSummary> findActiveLocationSummariesForOrg(@NotNull Identifier organizationId) {
		return findLocationSummaries(new LocationsFilter(organizationId, null, null, Status.ACTIVE));
	}
	
	default LocationsFilter defaultLocationsFilter() {
		return new LocationsFilter();
	};
	
	default Paging defaultLocationsPaging() {
		return new Paging(SORT_OPTIONS.get(0));
	}
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("displayName")),
		Sort.by(Order.desc("displayName")),
		Sort.by(Order.asc("reference")),
		Sort.by(Order.desc("reference")),
		Sort.by(Order.asc("country")),
		Sort.by(Order.desc("country")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);
	
	default List<Sort> getLocationsSortOptions() {
		return SORT_OPTIONS;
	}
	
}
