package ca.magex.crm.graphql.datafetcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.graphql.util.PagingBuilder;
import graphql.schema.DataFetchingEnvironment;

/**
 * Base class for the data fetchers with any common data
 * 
 * @author Jonny
 *
 */
public abstract class AbstractDataFetcher {	

	protected Crm crm = null;

	protected AbstractDataFetcher(Crm crm) {
		this.crm = crm;
	}

	/**
	 * extracts the filter from the environment
	 * 
	 * @param environment
	 * @return
	 */
	protected Map<String, Object> extractFilter(DataFetchingEnvironment environment) {
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
	 * 
	 * @param environment
	 * @param addressKey
	 * @return
	 */
	protected MailingAddress extractMailingAddress(DataFetchingEnvironment environment, String addressKey) {
		Map<String, Object> addressMap = environment.getArgument(addressKey);
		return new MailingAddress(
				(String) addressMap.get("street"),
				(String) addressMap.get("city"),
				(String) addressMap.get("province"),
				crm.findCountryByCode((String) addressMap.get("countryCode")),
				(String) addressMap.get("postalCode"));
	}

	/**
	 * extracts the person name from the environment
	 * 
	 * @param environment
	 * @param nameKey
	 * @return
	 */
	protected PersonName extractPersonName(DataFetchingEnvironment environment, String nameKey) {
		Map<String, Object> nameMap = environment.getArgument(nameKey);
		return new PersonName(
				crm.findSalutationByCode((String) nameMap.get("salutation")),
				(String) nameMap.get("firstName"),
				(String) nameMap.get("middleName"),
				(String) nameMap.get("lastName"));
	}

	/**
	 * extracts the communications from the environment
	 * 
	 * @param environment
	 * @param commsKey
	 * @return
	 */
	protected Communication extractCommunication(DataFetchingEnvironment environment, String commsKey) {
		Map<String, Object> commsMap = environment.getArgument(commsKey);
		return new Communication(
				(String) commsMap.get("jobTitle"),
				crm.findLanguageByCode((String) commsMap.get("language")),
				(String) commsMap.get("email"),
				new Telephone(
						(String) commsMap.get("phoneNumber"),
						(String) commsMap.get("phoneExtension")),
				(String) commsMap.get("faxNumber"));
	}

	/**
	 * Extracts the business position from the environment
	 * 
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected BusinessPosition extractBusinessPosition(DataFetchingEnvironment environment, String businessKey) {
		Map<String, Object> businessMap = environment.getArgument(businessKey);
		return new BusinessPosition(
				crm.findBusinessSectorByCode((String) businessMap.get("sector")),
				crm.findBusinessUnitByCode((String) businessMap.get("unit")),
				crm.findBusinessClassificationByCode((String) businessMap.get("classification")));
	}

	/**
	 * Extracts the role from the environment
	 * 
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected Role extractRole(DataFetchingEnvironment environment, String roleKey) {
		return crm.findRoleByCode(environment.getArgument(roleKey));
	}

	/**
	 * Extracts the role from the environment
	 * 
	 * @param environment
	 * @param businessKey
	 * @return
	 */
	protected List<Role> extractRoles(DataFetchingEnvironment environment, String roleKey) {
		List<String> roles = environment.getArgument(roleKey);
		return roles.stream().map(crm::findRoleByCode).collect(Collectors.toList());
	}
}