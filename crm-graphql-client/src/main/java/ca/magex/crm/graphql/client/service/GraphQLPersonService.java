package ca.magex.crm.graphql.client.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.MapBuilder;
import ca.magex.crm.graphql.client.ModelBinder;

/**
 * Implementation of the CRM Person Service which is backed by a GraphQL Server
 * 
 * @author Jonny
 */
public class GraphQLPersonService implements CrmPersonService {

	/** client used for making the GraphQL calls */
	private GraphQLClient graphQLClient;

	/**
	 * Constructs our new Person Service requiring the given graphQL client for remoting
	 * 
	 * @param graphQLClient
	 */
	public GraphQLPersonService(GraphQLClient graphQLClient) {
		this.graphQLClient = graphQLClient;
	}

	@Override
	public PersonDetails createPerson(OrganizationIdentifier organizationId, String displayName, PersonName name, MailingAddress address, Communication communication, List<BusinessRoleIdentifier> businessRoleIds) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"createPerson",
						"createPerson",
						new MapBuilder()
								.withEntry("organizationId", organizationId.getCode())
								.withEntry("displayName", displayName)
								.withEntry("firstName", name.getFirstName())
								.withEntry("middleName", name.getMiddleName())
								.withEntry("lastName", name.getLastName())
								.withOptionalEntry("salutationIdentifier", Optional.ofNullable(name.getSalutation().getIdentifier()))
								.withOptionalEntry("salutationOther", Optional.ofNullable(name.getSalutation().getOther()))
								.withEntry("street", address.getStreet())
								.withEntry("city", address.getCity())
								.withOptionalEntry("provinceIdentifier", Optional.ofNullable(address.getProvince().getIdentifier()))
								.withOptionalEntry("provinceOther", Optional.ofNullable(address.getProvince().getOther()))
								.withOptionalEntry("countryIdentifier", Optional.ofNullable(address.getCountry().getIdentifier()))
								.withOptionalEntry("countryOther", Optional.ofNullable(address.getCountry().getOther()))
								.withEntry("postalCode", address.getPostalCode())
								.withEntry("jobTitle", communication.getJobTitle())
								.withOptionalEntry("languageIdentifier", Optional.ofNullable(communication.getLanguage().getIdentifier()))
								.withOptionalEntry("languageOther", Optional.ofNullable(communication.getLanguage().getOther()))
								.withEntry("email", communication.getEmail())
								.withEntry("phoneNumber", communication.getHomePhone().getNumber())
								.withEntry("phoneExtension", communication.getHomePhone().getExtension())
								.withEntry("faxNumber", communication.getFaxNumber())
								.withEntry("businessRoleIds", businessRoleIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public PersonSummary enablePerson(PersonIdentifier personId) {
		return ModelBinder.toPersonSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonStatus",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("status", Status.ACTIVE)
								.build()));
	}

	@Override
	public PersonSummary disablePerson(PersonIdentifier personId) {
		return ModelBinder.toPersonSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonStatus",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("status", Status.INACTIVE)
								.build()));
	}

	@Override
	public PersonDetails updatePersonDisplayName(PersonIdentifier personId, String displayName) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonDisplayName",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("displayName", displayName)
								.build())); 
	}
	
	@Override
	public PersonDetails updatePersonLegalName(PersonIdentifier personId, PersonName name) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonLegalName",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("firstName", name.getFirstName())
								.withEntry("middleName", name.getMiddleName())
								.withEntry("lastName", name.getLastName())
								.withOptionalEntry("salutationIdentifier", Optional.ofNullable(name.getSalutation().getIdentifier()))
								.withOptionalEntry("salutationOther", Optional.ofNullable(name.getSalutation().getOther()))
								.build()));
	}

	@Override
	public PersonDetails updatePersonAddress(PersonIdentifier personId, MailingAddress address) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonAddress",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
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
	public PersonDetails updatePersonCommunication(PersonIdentifier personId, Communication communication) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonCommunication",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("jobTitle", communication.getJobTitle())
								.withOptionalEntry("languageIdentifier", Optional.ofNullable(communication.getLanguage().getIdentifier()))
								.withOptionalEntry("languageOther", Optional.ofNullable(communication.getLanguage().getOther()))
								.withEntry("email", communication.getEmail())
								.withEntry("phoneNumber", communication.getHomePhone().getNumber())
								.withEntry("phoneExtension", communication.getHomePhone().getExtension())
								.withEntry("faxNumber", communication.getFaxNumber())
								.build()));
	}

	@Override
	public PersonDetails updatePersonBusinessRoles(PersonIdentifier personId, List<BusinessRoleIdentifier> businessRoleIds) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"updatePersonBusinessRoles",
						"updatePerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.withEntry("businessRoleIds", businessRoleIds.stream().map((id) -> id.getCode()).collect(Collectors.toList()))
								.build()));
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		return ModelBinder.toPersonSummary(graphQLClient
				.performGraphQLQueryWithVariables(
						"findPersonSummary",
						"findPerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.build()));
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		return ModelBinder.toPersonDetails(graphQLClient
				.performGraphQLQueryWithVariables(
						"findPerson",
						"findPerson",
						new MapBuilder()
								.withEntry("personId", personId.getCode())
								.build()));
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return ModelBinder.toLong(graphQLClient
				.performGraphQLQueryWithVariables(
						"countPersons",
						"countPersons",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.build()));
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toPersonSummary, graphQLClient
				.performGraphQLQueryWithVariables(
						"findPersonSummaries",
						"findPersons",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toPersonDetails, graphQLClient
				.performGraphQLQueryWithVariables(
						"findPersonDetails",
						"findPersons",
						new MapBuilder()
								.withOptionalEntry("organizationId", Optional.ofNullable(filter.getOrganizationId()))
								.withOptionalEntry("displayName", Optional.ofNullable(filter.getDisplayName()))
								.withOptionalEntry("status", Optional.ofNullable(filter.getStatusCode()))
								.withEntry("pageNumber", paging.getPageNumber())
								.withEntry("pageSize", paging.getPageSize())
								.withEntry("sortField", sortInfo.getLeft())
								.withEntry("sortOrder", sortInfo.getRight())
								.build()));
	}
}
