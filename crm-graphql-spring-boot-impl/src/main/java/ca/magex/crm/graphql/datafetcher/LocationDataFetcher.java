package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;

/**
 * Contains the data fetcher implementations for each of the location API methods
 * 
 * @author Jonny
 */
public class LocationDataFetcher extends AbstractDataFetcher {
	
	private static Logger logger = LoggerFactory.getLogger(LocationDataFetcher.class);

	public LocationDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	public DataFetcher<LocationDetails> findLocation() {
		return (environment) -> {
			logger.info("Entering findLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return organizations.findLocation(new Identifier(locationId));
		};
	}
	
	public DataFetcher<Integer> countLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return (int) organizations.countLocations(new LocationsFilter(
					extractFilter(environment)));
		};
	}
	
	public DataFetcher<Page<LocationDetails>> findLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return organizations.findLocationDetails(new LocationsFilter(
					extractFilter(environment)), 
					extractPaging(environment));
		};
	}	

	public DataFetcher<LocationDetails> byOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + LocationDataFetcher.class.getSimpleName());
			OrganizationDetails organization = environment.getSource();
			if (organization.getMainLocationId() != null) {
				return organizations.findLocation(organization.getMainLocationId());
			}
			else {
				return null;
			}
		};
	}
	
	public DataFetcher<LocationDetails> createLocation() { 
		return (environment) -> {
			logger.info("Entering createLocation@" + LocationDataFetcher.class.getSimpleName());
			return organizations.createLocation(
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
			organizations.enableLocation(locationId);
			return organizations.findLocation(locationId);
		};
	}

	public DataFetcher<LocationDetails> disableLocation() {
		return (environment) -> {
			logger.debug("Entering disableLocation@" + LocationDataFetcher.class.getSimpleName());
			Identifier locationId = new Identifier((String) environment.getArgument("locationId"));
			organizations.disableLocation(locationId);
			return organizations.findLocation(locationId);
		};
	}
	
	public DataFetcher<LocationDetails> updateLocationName() {
		return (environment) -> {
			logger.debug("Entering updateLocationName@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			String locationName = environment.getArgument("locationName");
			return organizations.updateLocationName(new Identifier(locationId), locationName);
		};
	}
	
	public DataFetcher<LocationDetails> updateLocationAddress() {
		return (environment) -> {
			logger.debug("Entering updateLocationName@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			MailingAddress address = extractMailingAddress(environment, "locationAddress");
			return organizations.updateLocationAddress(new Identifier(locationId), address);
		};
	}
}