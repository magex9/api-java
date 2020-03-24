package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.springframework.data.domain.Sort;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;


public class OrganizationsDataFetcher implements DataFetcher<List<Organization>> {

	private OrganizationService organizations = null;
	
	public OrganizationsDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}

	@Override
	public List<Organization> get(DataFetchingEnvironment environment) {
		Integer offset = environment.getArgument("offset");
		Integer pageSize = environment.getArgument("pageSize");				
		Paging paging = new Paging(offset.longValue(), pageSize, Sort.by("organizationId"));
		List<Organization> results = organizations.findOrganizations(new OrganizationsFilter(null, paging));
		return results;
//		return new MapBuilder()
//				.withEntry("id", id)
//				.withEntry("status", organization.getStatus().toString())
//				.withEntry("displayName", organization.getDisplayName())
//				.withEntry("mainLocation", organizations.findLocation(organization.getMainLocation()))
//				.build();
	}
}
