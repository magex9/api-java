package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Validated
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLocationService implements CrmLocationService {

	public static String HZ_LOCATION_KEY = "locations";

	@Autowired private HazelcastInstance hzInstance;

	// these need to be marked as lazy because spring proxies this class due to the @Validated annotation
	// if these are not lazy then they are autowired before the proxy is created and we get a cyclic dependency
	// so making them lazy allows the proxy to be created before autowiring
	@Autowired @Lazy private CrmOrganizationService organizationService;

	@Override
	public LocationDetails createLocation(
			@NotNull Identifier organizationId, 
			@NotNull String locationName, 
			@NotNull String locationReference, 
			@NotNull MailingAddress address) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationSummary(organizationId);
		/* create our new location for this organizationId */
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public LocationDetails updateLocationName(
			@NotNull Identifier locationId, 
			@NotNull String displayName) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public LocationDetails updateLocationAddress(
			@NotNull Identifier locationId, 
			@NotNull MailingAddress address) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public LocationSummary enableLocation(
			@NotNull Identifier locationId) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public LocationSummary disableLocation(
			@NotNull Identifier locationId) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public LocationSummary findLocationSummary(
			@NotNull Identifier locationId) {
		return findLocationDetails(locationId);
	}

	@Override
	public LocationDetails findLocationDetails(
			@NotNull Identifier locationId) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Location ID '" + locationId + "'");
		}
		return locDetails;
	}

	@Override
	public long countLocations(
			@NotNull LocationsFilter filter) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
		return locations.values()
			.stream()
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
			.count();
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(
			@NotNull LocationsFilter filter, 
			@NotNull Paging paging) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
	public FilteredPage<LocationSummary> findLocationSummaries(
			@NotNull LocationsFilter filter, 
			@NotNull Paging paging) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap(HZ_LOCATION_KEY);
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
}