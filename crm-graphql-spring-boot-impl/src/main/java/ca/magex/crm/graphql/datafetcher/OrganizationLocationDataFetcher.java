package ca.magex.crm.graphql.datafetcher;

import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.OrganizationService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class OrganizationLocationDataFetcher implements DataFetcher<Location> {

	private OrganizationService organizations = null;

	public OrganizationLocationDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}

	@Override
	public Location get(DataFetchingEnvironment environment) {
		Organization organization = environment.getSource();
		return organizations.findLocation(organization.getMainLocation());
	}
}
