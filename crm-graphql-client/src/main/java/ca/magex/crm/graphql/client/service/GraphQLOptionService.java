package ca.magex.crm.graphql.client.service;

import java.util.stream.Collectors;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.MapBuilder;
import ca.magex.crm.graphql.client.ModelBinder;

/**
 * Client side implementation of the CrmOptionService
 * 
 * @author Jonny
 */
public class GraphQLOptionService implements CrmOptionService {
	
	/** client used for making the GraphQL calls */
	private GraphQLClient graphQLClient;
	
	/**
	 * Constructs our new Organization Service requiring the given graphQL client for remoting
	 * 
	 * @param graphQLClient
	 */
	public GraphQLOptionService(GraphQLClient graphQLClient) {
		this.graphQLClient = graphQLClient;
	}	
	
	@Override
	public Option createOption(OptionIdentifier parentId, Type type, Localized name) {
		return ModelBinder.toOption(graphQLClient
				.performGraphQLQueryWithVariables(
						"createOption", 
						"createOption", 
						new MapBuilder()
						.withEntry("type", type.getCode())
						.withEntry("parentId", parentId == null ? "" : parentId.toString())
						.withEntry("code", name.getCode())
						.withEntry("english", name.getEnglishName())
						.withEntry("french", name.getFrenchName())
						.build()));
	}

	@Override
	public Option findOption(OptionIdentifier optionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
