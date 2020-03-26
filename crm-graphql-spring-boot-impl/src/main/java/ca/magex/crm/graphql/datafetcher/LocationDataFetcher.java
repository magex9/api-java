package ca.magex.crm.graphql.datafetcher;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;

public class LocationDataFetcher extends AbstractDataFetcher {

	public LocationDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	/**
	 * returns a data fetcher for retrieving a location by it's id
	 * 
	 * @return
	 */
	public DataFetcher<Location> byId() {
		return (environment) -> {
			String id = environment.getArgument("id");
			return organizations.findLocation(new Identifier(id));
		};
	}

	/**
	 * returns a data fetcher for retrieving a location by it's id
	 * 
	 * @return
	 */
	public DataFetcher<Location> byOrganization() {
		return (environment) -> {
			Organization organization = environment.getSource();
			return organizations.findLocation(organization.getMainLocationId());
		};
	}

	/**
	 * returns a data fetcher for finding locations
	 * 
	 * @return
	 */
	public DataFetcher<Page<Location>> finder() {
		return (environment) -> {
			Paging paging = extractPaging(environment);		
			return organizations.findLocations(new LocationsFilter("", paging));
		};
	}
}
