package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

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
		return ModelBinder.toLocationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"enableLocation",
						"enableLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.toString())
								.build()));
	}

	@Override
	public LocationSummary disableLocation(LocationIdentifier locationId) {
		return ModelBinder.toLocationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"disableLocation",
						"disableLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.toString())
								.build()));
	}

	@Override
	public LocationDetails updateLocationName(LocationIdentifier locationId, String displayName) {
		return ModelBinder.toLocationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateLocationName",
						"updateLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.toString())
								.withEntry("displayName", displayName)
								.build()));
	}

	@Override
	public LocationDetails updateLocationAddress(LocationIdentifier locationId, MailingAddress address) {
		return ModelBinder.toLocationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updateLocationAddress",
						"updateLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.getCode())
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
	public LocationSummary findLocationSummary(LocationIdentifier locationId) {
		return ModelBinder.toLocationSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"findLocationSummary",
						"findLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.toString())
								.build()));
	}

	@Override
	public LocationDetails findLocationDetails(LocationIdentifier locationId) {
		return ModelBinder.toLocationDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"findLocation",
						"findLocation",
						new MapBuilder()
								.withEntry("locationId", locationId.toString())
								.build()));
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return ModelBinder.toLong(graphQLClient
				.performGraphQLQueryWithVariables(
						"countLocations",
						"countLocations",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("reference", Optional.ofNullable(filter.getReference()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.build()));
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toLocationDetails, graphQLClient
				.performGraphQLQueryWithVariables(
						"findLocationDetails",
						"findLocations",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("reference", Optional.ofNullable(filter.getReference()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toLocationSummary, graphQLClient
				.performGraphQLQueryWithVariables(
						"findLocationSummaries",
						"findLocations",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("reference", Optional.ofNullable(filter.getReference()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}
}