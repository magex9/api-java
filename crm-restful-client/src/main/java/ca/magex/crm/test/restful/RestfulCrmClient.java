package ca.magex.crm.test.restful;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.mashape.unirest.http.Unirest;

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
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmClient;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonParser;

public class RestfulCrmClient implements CrmClient {

	private String server;

	private Locale locale;
	
	private String contentType;
	
	public RestfulCrmClient(String server, Locale locale) {
		this.server = server;
		this.locale = locale;
		this.contentType = "application/json";
	}
	
	public boolean login(String username, String password) {
		return true;
	}
	
	public boolean logout() {
		return true;
	}

	public String getConfig() throws Exception {
		return Unirest.get(server + "/api.json")
			.header("Content-Type", contentType)
			.header("Locale", locale.toString())
			.asString()
			.getBody();
	}

	public JsonArray list(String endpoint) {
		try {
			String body = Unirest.get(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.asString()
				.getBody();
			return JsonParser.parseArray(body);
		} catch (Exception e) {
			throw new RuntimeException("Problem getting to endpoint: " + endpoint, e);
		}
	}

	public JsonObject get(String endpoint) {
		return get(endpoint, new JsonObject());
	}
	
	public JsonObject get(String endpoint, JsonObject data) {
		try {
			String body = Unirest.get(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.queryString(data.stream().collect(Collectors.toMap(p -> p.key(), p -> JsonElement.unwrap(p.value()))))
				.asString()
				.getBody();
			return JsonParser.parseObject(body);
		} catch (Exception e) {
			throw new RuntimeException("Problem getting to endpoint: " + endpoint, e);
		}
	}
	
	public JsonObject post(String endpoint, JsonObject data) {
		try {
			return JsonParser.parseObject(Unirest.post(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.body(data.toString())
				.asString()
				.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Problem posting to endpoint: " + endpoint, e);
		}
	}

	public JsonObject put(String endpoint) {
		return put(endpoint, new JsonObject());
	}

	public JsonObject put(String endpoint, JsonObject data) {
		try {
			return JsonParser.parseObject(Unirest.post(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.body(data.toString())
				.asString()
				.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Problem posting to endpoint: " + endpoint, e);
		}
	}
	
	public JsonObject patch(String endpoint, JsonObject data) {
		try {
			return JsonParser.parseObject(Unirest.patch(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.body(data.toString())
				.asString()
				.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Problem posting to endpoint: " + endpoint, e);
		}
	}

	@Override
	public List<Status> findStatuses() {
		return get("/api/lookup/statuses").getArray("options").stream()
			.map(o -> Status.valueOf(((JsonObject)o).getString("code")))
			.collect(Collectors.toList());
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/statuses", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> Status.valueOf(((JsonObject)o).getString("code")))
			.findFirst().get();
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/statuses", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> Status.valueOf(((JsonObject)o).getString("code")))
			.findFirst().get();
	}

	@Override
	public List<Country> findCountries() {
		return get("/api/lookup/countries").getArray("content").stream()
			.map(o -> new Country(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		JsonObject obj = get("/api/lookup/countries", new JsonObject().with("code", code));
		return new Country(obj.getString("@value"),obj.getString("@en"), obj.getString("@fr"));
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		JsonObject obj = get("/api/lookup/countries", new JsonObject().with("name", name));
		return new Country(obj.getString("@value"),obj.getString("@en"), obj.getString("@fr"));
	}

	@Override
	public List<Language> findLanguages() {
		return get("/api/lookup/languages").getArray("options").stream()
			.map(o -> new Language(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/languages", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new Language(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/languages", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new Language(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<Salutation> findSalutations() {
		return get("/api/lookup/salutations").getArray("options").stream()
			.map(o -> new Salutation(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/salutations", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new Salutation(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/salutations", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new Salutation(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return get("/api/lookup/businessSector").getArray("options").stream()
			.map(o -> new BusinessSector(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessSector", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessSector(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/businessSector", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessSector(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return get("/api/lookup/businessUnit").getArray("options").stream()
			.map(o -> new BusinessUnit(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessUnit", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessUnit(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/businessUnit", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessUnit(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return get("/api/lookup/businessClassification").getArray("options").stream()
			.map(o -> new BusinessClassification(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessClassification", new JsonObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessClassification(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return get("/api/lookup/businessClassification", new JsonObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessClassification(
				((JsonObject)o).getString("@value"), 
				((JsonObject)o).getString("@en"), 
				((JsonObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		JsonObject result = post("/api/organizations", new JsonObject()
			.with("displayName", displayName));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null,
			result.contains("mainContact") ? new Identifier(result.getString("mainContact")) : null,
					groups);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		JsonObject result = get("/api/organizations/" + organizationId + "/summary");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		JsonObject result = get("/api/organizations/" + organizationId);
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocationId") ? new Identifier(result.getString("mainLocationId")) : null,
			result.contains("mainContact") ? new Identifier(result.getString("mainContact")) : null,
			new ArrayList<String>());
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String displayName) {
		JsonObject result = patch("/api/organizations/" + organizationId, new JsonObject()
			.with("displayName", displayName));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null,
			result.contains("mainContact") ? new Identifier(result.getString("mainContact")) : null,
			new ArrayList<String>());
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		JsonObject result = patch("/api/organizations/" + organizationId, new JsonObject()
			.with("mainLocationId", locationId.toString()));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null,
			result.contains("mainContact") ? new Identifier(result.getString("mainContact")) : null,
			new ArrayList<String>());
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		JsonObject result = put("/api/organizations/" + organizationId + "/enable");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		JsonObject result = put("/api/organizations/" + organizationId + "/disable");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return get("/api/organizations/count", new JsonObject()
				.with("displayName", filter.getDisplayName())
				.with("status", filter.getStatus().toString().toLowerCase()))
			.getLong("total");
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		JsonObject result = get("/api/organizations", new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus().toString().toLowerCase())
			.with("page", paging.getPageNumber())
			.with("limit", paging.getPageSize()));
		List<OrganizationSummary> items = result.getArray("content").stream()
					.map(item -> (JsonObject)item)
					.map(item -> new OrganizationSummary(
				new Identifier(item.getString("organizationId")),
				Status.valueOf(item.getString("status").toUpperCase()),
				item.getString("displayName")))
			.collect(Collectors.toList());
		long total = result.getLong("total");
		return new FilteredPage<OrganizationSummary>(filter, paging, items, total);
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		JsonObject result = get("/api/organizations", new JsonObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() != null ? filter.getStatus().toString().toLowerCase() : null)
			.with("page", paging.getPageNumber())
			.with("limit", paging.getPageSize()));
		List<OrganizationDetails> items = result.getArray("content").stream()
					.map(item -> (JsonObject)item)
					.map(item -> new OrganizationDetails(
				new Identifier(item.getString("organizationId")),
				Status.valueOf(item.getString("status").toUpperCase()),
				item.getString("displayName"),
				item.contains("mainLocationId") ? new Identifier(item.getString("mainLocationId")) : null,
				item.contains("mainContactId") ? new Identifier(item.getString("mainContactId")) : null,
				new ArrayList<String>()))
			.collect(Collectors.toList());
		long total = result.getLong("total");
		return new FilteredPage<OrganizationDetails>(filter, paging, items, total);
	}
	
	@Override
	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address) {
		JsonObject result = post("/api/locations", new JsonObject()
			.with("organizationId", organizationId.toString())
			.with("reference", reference)
			.with("displayName", displayName)
			.with("address", new JsonObject()
				.with("street", address.getStreet())
				.with("city", address.getCity())
				.with("province", address.getProvince())
				.with("country", address.getCountry())
				.with("postalCode", address.getPostalCode())));
		return new LocationDetails(new Identifier(result.getString("locationId")), 
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("reference"),
			result.getString("displayName"),
			new MailingAddress(
				result.getObject("address").getString("street"),
				result.getObject("address").getString("city"),
				result.getObject("address").getString("province"),
				result.getObject("address").getString("country"),
				result.getObject("address").getString("postalCode")));
	}

	@Override
	public LocationSummary enableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary disableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationName(Identifier locationId, String displaysName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails updateLocationAddress(Identifier locationId, MailingAddress address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationSummary findLocationSummary(Identifier locationId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocationDetails findLocationDetails(Identifier locationId) {
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

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address,
			Communication communication, BusinessPosition position) {
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
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User enableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User disableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUser(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roleIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String resetPassword(Identifier userId) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public long countUsers(UsersFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group findGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group createGroup(Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role findRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role findRoleByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role enableRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Role disableRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationMainContact(@NotNull Identifier organizationId,
			@NotNull Identifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrganizationDetails updateOrganizationGroups(@NotNull Identifier organizationId,
			@NotNull List<String> groups) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserByUsername(@NotNull String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group findGroupByCode(@NotNull String code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean reset() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void dump(OutputStream os) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canCreateOrganization() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCreateGroup() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewGroup(String group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnableGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisableGroup(Identifier groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewRoles() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewRole(String code) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canViewRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canUpdateRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnableRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDisableRole(Identifier roleId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public List<Province> findProvinces(String country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Province findProvinceByCode(@NotNull String province, @NotNull String country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Province findProvinceByLocalizedName(@NotNull Locale locale, @NotNull String province,
			@NotNull String country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canCreateRole(Identifier groupId) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
