package ca.magex.crm.amnesia.services;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
public class AmnesiaLocationService implements CrmLocationService {

	@Autowired private AmnesiaDB db;
	
	public AmnesiaLocationService() {}
	
	public AmnesiaLocationService(AmnesiaDB db) {
		this.db = db;
	}
	
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		return db.saveLocation(new LocationDetails(db.generateId(), db.findOrganization(organizationId).getOrganizationId(), Status.ACTIVE, locationReference, locationName, address));
	}

	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		return db.saveLocation(findLocationDetails(locationId).withDisplayName(locationName));
	}

	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return db.saveLocation(findLocationDetails(locationId).withAddress(address));
	}

	public LocationSummary enableLocation(Identifier locationId) {		
		return db.saveLocation(findLocationDetails(locationId).withStatus(Status.ACTIVE));
	}

	public LocationSummary disableLocation(Identifier locationId) {		
		return db.saveLocation(findLocationDetails(locationId).withStatus(Status.INACTIVE));
	}
	
	public LocationSummary findLocationSummary(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	public LocationDetails findLocationDetails(Identifier locationId) {
		return db.findLocation(locationId);
	}
	
	public Stream<LocationDetails> apply(LocationsFilter filter) {
		return db.findByType(LocationDetails.class)
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true);		
	}
	
	public long countLocations(LocationsFilter filter) {
		return apply(filter).count();
	}
	
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}
	
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

}