package ca.magex.crm.api.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;

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
			@NotNull Identifier organizationId, 
			@NotNull String reference, 
			@NotNull String displayName, 
			@NotNull MailingAddress address) {
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
		@NotNull Identifier organizationId,
		@NotNull String reference, 
		@NotNull String displayName, 
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

	FilteredPage<LocationDetails> findLocationDetails(
		@NotNull LocationsFilter filter, 
		@NotNull Paging paging
	);

	FilteredPage<LocationSummary> findLocationSummaries(
		@NotNull LocationsFilter filter, 
		@NotNull Paging paging
	);
	
	default FilteredPage<LocationDetails> findLocationDetails(@NotNull LocationsFilter filter) {
		return findLocationDetails(filter, defaultLocationsPaging());
	}
	
	default FilteredPage<LocationSummary> findLocationSummaries(@NotNull LocationsFilter filter) {
		return findLocationSummaries(filter, defaultLocationsPaging());
	}
	
	default Page<LocationSummary> findActiveLocationSummariesForOrg(@NotNull Identifier organizationId) {
		return findLocationSummaries(new LocationsFilter(organizationId, null, null, Status.ACTIVE));
	}
	
	default LocationsFilter defaultLocationsFilter() {
		return new LocationsFilter();
	};
	
	default Paging defaultLocationsPaging() {
		return new Paging(LocationsFilter.getSortOptions().get(0));
	}
	
}
