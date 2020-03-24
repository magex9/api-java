package ca.magex.crm.graphql.datafetcher;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class LocationDataFetcher implements DataFetcher<Location> {

	private OrganizationService organizations = null;
	
	public LocationDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	@Override
	public Location get(DataFetchingEnvironment environment) {
		String id = environment.getArgument("id");
		return organizations.findLocation(new Identifier(id));
	}
}
