package ca.magex.crm.graphql.datafetcher;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class OrganizationDataFetcher implements DataFetcher<Organization> {

	private OrganizationService organizations = null;
	
	public OrganizationDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}

	@Override
	public Organization get(DataFetchingEnvironment environment) {
		String id = environment.getArgument("id");
		return organizations.findOrganization(new Identifier(id));
//		return new MapBuilder()
//				.withEntry("id", id)
//				.withEntry("status", organization.getStatus().toString())
//				.withEntry("displayName", organization.getDisplayName())
//				.withEntry("mainLocation", organizations.findLocation(organization.getMainLocation()))
//				.build();
	}
}
