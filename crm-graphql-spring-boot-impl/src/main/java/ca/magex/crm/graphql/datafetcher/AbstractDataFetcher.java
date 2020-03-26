package ca.magex.crm.graphql.datafetcher;

import java.util.Map;

import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.graphql.util.PagingBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * Base class for the data fetchers with any common data 
 * @author Jonny
 *
 */
public abstract class AbstractDataFetcher {

	protected OrganizationService organizations = null;
	
	protected AbstractDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
	}
	
	/**
	 * extracts the paging input from the environment
	 * @param environment
	 * @return
	 */
	protected Paging extractPaging(DataFetchingEnvironment environment) {
		Map<String,Object> pagingMap = environment.getArgument("paging");
		
		return new PagingBuilder()
				.withPageNumber((Integer) pagingMap.get("pageNumber"))
				.withPageSize((Integer) pagingMap.get("pageSize"))
				.withSortField((String) pagingMap.get("sortField"))
				.withSortDirection((String) pagingMap.get("sortOrder"))
				.build();
	}
}
