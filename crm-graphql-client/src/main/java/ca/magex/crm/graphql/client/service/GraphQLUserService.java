package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.MapBuilder;
import ca.magex.crm.graphql.client.ModelBinder;

/**
 * Implementation of the CRM User Service which is backed by a GraphQL Server
 * 
 * @author Jonny
 */
public class GraphQLUserService implements CrmUserService {

	/** client used for making the GraphQL calls */
	private GraphQLClient graphQLClient;

	/**
	 * Constructs our new User Service requiring the given graphQL client for remoting
	 * 
	 * @param graphQLClient
	 */
	public GraphQLUserService(GraphQLClient graphQLClient) {
		this.graphQLClient = graphQLClient;
	}

	@Override
	public UserDetails createUser(PersonIdentifier personId, String username, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		return ModelBinder.toUserDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"createUser",
						"createUser",
						new MapBuilder()
								.withEntry("personId", personId.toString())
								.withEntry("username", username)
								.withEntry("authenticationRoleIds", authenticationRoleIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public UserSummary enableUser(UserIdentifier userId) {
		return ModelBinder.toUserSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateUserStatus",
						"updateUser",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.withEntry("status", Status.ACTIVE)
								.build()));
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		return ModelBinder.toUserSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateUserStatus",
						"updateUser",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.withEntry("status", Status.INACTIVE)
								.build()));
	}

	@Override
	public UserDetails updateUserAuthenticationRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> authenticationRoleIds) {
		return ModelBinder.toUserDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateUserRoles",
						"updateUser",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.withEntry("authenticationRoleIds", authenticationRoleIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		return ModelBinder.toBoolean(graphQLClient
				.performGraphQLQueryWithVariables(
						"changeUserPassword",
						"changeUserPassword",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.withEntry("currentPassword", currentPassword)
								.withEntry("newPassword", newPassword)
								.build()));
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		return ModelBinder.toString(graphQLClient
				.performGraphQLQueryWithVariables(
						"resetUserPassword",
						"resetUserPassword",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.build()));
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		return ModelBinder.toUserSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"findUser",
						"findUserSummary",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.build()));
	}

	@Override
	public UserSummary findUserSummaryByUsername(String username) {
		try {
			return findUserSummaries(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		return ModelBinder.toUserDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"findUser",
						"findUser",
						new MapBuilder()
								.withEntry("userId", userId.toString())
								.build()));
	}

	@Override
	public UserDetails findUserDetailsByUsername(String username) {
		try {
			return findUserDetails(defaultUsersFilter().withUsername(username), UsersFilter.getDefaultPaging()).getSingleItem();
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return ModelBinder.toLong(graphQLClient
				.performGraphQLQueryWithVariables(
						"countUsers",
						"countUsers",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("personId", Optional.ofNullable(filter.getPersonId()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("username", Optional.ofNullable(filter.getUsername()))
								.withOptionalEntry("authenticationRoleId", Optional.ofNullable(filter.getAuthenticationRoleId()))
								.build()));
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toUserSummary, graphQLClient
				.performGraphQLQueryWithVariables(
						"findUserSummaries",
						"findUsers",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("personId", Optional.ofNullable(filter.getPersonId()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("username", Optional.ofNullable(filter.getUsername()))
								.withOptionalEntry("authenticationRoleId", Optional.ofNullable(filter.getAuthenticationRoleId()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toUserDetails, graphQLClient
				.performGraphQLQueryWithVariables(
						"findUserDetails",
						"findUsers",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("personId", Optional.ofNullable(filter.getPersonId()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withOptionalEntry("username", Optional.ofNullable(filter.getUsername()))
								.withOptionalEntry("authenticationRoleId", Optional.ofNullable(filter.getAuthenticationRoleId()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}
}
