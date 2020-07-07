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

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.api.system.id.SalutationIdentifier;
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
	@SuppressWarnings("unchecked")
	protected MailingAddress extractMailingAddress(DataFetchingEnvironment environment, String addressKey) {
		Map<String, Object> addressMap = environment.getArgument(addressKey);		
		return new MailingAddress(
				(String) addressMap.get("street"),
				(String) addressMap.get("city"),
				extractProvince((Map<String,Object>) addressMap.get("province")),
				extractCountry((Map<String,Object>) addressMap.get("country")),
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
				extractSalutation((String) nameMap.get("salutation")),
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
				extractLanguage((String) commsMap.get("language")),
				(String) commsMap.get("email"),
				new Telephone(
						(String) commsMap.get("phoneNumber"),
						(String) commsMap.get("phoneExtension")),
				(String) commsMap.get("faxNumber"));
	}
	
	/**
	 * Extracts the authentication groups and converts them to AuthenticationGroupIdentifiers
	 * @param environment
	 * @param groupsId
	 * @return
	 */
	public List<AuthenticationGroupIdentifier> extractAuthenticationGroups(DataFetchingEnvironment environment, String groupsId) {
		List<String> groups = environment.getArgument(groupsId);
		return groups.stream().map((group) -> new AuthenticationGroupIdentifier(group)).collect(Collectors.toList());
	}
	
	/**
	 * Extracts the business groups and converts them to BusinessGroupIdentifier
	 * @param environment
	 * @param groupsId
	 * @return
	 */
	public List<BusinessGroupIdentifier> extractBusinessGroups(DataFetchingEnvironment environment, String groupsId) {
		List<String> groups = environment.getArgument(groupsId);
		return groups.stream().map((group) -> new BusinessGroupIdentifier(group)).collect(Collectors.toList());
	}
	
	/**
	 * Extracts the roles and converts them to AuthenticationRoleIdentifiers
	 * @param environment
	 * @param rolesId
	 * @return
	 */
	public List<AuthenticationRoleIdentifier> extractAuthenticationRoles(DataFetchingEnvironment environment, String rolesId) {
		List<String> roles = environment.getArgument(rolesId);
		return roles.stream().map((role) -> new AuthenticationRoleIdentifier(role)).collect(Collectors.toList());
	}
	
	/**
	 * Extracts the roles and converts them to BusinessRoleIdentifiers
	 * @param environment
	 * @param rolesId
	 * @return
	 */
	public List<BusinessRoleIdentifier> extractBusinessRoles(DataFetchingEnvironment environment, String rolesId) {
		List<String> roles = environment.getArgument(rolesId);
		return roles.stream().map((role) -> new BusinessRoleIdentifier(role)).collect(Collectors.toList());
	}
	
	/**
	 * Builds the Choice based on the input provided
	 * @param input
	 * @return
	 */
	private Choice<ProvinceIdentifier> extractProvince(Map<String,Object> input) {
		if (input.containsKey("code")) {
			return new Choice<>(crm.findOptionByCode(Type.PROVINCE, (String) input.get("code")).getOptionId());			
		}
		else {
			return new Choice<>((String) input.getOrDefault("other", ""));
		}
	}
	
	/**
	 * Builds the Choice based on the input provided
	 * @param input
	 * @return
	 */
	private Choice<CountryIdentifier> extractCountry(Map<String,Object> input) {
		if (input.containsKey("code")) {
			return new Choice<>(crm.findOptionByCode(Type.COUNTRY, (String) input.get("code")).getOptionId());
		}
		else {
			return new Choice<>((String) input.getOrDefault("other", ""));
		}
	}
	
	/**
	 * Builds the choice based on the input provided
	 * @param input
	 * @return
	 */
	private Choice<SalutationIdentifier> extractSalutation(String input) {
		try {
			return new Choice<>(crm.findOptionByCode(Type.SALUTATION, input).getOptionId());			
		}
		catch(ItemNotFoundException e) {
			return new Choice<>(input);
		}
	}
	
	/**
	 * Builds the choice based on the input provided
	 * @param input
	 * @return
	 */
	private Choice<LanguageIdentifier> extractLanguage(String input) {
		try {
			return new Choice<>(crm.findOptionByCode(Type.LANGUAGE, input).getOptionId());			
		}
		catch(ItemNotFoundException e) {
			return new Choice<>(input);
		}
	}
}