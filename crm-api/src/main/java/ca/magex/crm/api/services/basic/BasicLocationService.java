package ca.magex.crm.api.services.basic;

import org.apache.commons.codec.binary.StringUtils;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class BasicLocationService implements CrmLocationService {

	private CrmRepositories repos;
	
	public BasicLocationService(CrmRepositories repos) {
		this.repos = repos;
	}
	
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String locationReference, String locationName, MailingAddress address) {
		return repos.saveLocationDetails(new LocationDetails(repos.generateLocationId(), organizationId, Status.ACTIVE, locationReference, locationName, address, null));
	}

	public LocationDetails updateLocationName(LocationIdentifier locationId, String locationName) {
		LocationDetails loc = repos.findLocationDetails(locationId);
		if (loc == null) {
			return null;
		}
		if (StringUtils.equals(loc.getDisplayName(), locationName)) {
			return loc;
		}
		return repos.saveLocationDetails(loc.withDisplayName(locationName));
	}

	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		LocationDetails loc = repos.findLocationDetails(locationId);
		if (loc == null) {
			return null;
		}
		if (loc.getAddress().equals(address)) {
			return loc;
		}
		return repos.saveLocationDetails(loc.withAddress(address));
	}

	public LocationSummary enableLocation(LocationIdentifier locationId) {		
		LocationDetails loc = repos.findLocationDetails(locationId);
		if (loc == null) {
			return null;
		}
		if (loc.getStatus() == Status.ACTIVE) {
			return loc.asSummary();
		}
		return repos.saveLocationDetails(loc.withStatus(Status.ACTIVE)).asSummary();
	}

	public LocationSummary disableLocation(LocationIdentifier locationId) {
		LocationDetails loc = repos.findLocationDetails(locationId);
		if (loc == null) {
			return null;
		}
		if (loc.getStatus() == Status.INACTIVE) {
			return loc.asSummary();
		}
		return repos.saveLocationDetails(loc.withStatus(Status.INACTIVE)).asSummary();
	}
	
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		return repos.findLocationSummary(locationId);
	}
	
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		return repos.findLocationDetails(locationId);
	}
	
	public long countLocations(LocationsFilter filter) {
		return repos.countLocations(filter);
	}
	
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return repos.findLocationSummary(filter, paging);
	}
	
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return repos.findLocationDetails(filter, paging);
	}

}