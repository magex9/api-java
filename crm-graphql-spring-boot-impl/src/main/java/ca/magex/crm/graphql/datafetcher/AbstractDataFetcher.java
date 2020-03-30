package ca.magex.crm.graphql.datafetcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.graphql.util.PagingBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * Base class for the data fetchers with any common data
 * 
 * @author Jonny
 *
 */
public abstract class AbstractDataFetcher {

	protected Properties countryLookup = new Properties();

	protected OrganizationService organizations = null;

	protected AbstractDataFetcher(OrganizationService organizations) {
		this.organizations = organizations;
		URL countries = getClass().getResource("/countries.properties");
		try (InputStream c = countries.openStream()) {
			this.countryLookup.load(c);
		} catch (IOException ioe) {
			throw new RuntimeException("Error loading countries.properties");
		}
	}
	
	/**
	 * extracts the filter from the environment
	 * @param environment
	 * @return
	 */
	protected Map<String,Object> extractFilter(DataFetchingEnvironment environment) {
		return environment.getArgument("filter");
	}

	/**
	 * extracts the paging input from the environment
	 * 
	 * @param environment
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Paging extractPaging(DataFetchingEnvironment environment) {
		Map<String, Object> pagingMap = environment.getArgument("paging");
		List<String> sortFields = (List<String>) pagingMap.get("sortField");
		List<String> sortOrders = (List<String>) pagingMap.get("sortOrder");

		return new PagingBuilder()
				.withPageNumber((Integer) pagingMap.get("pageNumber"))
				.withPageSize((Integer) pagingMap.get("pageSize"))
				.withSortFields(sortFields)
				.withSortDirections(sortOrders)
				.build();
	}

	/**
	 * extracts the mailing address from the environment
	 * @param environment
	 * @param addressKey
	 * @return
	 */
	protected MailingAddress extractMailingAddress(DataFetchingEnvironment environment, String addressKey) {
		Map<String,Object> addressMap = environment.getArgument(addressKey);
		return new MailingAddress(
				(String) addressMap.get("street"), 
				(String) addressMap.get("city"), 
				(String) addressMap.get("province"), 
				new Country(
						(String) addressMap.get("countryCode"), 
						countryLookup.getProperty((String) addressMap.get("countryCode"))), 
				(String) addressMap.get("postalCode"));
	}
}