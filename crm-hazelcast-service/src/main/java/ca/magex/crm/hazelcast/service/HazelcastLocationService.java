package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastLocationService implements CrmLocationService {

	public static String HZ_LOCATION_KEY = "locations";

	private XATransactionAwareHazelcastInstance hzInstance;
	private CrmOrganizationService organizationService;	
	
	public HazelcastLocationService(
			XATransactionAwareHazelcastInstance hzInstance, 
			@Lazy CrmOrganizationService organizationService) {
		this.hzInstance = hzInstance;
		this.organizationService = organizationService;
	}

	@Override
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationSummary(organizationId);
		/* create our new location for this organizationId */
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_LOCATION_KEY);
		LocationDetails locDetails = new LocationDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				organizationId,
				Status.ACTIVE,
				locationReference,
				locationName,
				address);
		locations.put(locDetails.getLocationId(), locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displayName) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		if (StringUtils.equals(displayName, locDetails.getDisplayName())) {
			return locDetails;
		}
		locDetails = locDetails.withDisplayName(displayName);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		if (locDetails.getAddress().equals(address)) {
			return locDetails;
		}
		locDetails = locDetails.withAddress(address);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		if (locDetails.getStatus() == Status.ACTIVE) {
			return locDetails;
		}
		locDetails = locDetails.withStatus(Status.ACTIVE);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		if (locDetails.getStatus() == Status.INACTIVE) {
			return locDetails;
		}
		locDetails = locDetails.withStatus(Status.INACTIVE);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return findLocationDetails(locationId);
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		return locations.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.count();
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		List<LocationDetails> allMatchingLocations = locations.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingLocations, paging);
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		TransactionalMap<Identifier, LocationDetails> locations = hzInstance.getLocationsMap();
		List<LocationSummary> allMatchingLocations = locations.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingLocations, paging);
	}
	
	@Override
	public Page<LocationSummary> findActiveLocationSummariesForOrg(@NotNull Identifier organizationId) {	
		return CrmLocationService.super.findActiveLocationSummariesForOrg(organizationId);
	}
	
	@Override
	public FilteredPage<LocationDetails> findLocationDetails(@NotNull LocationsFilter filter) {	
		return CrmLocationService.super.findLocationDetails(filter);
	}
	
	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(@NotNull LocationsFilter filter) {	
		return CrmLocationService.super.findLocationSummaries(filter);
	}
	
	@Override
	public LocationDetails createLocation(LocationDetails prototype) {	
		return CrmLocationService.super.createLocation(prototype);
	}
	
	@Override
	public LocationDetails prototypeLocation(@NotNull Identifier organizationId, @NotNull String displayName, @NotNull String reference, @NotNull MailingAddress address) {	
		return CrmLocationService.super.prototypeLocation(organizationId, displayName, reference, address);
	}
}