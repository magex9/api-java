package ca.magex.crm.amnesia.services;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLocationService implements CrmLocationService {

	private AmnesiaDB db;
	
	public AmnesiaLocationService(AmnesiaDB db) {
		this.db = db;
	}
	
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		return db.saveLocation(validate(new LocationDetails(db.generateId(), db.findOrganization(organizationId).getOrganizationId(), Status.ACTIVE, locationReference, locationName, address)));
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		return db.saveLocation(validate(findLocationDetails(locationId).withDisplayName(locationName)));
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return db.saveLocation(validate(findLocationDetails(locationId).withAddress(address)));
	}

	public LocationSummary enableLocation(Identifier locationId) {		
		return db.saveLocation(validate(findLocationDetails(locationId).withStatus(Status.ACTIVE)));
	}

	public LocationSummary disableLocation(Identifier locationId) {		
		return db.saveLocation(validate(findLocationDetails(locationId).withStatus(Status.INACTIVE)));
	}
	
	public LocationSummary findLocationSummary(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	private LocationDetails validate(LocationDetails location) {
		return db.getValidation().validate(location);
	}
	
	public Stream<LocationDetails> apply(LocationsFilter filter) {
		return db.findByType(LocationDetails.class)
			.filter(loc -> StringUtils.isNotBlank(filter.getDisplayName()) ? loc.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(loc -> filter.getStatus() != null ? loc.getStatus().equals(filter.getStatus()) : true)
			.filter(loc -> filter.getOrganizationId() != null ? loc.getOrganizationId().equals(filter.getOrganizationId()) : true);
	}
	
	public long countLocations(LocationsFilter filter) {
		return apply(filter).count();
	}
	
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}
	
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

}