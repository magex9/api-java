package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserSummary disableUser(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetails updateUserRoles(UserIdentifier userId, List<AuthenticationRoleIdentifier> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePassword(UserIdentifier userId, String currentPassword, String newPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String resetPassword(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetails findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countUsers(UsersFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}
}
