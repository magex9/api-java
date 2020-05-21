package ca.magex.crm.graphql.datafetcher;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.graphql.util.PagingBuilder;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * Base class for the data fetchers with any common data
 * 
 * @author Jonny
 *
 */
public abstract class AbstractDataFetcher {

	private static Logger logger = LoggerFactory.getLogger(AbstractDataFetcher.class);

	@Autowired protected Crm crm;

	public DataFetcher<String> getNameByLocale(Locale locale) {
		return (environment) -> {
			logger.debug("Entering getNameByLocale@" + AbstractDataFetcher.class.getSimpleName());
			Object source = environment.getSource();
			Method getName = ReflectionUtils.findMethod(source.getClass(), "getName", Locale.class);
			return (String) ReflectionUtils.invokeMethod(getName, source, locale);
		};
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
				(String) addressMap.get("countryCode"),
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
				(String) nameMap.get("salutation"),
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
				(String) commsMap.get("language"),
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
				(String) businessMap.get("sector"),
				(String) businessMap.get("unit"),
				(String) businessMap.get("classification"));
	}
}