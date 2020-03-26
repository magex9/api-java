package ca.magex.crm.graphql.datafetcher;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import graphql.schema.DataFetcher;

public class OrganizationDataFetcher extends AbstractDataFetcher {

	public OrganizationDataFetcher(OrganizationService organizations) {
		super(organizations);
	}

	/**
	 * returns a data fetcher for retrieving a location by it's id
	 * 
	 * @return
	 */
	public DataFetcher<Organization> byId() {
		return (environment) -> {
			String id = environment.getArgument("organizationId");
			return organizations.findOrganization(new Identifier(id));
		};
	}

	/**
	 * returns a data fetcher for retrieving an organization by it's id
	 * 
	 * @return
	 */
	public DataFetcher<Page<Organization>> finder() {
		return (environment) -> {		
			Paging paging = extractPaging(environment);		
			return organizations.findOrganizations(new OrganizationsFilter(null, paging));			
		};
	}
}
