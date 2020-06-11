package ca.magex.crm.api.decorators;

import ca.magex.crm.api.services.CrmLocationService;

import javax.validation.constraints.NotNull;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmLocationServiceDelegate implements CrmLocationService {
	
	private CrmLocationService delegate;
	
	public CrmLocationServiceDelegate(CrmLocationService delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public LocationDetails prototypeLocation(Identifier organizationId, String reference, String displayName, MailingAddress address) {
		return delegate.prototypeLocation(organizationId, reference, displayName, address);
	}
	
	@Override
	public LocationDetails createLocation(LocationDetails prototype) {
		return delegate.createLocation(prototype);
	}
	
	@Override
	public LocationDetails createLocation(Identifier organizationId, String reference, String displayName, MailingAddress address) {
		return delegate.createLocation(organizationId, reference, displayName, address);
	}
	
	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		return delegate.enableLocation(locationId);
	}
	
	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return delegate.disableLocation(locationId);
	}
	
	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displaysName) {
		return delegate.updateLocationName(locationId, displaysName);
	}
	
	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return delegate.updateLocationAddress(locationId, address);
	}
	
	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return delegate.findLocationSummary(locationId);
	}
	
	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		return delegate.findLocationDetails(locationId);
	}
	
	@Override
	public long countLocations(LocationsFilter filter) {
		return delegate.countLocations(filter);
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		return delegate.findLocationDetails(filter, paging);
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		return delegate.findLocationSummaries(filter, paging);
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter) {
		return delegate.findLocationDetails(filter);
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter) {
		return delegate.findLocationSummaries(filter);
	}
	
	@Override
	public FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(Identifier organizationId) {
		return delegate.findActiveLocationSummariesForOrg(organizationId);
	}
	
	@Override
	public LocationsFilter defaultLocationsFilter() {
		return delegate.defaultLocationsFilter();
	}
	
	@Override
	public Paging defaultLocationsPaging() {
		return delegate.defaultLocationsPaging();
	}
	
}
