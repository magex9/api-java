package ca.magex.crm.graphql.datafetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

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

	public DataFetcher<Location> byId() {
		return (environment) -> {
			logger.info("Entering byId@" + LocationDataFetcher.class.getSimpleName());
			String id = environment.getArgument("id");
			return organizations.findLocation(new Identifier(id));
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

	public DataFetcher<Page<Location>> finder() {
		return (environment) -> {
			logger.info("Entering finder@" + LocationDataFetcher.class.getSimpleName());
			Paging paging = extractPaging(environment);		
			return organizations.findLocations(new LocationsFilter("", paging));
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
}