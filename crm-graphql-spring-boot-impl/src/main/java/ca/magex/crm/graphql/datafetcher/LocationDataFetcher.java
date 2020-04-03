package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the location API methods
 * 
 * @author Jonny
 */
public class LocationDataFetcher extends AbstractDataFetcher {
	
	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);

	public LocationDataFetcher(Crm crm) {
		super(crm);
	}

	public DataFetcher<LocationDetails> findLocation() {
		return (environment) -> {
			logger.info("Entering findLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return crm.findLocationDetails(new Identifier(locationId));
		};
	}

	public LocationsFilter extractFilter(Map<String, Object> filter) {
		String displayName = (String) filter.get("displayName");
		Status status = null;
		if (filter.containsKey("status") && StringUtils.isNotBlank((String) filter.get("status"))) {
			try {
				status = Status.valueOf((String) filter.get("status"));
			}
			catch(IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filter.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
		return new LocationsFilter(displayName, status);
	}
	
	public DataFetcher<Integer> countLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return (int) crm.countLocations(extractFilter(
					extractFilter(environment)));
		};
	}
	
	public DataFetcher<Page<LocationDetails>> findLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return crm.findLocationDetails(extractFilter(
					extractFilter(environment)), 
					extractPaging(environment));
		};
	}	

	public DataFetcher<LocationDetails> byOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + LocationDataFetcher.class.getSimpleName());
			OrganizationDetails organization = environment.getSource();
			if (organization.getMainLocationId() != null) {
				return crm.findLocationDetails(organization.getMainLocationId());
			}
			else {
				return null;
			}
		};
	}
	
	public DataFetcher<LocationDetails> createLocation() { 
		return (environment) -> {
			logger.info("Entering createLocation@" + LocationDataFetcher.class.getSimpleName());
			return crm.createLocation(
					new Identifier((String) environment.getArgument("organizationId")), 
					environment.getArgument("locationName"), 
					environment.getArgument("locationReference"), 
					extractMailingAddress(environment, "locationAddress"));
		};
	}
	
	public DataFetcher<LocationDetails> enableLocation() {
		return (environment) -> {
			logger.debug("Entering enableLocation@" + LocationDataFetcher.class.getSimpleName());
			Identifier locationId = new Identifier((String) environment.getArgument("locationId"));
			crm.enableLocation(locationId);
			return crm.findLocationDetails(locationId);
		};
	}

	public DataFetcher<LocationDetails> disableLocation() {
		return (environment) -> {
			logger.debug("Entering disableLocation@" + LocationDataFetcher.class.getSimpleName());
			Identifier locationId = new Identifier((String) environment.getArgument("locationId"));
			crm.disableLocation(locationId);
			return crm.findLocationDetails(locationId);
		};
	}
	
	public DataFetcher<LocationDetails> updateLocation() {
		return (environment) -> {
			logger.debug("Entering updateLocation@" + LocationDataFetcher.class.getSimpleName());
			Identifier locationId = new Identifier((String) environment.getArgument("locationId"));
			if (environment.getArgument("locationName") != null) {
				crm.updateLocationName(
						locationId,
						environment.getArgument("locationName"));
			}
			if (environment.getArgument("locationAddress") != null) {
				crm.updateLocationAddress(
						locationId,
						extractMailingAddress(environment, "locationAddress"));
			}
			return crm.findLocationDetails(locationId);
		};
	}	
}