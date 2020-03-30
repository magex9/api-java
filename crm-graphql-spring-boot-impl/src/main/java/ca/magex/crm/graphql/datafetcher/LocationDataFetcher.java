package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
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

	public DataFetcher<Location> findLocation() {
		return (environment) -> {
			logger.info("Entering findLocation@" + LocationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("id");
			return organizations.findLocation(new Identifier(id));
		};
	}
	
	public DataFetcher<Integer> countLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return (int) organizations.countLocations(new LocationsFilter(
					extractFilter(environment), 
					new Paging(1, Integer.MAX_VALUE, Sort.by("displayName"))));
		};
	}
	
	public DataFetcher<Page<Location>> findLocations() {
		return (environment) -> {
			logger.info("Entering findLocations@" + LocationDataFetcher.class.getSimpleName());
			return organizations.findLocations(new LocationsFilter(
					extractFilter(environment), 
					extractPaging(environment)));
		};
	}

	public DataFetcher<Location> byOrganization() {
		return (environment) -> {
			logger.info("Entering byOrganization@" + LocationDataFetcher.class.getSimpleName());
			Organization organization = environment.getSource();
			if (organization.getMainLocationId() != null) {
				return organizations.findLocation(organization.getMainLocationId());
			}
			else {
				return null;
			}
		};
	}
	
	public DataFetcher<Location> createLocation() { 
		return (environment) -> {
			logger.info("Entering createLocation@" + LocationDataFetcher.class.getSimpleName());
			return organizations.createLocation(
					new Identifier((String) environment.getArgument("organizationId")), 
					environment.getArgument("locationName"), 
					environment.getArgument("locationReference"), 
					extractMailingAddress(environment, "locationAddress"));
		};
	}
	
	public DataFetcher<Location> enableLocation() {
		return (environment) -> {
			logger.debug("Entering enableLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return organizations.enableLocation(new Identifier(locationId));
		};
	}

	public DataFetcher<Location> disableLocation() {
		return (environment) -> {
			logger.debug("Entering disableLocation@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			return organizations.disableLocation(new Identifier(locationId));
		};
	}
	
	public DataFetcher<Location> updateLocationName() {
		return (environment) -> {
			logger.debug("Entering updateLocationName@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			String locationName = environment.getArgument("locationName");
			return organizations.updateLocationName(new Identifier(locationId), locationName);
		};
	}
	
	public DataFetcher<Location> updateLocationAddress() {
		return (environment) -> {
			logger.debug("Entering updateLocationName@" + LocationDataFetcher.class.getSimpleName());
			String locationId = environment.getArgument("locationId");
			MailingAddress address = extractMailingAddress(environment, "address");
			return organizations.updateLocationAddress(new Identifier(locationId), address);
		};
	}
}