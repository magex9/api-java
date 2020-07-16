package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.stream.Collectors;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.FilteredPage;
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
							.withEntry("authenticationGroups", authenticationGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
							.withEntry("businessGroups", businessGroupIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
							.build()));
	}

	@Override
	public OrganizationSummary enableOrganization(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationSummary disableOrganization(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(OrganizationIdentifier organizationId, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(OrganizationIdentifier organizationId, LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(OrganizationIdentifier organizationId, PersonIdentifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationAuthenticationGroups(OrganizationIdentifier organizationId, List<AuthenticationGroupIdentifier> groupIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationBusinessGroups(OrganizationIdentifier organizationId, List<BusinessGroupIdentifier> groupIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

}
