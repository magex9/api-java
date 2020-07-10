package ca.magex.crm.amnesia.services;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
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

@Service("PrincipalLocationService")
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaLocationService implements CrmLocationService {

	private AmnesiaDB db;
	
	public AmnesiaLocationService(AmnesiaDB db) {
		this.db = db;
	}
	
	public LocationDetails createLocation(Identifier organizationId, String locationReference, String locationName, MailingAddress address) {
		return db.saveLocation(new LocationDetails(db.generateId(), organizationId, Status.ACTIVE, locationReference, locationName, address));
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		LocationDetails loc = db.findLocation(locationId);
		if (loc == null) {
			return null;
		}
		return db.saveLocation(loc.withDisplayName(locationName));
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		LocationDetails loc = db.findLocation(locationId);
		if (loc == null) {
			return null;
		}
		return db.saveLocation(loc.withAddress(address));
	}

	public LocationSummary enableLocation(Identifier locationId) {		
		LocationDetails loc = db.findLocation(locationId);
		if (loc == null) {
			return null;
		}
		return db.saveLocation(loc.withStatus(Status.ACTIVE));
	}

	public LocationSummary disableLocation(Identifier locationId) {
		LocationDetails loc = db.findLocation(locationId);
		if (loc == null) {
			return null;
		}
		return db.saveLocation(loc.withStatus(Status.INACTIVE));
	}
	
	public LocationSummary findLocationSummary(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	public Stream<LocationDetails> apply(LocationsFilter filter) {
		return db.findByType(LocationDetails.class)
			.filter(loc -> filter.apply(loc));
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