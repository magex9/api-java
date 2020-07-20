package ca.magex.crm.graphql.client.service;

import java.util.Optional;
import java.util.stream.Collectors;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.MapBuilder;
import ca.magex.crm.graphql.client.ModelBinder;

/**
 * Implementation of the CRM Location Service which is backed by a GraphQL Server
 * 
 * @author Jonny
 */
public class GraphQLLocationService implements CrmLocationService {

	/** client used for making the GraphQL calls */
	private GraphQLClient graphQLClient;
	
	/**
	 * Constructs our new Location Service requiring the given graphQL client for remoting
	 * 
	 * @param graphQLClient
	 */
	public GraphQLLocationService(GraphQLClient graphQLClient) {
		this.graphQLClient = graphQLClient;
	}
	
	@Override
	public LocationDetails createLocation(OrganizationIdentifier organizationId, String reference, String displayName, MailingAddress address) {
		return ModelBinder.toLocationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"createLocation",
						"createLocation",
						new MapBuilder()
							.withEntry("organizationId", organizationId.getCode())
							.withEntry("reference", reference)
							.withEntry("displayName", displayName)
							.withEntry("street", address.getStreet())
							.withEntry("city", address.getCity())
							.withOptionalEntry("provinceIdentifier", Optional.ofNullable(address.getProvince().getIdentifier()))
							.withOptionalEntry("provinceOther", Optional.ofNullable(address.getProvince().getOther()))
							.withOptionalEntry("countryIdentifier", Optional.ofNullable(address.getCountry().getIdentifier()))
							.withOptionalEntry("countryOther", Optional.ofNullable(address.getCountry().getOther()))
							.withEntry("postalCode", address.getPostalCode())
							.build()));
	}

	@Override
	public LocationSummary enableLocation(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary disableLocation(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationName(LocationIdentifier locationId, String displaysName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

}
