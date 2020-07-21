package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.MapBuilder;
import ca.magex.crm.graphql.client.ModelBinder;

/**
 * Implementation of the CRM Organization Service which is backed by a GraphQL Server
 * 
 * @author Jonny
 */
public class GraphQLOrganizationService implements CrmOrganizationService {

	/** client used for making the GraphQL calls */
	private GraphQLClient graphQLClient;

	/**
	 * Constructs our new Organization Service requiring the given graphQL client for remoting
	 * 
	 * @param graphQLClient
	 */
	public GraphQLOrganizationService(GraphQLClient graphQLClient) {
		this.graphQLClient = graphQLClient;
	}

	@Override
	public OrganizationDetails createOrganization(String displayName, List<AuthenticationGroupIdentifier> authenticationGroupIds, List<BusinessGroupIdentifier> businessGroupIds) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"createOrganization",
						"createOrganization",
						new MapBuilder()
								.withEntry("displayName", displayName)
								.withEntry("authenticationGroupIds", authenticationGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.withEntry("businessGroupIds", businessGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		return ModelBinder.toOrganizationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationStatus",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withEntry("status", Status.ACTIVE)
								.build()));
	}

	@Override
	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		return ModelBinder.toOrganizationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationStatus",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withEntry("status", Status.INACTIVE)
								.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationName",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withEntry("displayName", name)
								.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationMainLocation",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withOptionalEntry("mainLocationId", Optional.ofNullable(locationId))
								.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationMainContact",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withOptionalEntry("mainContactId", Optional.ofNullable(personId))
								.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> authenticationGroupIds) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationAuthenticationGroups",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withEntry("authenticationGroupIds", authenticationGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> businessGroupIds) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateOrganizationBusinessGroups",
						"updateOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.withEntry("businessGroupIds", businessGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		return ModelBinder.toOrganizationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"findOrganizationSummary",
						"findOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.build()));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		return ModelBinder.toOrganizationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"findOrganization",
						"findOrganization",
						new MapBuilder()
								.withEntry("organizationId", organizationId.toString())
								.build()));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return ModelBinder.toLong(graphQLClient
				.performGraphQLQueryWithVariables(
						"countOrganizations",
						"countOrganizations",
						new MapBuilder()
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("authenticationGroupId", Optional.ofNullable(filter.getAuthenticationGroupId()))
								.withOptionalEntry("businessGroupId", Optional.ofNullable(filter.getBusinessGroupId()))
								.build()));
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toOrganizationDetails, graphQLClient
				.performGraphQLQueryWithVariables(
						"findOrganizationDetails",
						"findOrganizations",
						new MapBuilder()
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("authenticationGroupId", Optional.ofNullable(filter.getAuthenticationGroupId()))
								.withOptionalEntry("businessGroupId", Optional.ofNullable(filter.getBusinessGroupId()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toOrganizationSummary, graphQLClient
				.performGraphQLQueryWithVariables(
						"findOrganizationDetails",
						"findOrganizations",
						new MapBuilder()
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("authenticationGroupId", Optional.ofNullable(filter.getAuthenticationGroupId()))
								.withOptionalEntry("businessGroupId", Optional.ofNullable(filter.getBusinessGroupId()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}

}
