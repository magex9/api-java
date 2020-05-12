package ca.magex.crm.api.services;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;

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
}
