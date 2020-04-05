package ca.magex.crm.graphql.client;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

/**
 * Implementation of the Organization Service that uses a GraphQL Server
 * 
 * @author Jonny
 */
public class OrganizationServiceGraphQLClient extends GraphQLClient implements CrmOrganizationService, CrmLocationService, CrmPersonService {

	/**
	 * constructs a new Service for the given graphql endpoint
	 * 
	 * @param endpoint
	 */
	public OrganizationServiceGraphQLClient(String endpoint) {
		super(endpoint, "/organization-service-queries.properties");
	}

	@Override
	public OrganizationDetails createOrganization(String organizationName) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"createOrganization",
				"createOrganization",
				organizationName));
	}

	@Override
	public OrganizationDetails enableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"enableOrganization",
				"enableOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails disableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"disableOrganization",
				"disableOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails updateOrganizationName(Identifier organizationId, String name) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"updateOrganizationName",
				"updateOrganization",
				organizationId,
				name));
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"updateOrganizationMainLocation",
				"updateOrganization",
				organizationId,
				locationId));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return ModelBinder.toOrganizationSummary(performGraphQLQuery(
				"findOrganization",
				"findOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery(
				"findOrganization",
				"findOrganization",
				organizationId));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQuery(
				"countOrganizations",
				"countOrganizations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationSummary, performGraphQLQuery(
				"findOrganizationSummaries",
				"findOrganizations",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationDetails, performGraphQLQuery(
				"findOrganizationDetails",
				"findOrganizations",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQuery(
				"createLocation",
				"createLocation",
				organizationId,
				locationName,
				locationReference,
				address == null ? null : address.getStreet(),
				address == null ? null : address.getCity(),
				address == null ? null : address.getProvince(),
				address == null ? null : address.getCountry().getCode(),
				address == null ? null : address.getPostalCode()));
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		return ModelBinder.toLocationDetails(performGraphQLQuery(
				"updateLocationName",
				"updateLocation",
				locationId,
				locationName));
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQuery(
				"updateLocationAddress",
				"updateLocation",
				locationId,
				address == null ? null : address.getStreet(),
				address == null ? null : address.getCity(),
				address == null ? null : address.getProvince(),
				address == null ? null : address.getCountry().getCode(),
				address == null ? null : address.getPostalCode()));
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQuery(
				"enableLocation",
				"enableLocation",
				locationId));
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQuery(
				"disableLocation",
				"disableLocation",
				locationId));
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQuery(
				"findLocation",
				"findLocation",
				locationId));
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		return ModelBinder.toLocationDetails(performGraphQLQuery(
				"findLocation",
				"findLocation",
				locationId));
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQuery(
				"countLocations",
				"countLocations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toLocationDetails, performGraphQLQuery(
				"findLocationDetails",
				"findLocations",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toLocationSummary, performGraphQLQuery(
				"findLocationSummaries",
				"findLocations",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"createPerson",
				"createPerson",
				organizationId,
				name.getFirstName(),
				name.getMiddleName(),
				name.getLastName(),
				name.getSalutation().getCode(),
				address.getStreet(),
				address.getCity(),
				address.getProvince(),
				address.getCountry().getCode(),
				address.getPostalCode(),
				communication.getJobTitle(),
				communication.getLanguage().getCode(),
				communication.getEmail(),
				communication.getHomePhone().getNumber(),
				communication.getHomePhone().getExtension(),
				communication.getFaxNumber(),
				position.getSector().getCode(),
				position.getUnit().getCode(),
				position.getClassification().getCode()));
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"updatePersonName",
				"updatePerson",
				personId,
				name.getFirstName(),
				name.getMiddleName(),
				name.getLastName(),
				name.getSalutation().getCode()));
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"updatePersonAddress",
				"updatePerson",
				personId,
				address.getStreet(),
				address.getCity(),
				address.getProvince(),
				address.getCountry().getCode(),
				address.getPostalCode()));
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"updatePersonCommunication",
				"updatePerson",
				personId,
				communication.getJobTitle(),
				communication.getLanguage().getCode(),
				communication.getEmail(),
				communication.getHomePhone().getNumber(),
				communication.getHomePhone().getExtension(),
				communication.getFaxNumber()));
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"updatePersonBusinessUnit",
				"updatePerson",
				personId,
				position.getSector().getCode(),
				position.getUnit().getCode(),
				position.getClassification().getCode()));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQuery(
				"enablePerson",
				"enablePerson",
				personId));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQuery(
				"disablePerson",
				"disablePerson",
				personId));
	}

	@Override
	public PersonDetails addUserRole(Identifier personId, Role role) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"addUserRole",
				"addUserRole",
				personId,
				role.getCode()));
	}

	@Override
	public PersonDetails removeUserRole(Identifier personId, Role role) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"removeUserRole",
				"removeUserRole",
				personId,
				role.getCode()));
	}
	
	@Override
	public PersonDetails setUserRoles(Identifier personId, List<Role> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQuery(
				"findPerson",
				"findPerson",
				personId));
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return ModelBinder.toPersonDetails(performGraphQLQuery(
				"findPerson",
				"findPerson",
				personId));
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return ModelBinder.toLong(performGraphQLQuery(
				"countPersons",
				"countPersons",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toPersonSummary, performGraphQLQuery(
				"findPersonSummaries",
				"findPersons",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toPersonDetails, performGraphQLQuery(
				"findPersonDetails",
				"findPersons",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}	
}