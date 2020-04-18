package ca.magex.crm.hazelcast.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
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
		return locDetails;
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displaysName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}	
}