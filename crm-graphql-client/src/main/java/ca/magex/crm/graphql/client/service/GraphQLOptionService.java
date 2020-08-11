package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
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
		return ModelBinder.toOption(graphQLClient
				.performGraphQLQueryWithVariables(
						"findOption",
						"findOption",
						new MapBuilder()
								.withEntry("optionId", optionId.toString())
								.build()));
	}

	@Override
	public Option findOptionByCode(Type type, String optionCode) {
		return findOption(type.generateId(optionCode));
	}

	@Override
	public Option updateOptionName(OptionIdentifier optionId, Localized name) {
		return ModelBinder.toOption(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOption",
						"updateOption",
						new MapBuilder()
								.withEntry("optionId", optionId.toString())
								.withOptionalEntry("english", Optional.ofNullable(name.getEnglishName()))
								.withOptionalEntry("french", Optional.ofNullable(name.getFrenchName()))
								.build()));
	}

	@Override
	public Option enableOption(OptionIdentifier optionId) {
		return ModelBinder.toOption(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOption",
						"updateOption",
						new MapBuilder()
								.withEntry("optionId", optionId.toString())
								.withEntry("status", Status.ACTIVE)
								.build()));
	}

	@Override
	public Option disableOption(OptionIdentifier optionId) {
		return ModelBinder.toOption(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOption",
						"updateOption",
						new MapBuilder()
								.withEntry("optionId", optionId.toString())
								.withEntry("status", Status.INACTIVE)
								.build()));
	}

	@Override
	public long countOptions(OptionsFilter filter) {
		return ModelBinder.toLong(graphQLClient
				.performGraphQLQueryWithVariables(
						"countOptions",
						"countOptions",
						new MapBuilder()
								.withOptionalEntry("parentId", Optional.ofNullable(filter.getParentId()))
								.withOptionalEntry("type", Optional.ofNullable(filter.getTypeCode()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("code", Optional.ofNullable(filter.getCode()))
								.withOptionalEntry("english", Optional.ofNullable(filter.getEnglishName()))
								.withOptionalEntry("french", Optional.ofNullable(filter.getFrenchName()))
								.build()));
	}

	@Override
	public FilteredPage<Option> findOptions(OptionsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toOption, graphQLClient
				.performGraphQLQueryWithVariables(
						"findOptions",
						"findOptions",
						new MapBuilder()
								.withOptionalEntry("parentId", Optional.ofNullable(filter.getParentId()))
								.withOptionalEntry("type", Optional.ofNullable(filter.getTypeCode()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("code", Optional.ofNullable(filter.getCode()))
								.withOptionalEntry("english", Optional.ofNullable(filter.getEnglishName()))
								.withOptionalEntry("french", Optional.ofNullable(filter.getFrenchName()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}
}