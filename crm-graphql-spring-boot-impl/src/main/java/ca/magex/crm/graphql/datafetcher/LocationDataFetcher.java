package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.springframework.data.domain.Sort;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;

public class LocationDataFetcher {

	private OrganizationService organizations = null;
	
	public LocationDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	/**
	 * returns a data fetcher for retrieving a location by it's id
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
	 * @return
	 */
	public DataFetcher<Location> byOrganization() {
		return (environment) -> {
			Organization organization = environment.getSource();
			return organizations.findLocation(organization.getMainLocation());
		};
	}
	
	/**
	 * returns a data fetcher for finding locations
	 * @return
	 */
	public DataFetcher<List<Location>> finder() {
		return (environment) -> {
			Integer offset = environment.getArgument("offset");
			Integer pageSize = environment.getArgument("pageSize");				
			Paging paging = new Paging(offset.longValue(), pageSize, Sort.by("locationId"));
			List<Location> results = organizations.findLocations(new LocationsFilter("", paging));
			return results;
		};
	}
}
