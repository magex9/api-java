package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

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

public class CrmLocationServiceSlf4jDecorator implements CrmLocationService {
	
	private CrmLocationService delegate;
	
	private Logger logger;
	
	public CrmLocationServiceSlf4jDecorator(CrmLocationService delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public LocationDetails prototypeLocation(Identifier organizationId, String reference, String displayName, MailingAddress address) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
				LocationDetails result = delegate.prototypeLocation(organizationId, reference, displayName, address);
				logger.trace("Executed prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
				LocationDetails result = delegate.prototypeLocation(organizationId, reference, displayName, address);
				logger.debug("Executed prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypeLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
			return delegate.prototypeLocation(organizationId, reference, displayName, address);
		}
		else {
			return delegate.prototypeLocation(organizationId, reference, displayName, address);
		}
	}
	
	@Override
	public LocationDetails createLocation(LocationDetails prototype) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createLocation(" + prototype + ")");
				LocationDetails result = delegate.createLocation(prototype);
				logger.trace("Executed createLocation(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createLocation(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createLocation(" + prototype + ")");
				LocationDetails result = delegate.createLocation(prototype);
				logger.debug("Executed createLocation(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createLocation(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createLocation(" + prototype + ")");
			return delegate.createLocation(prototype);
		}
		else {
			return delegate.createLocation(prototype);
		}
	}
	
	@Override
	public LocationDetails createLocation(Identifier organizationId, String reference, String displayName, MailingAddress address) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
				LocationDetails result = delegate.createLocation(organizationId, reference, displayName, address);
				logger.trace("Executed createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
				LocationDetails result = delegate.createLocation(organizationId, reference, displayName, address);
				logger.debug("Executed createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createLocation(" + organizationId + ", " + reference + ", " + displayName + ", " + address + ")");
			return delegate.createLocation(organizationId, reference, displayName, address);
		}
		else {
			return delegate.createLocation(organizationId, reference, displayName, address);
		}
	}
	
	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enableLocation(" + locationId + ")");
				LocationSummary result = delegate.enableLocation(locationId);
				logger.trace("Executed enableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enableLocation(" + locationId + ")");
				LocationSummary result = delegate.enableLocation(locationId);
				logger.debug("Executed enableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enableLocation(" + locationId + ")");
			return delegate.enableLocation(locationId);
		}
		else {
			return delegate.enableLocation(locationId);
		}
	}
	
	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disableLocation(" + locationId + ")");
				LocationSummary result = delegate.disableLocation(locationId);
				logger.trace("Executed disableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disableLocation(" + locationId + ")");
				LocationSummary result = delegate.disableLocation(locationId);
				logger.debug("Executed disableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disableLocation(" + locationId + ")");
			return delegate.disableLocation(locationId);
		}
		else {
			return delegate.disableLocation(locationId);
		}
	}
	
	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displaysName) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateLocationName(" + locationId + ", " + displaysName + ")");
				LocationDetails result = delegate.updateLocationName(locationId, displaysName);
				logger.trace("Executed updateLocationName(" + locationId + ", " + displaysName + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateLocationName(" + locationId + ", " + displaysName + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateLocationName(" + locationId + ", " + displaysName + ")");
				LocationDetails result = delegate.updateLocationName(locationId, displaysName);
				logger.debug("Executed updateLocationName(" + locationId + ", " + displaysName + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateLocationName(" + locationId + ", " + displaysName + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateLocationName(" + locationId + ", " + displaysName + ")");
			return delegate.updateLocationName(locationId, displaysName);
		}
		else {
			return delegate.updateLocationName(locationId, displaysName);
		}
	}
	
	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateLocationAddress(" + locationId + ", " + address + ")");
				LocationDetails result = delegate.updateLocationAddress(locationId, address);
				logger.trace("Executed updateLocationAddress(" + locationId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateLocationAddress(" + locationId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateLocationAddress(" + locationId + ", " + address + ")");
				LocationDetails result = delegate.updateLocationAddress(locationId, address);
				logger.debug("Executed updateLocationAddress(" + locationId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateLocationAddress(" + locationId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateLocationAddress(" + locationId + ", " + address + ")");
			return delegate.updateLocationAddress(locationId, address);
		}
		else {
			return delegate.updateLocationAddress(locationId, address);
		}
	}
	
	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationSummary(" + locationId + ")");
				LocationSummary result = delegate.findLocationSummary(locationId);
				logger.trace("Executed findLocationSummary(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationSummary(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationSummary(" + locationId + ")");
				LocationSummary result = delegate.findLocationSummary(locationId);
				logger.debug("Executed findLocationSummary(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationSummary(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationSummary(" + locationId + ")");
			return delegate.findLocationSummary(locationId);
		}
		else {
			return delegate.findLocationSummary(locationId);
		}
	}
	
	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationDetails(" + locationId + ")");
				LocationDetails result = delegate.findLocationDetails(locationId);
				logger.trace("Executed findLocationDetails(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationDetails(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationDetails(" + locationId + ")");
				LocationDetails result = delegate.findLocationDetails(locationId);
				logger.debug("Executed findLocationDetails(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationDetails(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationDetails(" + locationId + ")");
			return delegate.findLocationDetails(locationId);
		}
		else {
			return delegate.findLocationDetails(locationId);
		}
	}
	
	@Override
	public long countLocations(LocationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling countLocations(" + filter + ")");
				long result = delegate.countLocations(filter);
				logger.trace("Executed countLocations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on countLocations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling countLocations(" + filter + ")");
				long result = delegate.countLocations(filter);
				logger.debug("Executed countLocations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on countLocations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling countLocations(" + filter + ")");
			return delegate.countLocations(filter);
		}
		else {
			return delegate.countLocations(filter);
		}
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationDetails(" + filter + ", " + paging + ")");
				FilteredPage<LocationDetails> result = delegate.findLocationDetails(filter, paging);
				logger.trace("Executed findLocationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationDetails(" + filter + ", " + paging + ")");
				FilteredPage<LocationDetails> result = delegate.findLocationDetails(filter, paging);
				logger.debug("Executed findLocationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationDetails(" + filter + ", " + paging + ")");
			return delegate.findLocationDetails(filter, paging);
		}
		else {
			return delegate.findLocationDetails(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationSummaries(" + filter + ", " + paging + ")");
				FilteredPage<LocationSummary> result = delegate.findLocationSummaries(filter, paging);
				logger.trace("Executed findLocationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationSummaries(" + filter + ", " + paging + ")");
				FilteredPage<LocationSummary> result = delegate.findLocationSummaries(filter, paging);
				logger.debug("Executed findLocationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationSummaries(" + filter + ", " + paging + ")");
			return delegate.findLocationSummaries(filter, paging);
		}
		else {
			return delegate.findLocationSummaries(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationDetails(" + filter + ")");
				FilteredPage<LocationDetails> result = delegate.findLocationDetails(filter);
				logger.trace("Executed findLocationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationDetails(" + filter + ")");
				FilteredPage<LocationDetails> result = delegate.findLocationDetails(filter);
				logger.debug("Executed findLocationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationDetails(" + filter + ")");
			return delegate.findLocationDetails(filter);
		}
		else {
			return delegate.findLocationDetails(filter);
		}
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findLocationSummaries(" + filter + ")");
				FilteredPage<LocationSummary> result = delegate.findLocationSummaries(filter);
				logger.trace("Executed findLocationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findLocationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findLocationSummaries(" + filter + ")");
				FilteredPage<LocationSummary> result = delegate.findLocationSummaries(filter);
				logger.debug("Executed findLocationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findLocationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findLocationSummaries(" + filter + ")");
			return delegate.findLocationSummaries(filter);
		}
		else {
			return delegate.findLocationSummaries(filter);
		}
	}
	
	@Override
	public FilteredPage<LocationSummary> findActiveLocationSummariesForOrg(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findActiveLocationSummariesForOrg(" + organizationId + ")");
				FilteredPage<LocationSummary> result = delegate.findActiveLocationSummariesForOrg(organizationId);
				logger.trace("Executed findActiveLocationSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findActiveLocationSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findActiveLocationSummariesForOrg(" + organizationId + ")");
				FilteredPage<LocationSummary> result = delegate.findActiveLocationSummariesForOrg(organizationId);
				logger.debug("Executed findActiveLocationSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findActiveLocationSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findActiveLocationSummariesForOrg(" + organizationId + ")");
			return delegate.findActiveLocationSummariesForOrg(organizationId);
		}
		else {
			return delegate.findActiveLocationSummariesForOrg(organizationId);
		}
	}
	
	@Override
	public LocationsFilter defaultLocationsFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultLocationsFilter()");
				LocationsFilter result = delegate.defaultLocationsFilter();
				logger.trace("Executed defaultLocationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultLocationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultLocationsFilter()");
				LocationsFilter result = delegate.defaultLocationsFilter();
				logger.debug("Executed defaultLocationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultLocationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultLocationsFilter()");
			return delegate.defaultLocationsFilter();
		}
		else {
			return delegate.defaultLocationsFilter();
		}
	}
	
	@Override
	public Paging defaultLocationsPaging() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultLocationsPaging()");
				Paging result = delegate.defaultLocationsPaging();
				logger.trace("Executed defaultLocationsPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultLocationsPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultLocationsPaging()");
				Paging result = delegate.defaultLocationsPaging();
				logger.debug("Executed defaultLocationsPaging() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultLocationsPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultLocationsPaging()");
			return delegate.defaultLocationsPaging();
		}
		else {
			return delegate.defaultLocationsPaging();
		}
	}
	
}
