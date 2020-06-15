package ca.magex.crm.graphql.client;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
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

	/* ------------------------------------------------------------------------------------------ */
	/*                                                                                            */
	/*                        Organization Service Implementation                                 */
	/*                                                                                            */
	/* ------------------------------------------------------------------------------------------ */

	@Override
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"createOrganization",
				"createOrganization",
				new MapBuilder()
						.withEntry("displayName", displayName)
						.withEntry("groups", groups)
						.build()));
	}

	@Override
	public OrganizationDetails enableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"enableOrganization",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.build()));
	}

	@Override
	public OrganizationDetails disableOrganization(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"disableOrganization",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"updateOrganizationDisplayName",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.withEntry("displayName", name)
						.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"updateOrganizationMainLocation",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.withEntry("locId", locationId.toString())
						.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"updateOrganizationMainContact",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.withEntry("personId", personId.toString())
						.build()));
	}

	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"updateOrganizationGroups",
				"updateOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.withEntry("groups", groups)
						.build()));
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		return ModelBinder.toOrganizationSummary(performGraphQLQueryWithVariables(
				"findOrganizationSummary",
				"findOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.build()));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		return ModelBinder.toOrganizationDetails(performGraphQLQueryWithVariables(
				"findOrganization",
				"findOrganization",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.build()));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return ModelBinder.toLong(performGraphQLQueryWithSubstitution(
				"countOrganizations",
				"countOrganizations",
				FilterBinder.toFilterString(filter)));
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toOrganizationSummary, performGraphQLQueryWithSubstitution(
				"findOrganizationSummaries",
				"findOrganizations",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toOrganizationDetails, performGraphQLQueryWithSubstitution(
				"findOrganizationDetails",
				"findOrganizations",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	/* -------------------------------------------------------------------------------------- */
	/*                                                                                        */
	/*                       Location Service Implementation                                  */
	/*                                                                                        */
	/* -------------------------------------------------------------------------------------- */

	@Override
	public LocationDetails createLocation(Identifier organizationId, String locationReference, String locationName, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQueryWithVariables(
				"createLocation",
				"createLocation",
				new MapBuilder()
						.withEntry("orgId", organizationId.toString())
						.withEntry("name", locationName)
						.withEntry("ref", locationReference)
						.withEntry("street", address.getStreet())
						.withEntry("city", address.getCity())
						.withEntry("province", address.getProvince())
						.withEntry("country", address.getCountry())
						.withEntry("postal", address.getPostalCode())
						.build()));
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQueryWithVariables(
				"enableLocation",
				"updateLocation",
				new MapBuilder()
						.withEntry("locId", locationId.toString())
						.build()));
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		return ModelBinder.toLocationSummary(performGraphQLQueryWithVariables(
				"disableLocation",
				"updateLocation",
				new MapBuilder()
						.withEntry("locId", locationId.toString())
						.build()));
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String locationName) {
		return ModelBinder.toLocationDetails(performGraphQLQueryWithVariables(
				"updateLocationName",
				"updateLocation",
				new MapBuilder()
						.withEntry("locId", locationId.toString())
						.withEntry("locationName", locationName)
						.build()));
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		return ModelBinder.toLocationDetails(performGraphQLQueryWithVariables(
				"updateLocationAddress",
				"updateLocation",
				new MapBuilder()
						.withEntry("locId", locationId.toString())
						.withEntry("street", address == null ? "" : address.getStreet())
						.withEntry("city", address == null ? "" : address.getCity())
						.withEntry("province", address == null ? "" : address.getProvince())
						.withEntry("country", address == null ? "" : address.getCountry())
						.withEntry("postal", address == null ? "" : address.getPostalCode())
						.build()));
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
				FilterBinder.toFilterString(filter)));
	}

	@Override
	public FilteredPage<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toLocationDetails, performGraphQLQueryWithSubstitution(
				"findLocationDetails",
				"findLocations",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public FilteredPage<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toLocationSummary, performGraphQLQueryWithSubstitution(
				"findLocationSummaries",
				"findLocations",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	/* ------------------------------------------------------------------------------------ */
	/*                                                                                      */
	/*                           Person Service Implementation                              */
	/*                                                                                      */
	/* ------------------------------------------------------------------------------------ */

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"createPerson",
				"createPerson",
				organizationId,
				name.getFirstName(),
				name.getMiddleName(),
				name.getLastName(),
				name.getSalutation(),
				address.getStreet(),
				address.getCity(),
				address.getProvince(),
				address.getCountry(),
				address.getPostalCode(),
				communication.getJobTitle(),
				communication.getLanguage(),
				communication.getEmail(),
				communication.getHomePhone().getNumber(),
				communication.getHomePhone().getExtension(),
				communication.getFaxNumber(),
				position.getSector(),
				position.getUnit(),
				position.getClassification()));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQueryWithVariables(
				"enablePerson",
				"updatePerson",
				new MapBuilder()
						.withEntry("personId", personId.toString())
						.build()));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return ModelBinder.toPersonSummary(performGraphQLQueryWithVariables(
				"disablePerson",
				"updatePerson",
				new MapBuilder()
						.withEntry("personId", personId.toString())
						.build()));
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
				name.getSalutation()));
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
				address.getCountry(),
				address.getPostalCode()));
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return ModelBinder.toPersonDetails(performGraphQLQueryWithSubstitution(
				"updatePersonCommunication",
				"updatePerson",
				personId,
				communication.getJobTitle(),
				communication.getLanguage(),
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
				position.getSector(),
				position.getUnit(),
				position.getClassification()));
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
				FilterBinder.toFilterString(filter)));
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toPersonSummary, performGraphQLQueryWithSubstitution(
				"findPersonSummaries",
				"findPersons",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toPersonDetails, performGraphQLQueryWithSubstitution(
				"findPersonDetails",
				"findPersons",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	/* ---------------------------------------------------------------------------------- */
	/*                                                                                    */
	/*                             User Service Implementation                            */
	/*                                                                                    */
	/* ---------------------------------------------------------------------------------- */

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		return ModelBinder.toUser(performGraphQLQueryWithVariables(
				"createUser",
				"createUser",
				new MapBuilder()
						.withEntry("personId", personId.toString())
						.withEntry("username", username)
						.withEntry("roles", roles)
						.build()));
	}

	@Override
	public User enableUser(Identifier userId) {
		return ModelBinder.toUser(performGraphQLQueryWithVariables(
				"enableUser",
				"updateUser",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.build()));
	}

	@Override
	public User disableUser(Identifier userId) {
		return ModelBinder.toUser(performGraphQLQueryWithVariables(
				"disableUser",
				"updateUser",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.build()));
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		return ModelBinder.toUser(performGraphQLQueryWithVariables(
				"updateUserRoles",
				"updateUser",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.withEntry("roles", roles)
						.build()));
	}

	@Override
	public String resetPassword(Identifier userId) {
		return performGraphQLQueryWithVariables(
				"resetUserPassword",
				"resetUserPassword",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.build());
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		return performGraphQLQueryWithVariables(
				"changeUserPassword",
				"changeUserPassword",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.withEntry("currentPassword", currentPassword)
						.withEntry("newPassword", newPassword)
						.build());
	}

	@Override
	public User findUser(Identifier userId) {
		return ModelBinder.toUser(performGraphQLQueryWithVariables(
				"findUser",
				"findUser",
				new MapBuilder()
						.withEntry("userId", userId.toString())
						.build()));
	}

	@Override
	public User findUserByUsername(String username) {
		FilteredPage<User> users = findUsers(new UsersFilter().withUsername(username), Paging.singleInstance());
		if (users.getContent().size() == 0) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
		return users.getContent().get(0);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return ModelBinder.toLong(performGraphQLQueryWithSubstitution(
				"countUsers",
				"countUsers",
				FilterBinder.toFilterString(filter)));
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toUser, performGraphQLQueryWithSubstitution(
				"findUsers",
				"findUsers",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	/* ---------------------------------------------------------------------------------------- */
	/*                                                                                          */
	/*                       Permission Service Implementation                                  */
	/*                                                                                          */
	/* ---------------------------------------------------------------------------------------- */

	@Override
	public Group createGroup(Localized name) {
		return ModelBinder.toGroup(performGraphQLQueryWithVariables(
				"createGroup",
				"createGroup",
				new MapBuilder()
						.withEntry("code", name.getCode())
						.withEntry("englishName", name.getEnglishName())
						.withEntry("frenchName", name.getFrenchName())
						.build()));
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		return ModelBinder.toGroup(performGraphQLQueryWithVariables(
				"updateGroupName",
				"updateGroup",
				new MapBuilder()
						.withEntry("groupId", groupId.toString())
						.withEntry("englishName", name.getEnglishName())
						.withEntry("frenchName", name.getFrenchName())
						.build()));
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		return ModelBinder.toGroup(performGraphQLQueryWithVariables(
				"enableGroup",
				"updateGroup",
				new MapBuilder()
						.withEntry("groupId", groupId.toString())
						.build()));
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		return ModelBinder.toGroup(performGraphQLQueryWithVariables(
				"disableGroup",
				"updateGroup",
				new MapBuilder()
						.withEntry("groupId", groupId.toString())
						.build()));
	}

	@Override
	public Group findGroup(Identifier groupId) {
		return ModelBinder.toGroup(performGraphQLQueryWithVariables(
				"findGroup",
				"findGroup",
				new MapBuilder()
						.withEntry("groupId", groupId.toString())
						.build()));
	}

	@Override
	public Group findGroupByCode(String code) {
		try {
			JSONObject json = performGraphQLQueryWithVariables(
					"findGroupByCode",
					"findGroups",
					new MapBuilder()
							.withEntry("code", code)
							.build());
			if (json.getInt("totalElements") != 1) {
				throw new ItemNotFoundException("Unable to find group for code '" + code + "'");
			}
			return ModelBinder.toGroup(json.getJSONArray("content").getJSONObject(0));
		} catch (JSONException j) {
			throw new RuntimeException(j);
		}
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toGroup, performGraphQLQueryWithSubstitution(
				"findGroups",
				"findGroups",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		return ModelBinder.toRole(performGraphQLQueryWithVariables(
				"createRole",
				"createRole",
				new MapBuilder()
						.withEntry("groupId", groupId.toString())
						.withEntry("code", name.getCode())
						.withEntry("englishName", name.getEnglishName())
						.withEntry("frenchName", name.getFrenchName())
						.build()));
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		return ModelBinder.toRole(performGraphQLQueryWithVariables(
				"updateRoleName",
				"updateRole",
				new MapBuilder()
						.withEntry("roleId", roleId.toString())
						.withEntry("englishName", name.getEnglishName())
						.withEntry("frenchName", name.getFrenchName())
						.build()));
	}

	@Override
	public Role enableRole(Identifier roleId) {
		return ModelBinder.toRole(performGraphQLQueryWithVariables(
				"enableRole",
				"updateRole",
				new MapBuilder()
						.withEntry("roleId", roleId.toString())
						.build()));
	}

	@Override
	public Role disableRole(Identifier roleId) {
		return ModelBinder.toRole(performGraphQLQueryWithVariables(
				"disableRole",
				"updateRole",
				new MapBuilder()
						.withEntry("roleId", roleId.toString())
						.build()));
	}

	@Override
	public Role findRole(Identifier roleId) {
		return ModelBinder.toRole(performGraphQLQueryWithVariables(
				"findRole",
				"findRole",
				new MapBuilder()
						.withEntry("roleId", roleId.toString())
						.build()));
	}

	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		try {
			JSONObject json = performGraphQLQueryWithVariables(
					"findRoleByCode",
					"findRoles",
					new MapBuilder()
							.withEntry("code", code)
							.build());
			if (json.getInt("totalElements") != 1) {
				throw new ItemNotFoundException("Unable to find role for code '" + code + "'");
			}
			return ModelBinder.toRole(json.getJSONArray("content").getJSONObject(0));
		} catch (JSONException j) {
			throw new RuntimeException(j);
		}
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		Pair<List<String>, List<String>> sortInfo = ModelBinder.getSortInfo(paging);
		return ModelBinder.toPage(filter, paging, ModelBinder::toRole, performGraphQLQueryWithSubstitution(
				"findRoles",
				"findRoles",
				FilterBinder.toFilterString(filter),
				paging.getPageNumber(),
				paging.getPageSize(),
				sortInfo.getFirst(),
				sortInfo.getSecond()));
	}

	/* ------------------------------------------------------------------------------------ */
	/*                                                                                      */
	/*                           Lookup Service Implementation                              */
	/*                                                                                      */
	/* ------------------------------------------------------------------------------------ */

	@Override
	public List<Status> findStatuses() {
		return ModelBinder.toList(ModelBinder::toStatus, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "status")
						.build()));
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toStatus, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "status")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findStatuses);
	}

	@Override
	public List<Country> findCountries() {
		return ModelBinder.toList(ModelBinder::toCountry, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "country")
						.build()));
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toCountry, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "country")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findCountries);
	}

	@Override
	public List<Province> findProvinces(String country) {
		return ModelBinder.toList(ModelBinder::toProvince, performGraphQLQueryWithVariables(
				"findQualifiedCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "province")
						.withEntry("qualifier", country)
						.build()));
	}

	@Override
	public Province findProvinceByCode(@NotNull String province, @NotNull String country) {
		return ModelBinder.toList(ModelBinder::toProvince, performGraphQLQueryWithVariables(
				"findQualifiedCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "province")
						.withEntry("code", province)
						.withEntry("qualifier", country)
						.build()))
				.get(0);
	}

	@Override
	public Province findProvinceByLocalizedName(
			@NotNull Locale locale,
			@NotNull String province,
			@NotNull String country) {
		return findByLocalizedName(locale, province, () -> findProvinces(findCountryByLocalizedName(locale, country).getCode()));
	}

	@Override
	public List<Salutation> findSalutations() {
		return ModelBinder.toList(ModelBinder::toSalutation, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "salutation")
						.build()));
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toSalutation, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "salutation")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findSalutations);
	}

	@Override
	public List<Language> findLanguages() {
		return ModelBinder.toList(ModelBinder::toLanguage, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "language")
						.build()));
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toLanguage, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "language")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findLanguages);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return ModelBinder.toList(ModelBinder::toBusinessSector, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "sector")
						.build()));
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessSector, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "sector")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessSectors);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return ModelBinder.toList(ModelBinder::toBusinessUnit, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "unit")
						.build()));
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessUnit, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "unit")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessUnits);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return ModelBinder.toList(ModelBinder::toBusinessClassification, performGraphQLQueryWithVariables(
				"findCodeLookups",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "classification")
						.build()));
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return ModelBinder.toList(ModelBinder::toBusinessClassification, performGraphQLQueryWithVariables(
				"findCodeLookup",
				"findCodeLookups",
				new MapBuilder()
						.withEntry("category", "classification")
						.withEntry("code", code)
						.build()))
				.get(0);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return findByLocalizedName(locale, name, this::findBusinessClassifications);
	}

	/**
	 * helper methods used to look through a list of the given locale/name pair
	 * 
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