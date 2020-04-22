package ca.magex.crm.graphql.client;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.StringUtils;
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
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

/**
 * Implementation of the Organization Service that uses a GraphQL Server
 * 
 * @author Jonny
 */
public class CrmServicesGraphQLClientImpl extends GraphQLClient implements CrmServices {

	/**
	 * constructs a new Service for the given graphql endpoint
	 * 
	 * @param endpoint
	 */
	public CrmServicesGraphQLClientImpl(String endpoint) {
		super(endpoint, "/organization-service-queries.properties");
	}

	@Override
	public OrganizationDetails createOrganization(String organizationDisplayName) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithSubstitution(
				"createOrganization",
				"createOrganization",
				organizationDisplayName));
	}

	@Override
	public OrganizationDetails enableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithSubstitution(
				"enableOrganization",
				"enableOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails disableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithSubstitution(
				"disableOrganization",
				"disableOrganization",
				organizationId));
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithSubstitution(
				"updateOrganizationDisplayName",
				"updateOrganization",
				organizationId,
				name));
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithSubstitution(
				"updateOrganizationMainLocation",
				"updateOrganization",
				organizationId,
				locationId));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {		
		return ModelBinder.toOrganizationSummary(performGraphQLQueryWithVariables(
				"findOrganizationSummary",
				"findOrganization",
				new MapBuilder().withEntry("id", organizationId.toString()).build()));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"findOrganization",
				"findOrganization",
				new MapBuilder().withEntry("id", organizationId.toString()).build()));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQueryWithSubstitution(
				"countOrganizations",
				"countOrganizations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationSummary, performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPage(paging, ModelBinder::toOrganizationDetails, performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toLocationDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toLocationDetails(performGraphQLQueryWithSubstitution(
				"updateLocationName",
				"updateLocation",
				locationId,
				locationName));
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toLocationSummary(performGraphQLQueryWithSubstitution(
				"enableLocation",
				"enableLocation",
				locationId));
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQueryWithSubstitution(
				"disableLocation",
				"disableLocation",
				locationId));
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQueryWithVariables(
				"findLocationSummary",
				"findLocation",
				new MapBuilder().withEntry("id", locationId.toString()).build()));
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
		return ModelBinder.toLocationDetails(performGraphQLQueryWithVariables(
				"findLocation",
				"findLocation",
				new MapBuilder().withEntry("id", locationId.toString()).build()));
	}

	@Override
	public long countLocations(LocationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQueryWithSubstitution(
				"countLocations",
				"countLocations",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toLocationDetails, performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPage(paging, ModelBinder::toLocationSummary, performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"updatePersonBusinessUnit",
				"updatePerson",
				personId,
				position.getSector().getCode(),
				position.getUnit().getCode(),
				position.getClassification().getCode()));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQueryWithSubstitution(
				"enablePerson",
				"enablePerson",
				personId));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQueryWithSubstitution(
				"disablePerson",
				"disablePerson",
				personId));
	}

	@Override
	public PersonDetails addUserRole(Identifier personId, Role role) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"addUserRole",
				"addUserRole",
				personId,
				role.getCode()));
	}

	@Override
	public PersonDetails removeUserRole(Identifier personId, Role role) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"removeUserRole",
				"removeUserRole",
				personId,
				role.getCode()));
	}
	
	@Override
	public PersonDetails setUserRoles(Identifier personId, List<Role> roles) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"setUserRoles",
				"setUserRoles",
				personId,
				roles.stream().map((r) -> r.getCode()).collect(Collectors.toList())));
	}
	
	@Override
	public PersonDetails setUserPassword(Identifier personId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQueryWithVariables(
				"findPersonSummary",
				"findPerson",
				new MapBuilder().withEntry("id", personId.toString()).build()));
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithVariables(
				"findPerson",
				"findPerson",
				new MapBuilder().withEntry("id", personId.toString()).build()));
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return ModelBinder.toLong(performGraphQLQueryWithSubstitution(
				"countPersons",
				"countPersons",
				filter.getDisplayName(),
				filter.getStatus()));
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(paging, ModelBinder::toPersonSummary, performGraphQLQueryWithSubstitution(
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
		return ModelBinder.toPage(paging, ModelBinder::toPersonDetails, performGraphQLQueryWithSubstitution(
				"findPersonDetails",
				"findPersons",
				filter.getDisplayName(),
				filter.getStatus(),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public List<Status> findStatuses() {
		return ModelBinder.toList(ModelBinder::toStatus, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"Status"));
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toStatus, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"Status",
				code)).get(0);
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findStatuses);
	}

	@Override
	public List<Role> findRoles() {
		return ModelBinder.toList(ModelBinder::toRole, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"Role"));
	}

	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toRole, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"Role",
				code)).get(0);
	}

	@Override
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findRoles);
	}

	@Override
	public List<Country> findCountries() {
		return ModelBinder.toList(ModelBinder::toCountry, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"Country"));
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toCountry, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"Country",
				code)).get(0);
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findCountries);
	}

	@Override
	public List<Salutation> findSalutations() {
		return ModelBinder.toList(ModelBinder::toSalutation, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"Salutation"));
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toSalutation, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"Salutation",
				code)).get(0);
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findSalutations);
	}

	@Override
	public List<Language> findLanguages() {
		return ModelBinder.toList(ModelBinder::toLanguage, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"Language"));
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toLanguage, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"Language",
				code)).get(0);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findLanguages);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return ModelBinder.toList(ModelBinder::toBusinessSector, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"BusinessSector"));
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessSector, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"BusinessSector",
				code)).get(0);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessSectors);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return ModelBinder.toList(ModelBinder::toBusinessUnit, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"BusinessUnit"));
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessUnit, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"BusinessUnit",
				code)).get(0);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessUnits);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return ModelBinder.toList(ModelBinder::toBusinessClassification, performGraphQLQueryWithSubstitution(
				"findAllCodeLookups", 
				"findCodeLookups", 
				"BusinessClassification"));
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessClassification, performGraphQLQueryWithSubstitution(
				"findSpecificCodeLookup", 
				"findCodeLookups", 
				"BusinessClassification",
				code)).get(0);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessClassifications);
	}	
	
	/**
	 * helper methods used to look through a list of the given locale/name pair
	 * @param <T>
	 * @param locale
	 * @param name
	 * @param supplier
	 * @return
	 */
	private <T extends CrmLookupItem> T findByLocalizedName(Locale locale, String name, Supplier<List<T>> supplier) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		Optional<T> optional = supplier
				.get()
				.stream()
				.filter((c) -> StringUtils.equals(c.getName(locale), name))
				.findFirst();				
		if (optional.isEmpty()) {
			throw new ItemNotFoundException("");
		}
		return optional.get();
	}
}