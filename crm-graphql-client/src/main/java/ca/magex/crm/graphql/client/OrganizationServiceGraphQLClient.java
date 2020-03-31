package ca.magex.crm.graphql.client;

import java.util.List;

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
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.OrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Role;

/**
 * Implementation of the Organization Service that uses a GraphQL Server
 * 
 * @author Jonny
 */
public class OrganizationServiceGraphQLClient extends GraphQLClient implements OrganizationService {

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
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("createOrganization",
				organizationName));
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("enableOrganization",
				organizationId));
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("disableOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails updateOrganizationName(Identifier organizationId, String name) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("updateOrganizationName",
				organizationId,
				name));
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("updateOrganizationMainLocation",
				organizationId,
				locationId));
	}

	@Override
	public OrganizationDetails findOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQuery("findOrganization",
				organizationId));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQuery("countOrganizations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationSummary, performGraphQLQuery("findOrganizationSummaries",
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
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationDetails, performGraphQLQuery("findOrganizationDetails",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public LocationDetails createLocation(Identifier organizationId, String locationName, String locationReference, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQuery("createLocation",
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
		return ModelBinder.toLocationDetails(performGraphQLQuery("updateLocationName",
				locationId,
				locationName));
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQuery("updateLocationAddress",
				locationId,
				address == null ? null : address.getStreet(),
				address == null ? null : address.getCity(),
				address == null ? null : address.getProvince(),
				address == null ? null : address.getCountry().getCode(),
				address == null ? null : address.getPostalCode()));
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQuery("enableLocation", 
				locationId));
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQuery("disableLocation", 
				locationId));
	}

	@Override
	public LocationDetails findLocation(Identifier locationId) {
		return ModelBinder.toLocationDetails(performGraphQLQuery("findLocation",
				locationId));
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQuery("countLocations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toLocationDetails, performGraphQLQuery("findLocationDetails",
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
		return ModelBinder.toPage(paging, ModelBinder::toLocationSummary, performGraphQLQuery("findLocationSummaries",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return ModelBinder.toPersonDetails(performGraphQLQuery("createPerson",
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails updatePersonBusinessUnit(Identifier personId, BusinessPosition unit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails addUserRole(Identifier personId, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails removeUserRole(Identifier personId, Role role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails findPerson(Identifier personId) {
		return ModelBinder.toPersonDetails(performGraphQLQuery("findPerson",
				personId));
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(OrganizationDetails organization) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(LocationDetails location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(PersonDetails person) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> validate(List<Role> roles) {
		// TODO Auto-generated method stub
		return null;
	}

}