package ca.magex.crm.api.services;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;

public interface CrmLocationService {

	LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address);

	LocationSummary enableLocation(Identifier locationId);

	LocationSummary disableLocation(Identifier locationId);

	LocationDetails updateLocationName(Identifier locationId, String displaysName);

	LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address);

	LocationSummary findLocationSummary(Identifier locationId);

	LocationDetails findLocationDetails(Identifier locationId);

	long countLocations(LocationsFilter filter);

	Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging);

	Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging);

}
