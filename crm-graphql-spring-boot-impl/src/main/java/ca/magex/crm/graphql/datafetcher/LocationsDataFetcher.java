package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.springframework.data.domain.Sort;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;


public class LocationsDataFetcher implements DataFetcher<List<Location>> {

	private OrganizationService organizations = null;
	
	public LocationsDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}

	@Override
	public List<Location> get(DataFetchingEnvironment environment) {
		Integer offset = environment.getArgument("offset");
		Integer pageSize = environment.getArgument("pageSize");				
		Paging paging = new Paging(offset.longValue(), pageSize, Sort.by("locationId"));
		List<Location> results = organizations.findLocations(new LocationsFilter("", paging));
		return results;
//		return new MapBuilder()
//				.withEntry("id", id)
//				.withEntry("status", organization.getStatus().toString())
//				.withEntry("displayName", organization.getDisplayName())
//				.withEntry("mainLocation", organizations.findLocation(organization.getMainLocation()))
//				.build();
	}
}
