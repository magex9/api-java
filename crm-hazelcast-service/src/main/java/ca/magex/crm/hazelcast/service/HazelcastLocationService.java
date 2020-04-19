package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
public class HazelcastLocationService implements CrmLocationService {

	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmOrganizationService organizationService;

	@Override
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationDetails(organizationId);
		/* create our new location for this organizationId */
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator("locations");
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
	public LocationSummary enableLocation(Identifier locationId) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Unable to find location " + locationId);
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
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Unable to find location " + locationId);
		}
		if (locDetails.getStatus() == Status.INACTIVE) {
			return locDetails;
		}
		locDetails = locDetails.withStatus(Status.INACTIVE);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displayName) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Unable to find location " + locationId);
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
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Unable to find location " + locationId);
		}
		if (locDetails.getAddress() != null && locDetails.getAddress().equals(address)) {
			return locDetails;
		}
		if (locDetails.getAddress() == null && address == null) {
			return locDetails;
		}
		locDetails = locDetails.withAddress(address);
		locations.put(locationId, locDetails);
		return SerializationUtils.clone(locDetails);
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return findLocationDetails(locationId);
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		LocationDetails locDetails = locations.get(locationId);
		if (locDetails == null) {
			throw new ItemNotFoundException("Unable to find location " + locationId);
		}
		return locDetails;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		return locations.size();
	}

	@Override
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		List<LocationDetails> allMatchingOrgs = locations.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}

	@Override
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		Map<Identifier, LocationDetails> locations = hzInstance.getMap("locations");
		List<LocationSummary> allMatchingOrgs = locations.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingOrgs, paging);
	}
}