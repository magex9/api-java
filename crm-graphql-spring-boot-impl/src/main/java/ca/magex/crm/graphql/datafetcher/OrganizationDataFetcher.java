package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.springframework.data.domain.Sort;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;

public class OrganizationDataFetcher {

	private OrganizationService organizations = null;
	
	public OrganizationDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}

	/**
	 * returns a data fetcher for retrieving a location by it's id
	 * @return
	 */
	public DataFetcher<Organization> byId() {
		return (environment) -> {
			String id = environment.getArgument("id");
			return organizations.findOrganization(new Identifier(id));
		};
	}
	
	/**
	 * returns a data fetcher for retrieving an organization by it's id
	 * @return
	 */
	public DataFetcher<List<Organization>> finder() {
		return (environment) -> {
			Integer offset = environment.getArgument("offset");
			Integer pageSize = environment.getArgument("pageSize");				
			Paging paging = new Paging(offset.longValue(), pageSize, Sort.by("organizationId"));
			List<Organization> results = organizations.findOrganizations(new OrganizationsFilter(null, paging));
			return results;
		};
	}
}
