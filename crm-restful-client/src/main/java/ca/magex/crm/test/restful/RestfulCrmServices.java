package ca.magex.crm.test.restful;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.mashape.unirest.http.Unirest;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.User;
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
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataElement;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;

public class RestfulCrmServices implements CrmServices {

	private String server;

	private Locale locale;
	
	private String contentType;
	
	public RestfulCrmServices(String server, Locale locale) {
		this.server = server;
		this.locale = locale;
		this.contentType = "applciation/json";
	}

	public String getConfig() throws Exception {
		return Unirest.get(server + "/api.json")
			.header("Content-Type", contentType)
			.header("Locale", locale.toString())
			.asString()
			.getBody();
	}

	public DataArray list(String endpoint) {
		try {
			String body = Unirest.get(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.asString()
				.getBody();
			return DataParser.parseArray(body);
		} catch (Exception e) {
			throw new RuntimeException("Problem getting to endpoint: " + endpoint, e);
		}
	}

	public DataObject get(String endpoint) {
		return get(endpoint, new DataObject());
	}
	
	public DataObject get(String endpoint, DataObject data) {
		try {
			String body = Unirest.get(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.queryString(data.stream().collect(Collectors.toMap(p -> p.key(), p -> DataElement.unwrap(p.value()))))
				.asString()
				.getBody();
			return DataParser.parseObject(body);
		} catch (Exception e) {
			throw new RuntimeException("Problem getting to endpoint: " + endpoint, e);
		}
	}
	
	public DataObject post(String endpoint, DataObject data) {
		try {
			return DataParser.parseObject(Unirest.post(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.body(data.toString())
				.asString()
				.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Problem posting to endpoint: " + endpoint, e);
		}
	}

	public DataObject put(String endpoint) {
		return put(endpoint, new DataObject());
	}

	public DataObject put(String endpoint, DataObject data) {
		try {
			return DataParser.parseObject(Unirest.post(server + endpoint)
				.header("Content-Type", contentType)
				.header("Locale", locale.toString())
				.body(data.toString())
				.asString()
				.getBody());
		} catch (Exception e) {
			throw new RuntimeException("Problem posting to endpoint: " + endpoint, e);
		}
	}
	
	public DataObject patch(String endpoint, DataObject data) {
		try {
			return DataParser.parseObject(Unirest.patch(server + endpoint)
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
			.map(o -> Status.valueOf(((DataObject)o).getString("code")))
			.collect(Collectors.toList());
	}

	@Override
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/statuses", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> Status.valueOf(((DataObject)o).getString("code")))
			.findFirst().get();
	}

	@Override
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/statuses", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> Status.valueOf(((DataObject)o).getString("code")))
			.findFirst().get();
	}

	@Override
	public List<Role> findRoles() {
		return get("/api/lookup/roles").getArray("options").stream()
			.map(o -> new Role(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/roles", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new Role(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/roles", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new Role(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<Country> findCountries() {
		return get("/api/lookup/countries").getArray("content").stream()
			.map(o -> new Country(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		DataObject obj = get("/api/lookup/countries", new DataObject().with("code", code));
		return new Country(obj.getString("@value"),obj.getString("@en"), obj.getString("@fr"));
	}

	@Override
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		DataObject obj = get("/api/lookup/countries", new DataObject().with("name", name));
		return new Country(obj.getString("@value"),obj.getString("@en"), obj.getString("@fr"));
	}

	@Override
	public List<Language> findLanguages() {
		return get("/api/lookup/languages").getArray("options").stream()
			.map(o -> new Language(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/languages", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new Language(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/languages", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new Language(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<Salutation> findSalutations() {
		return get("/api/lookup/salutations").getArray("options").stream()
			.map(o -> new Salutation(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public Salutation findSalutationByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/salutations", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new Salutation(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/salutations", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new Salutation(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return get("/api/lookup/businessSector").getArray("options").stream()
			.map(o -> new BusinessSector(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessSector findBusinessSectorByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessSector", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessSector(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/businessSector", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessSector(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return get("/api/lookup/businessUnit").getArray("options").stream()
			.map(o -> new BusinessUnit(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessUnit", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessUnit(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return get("/api/lookup/businessUnit", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessUnit(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return get("/api/lookup/businessClassification").getArray("options").stream()
			.map(o -> new BusinessClassification(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.collect(Collectors.toList());
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(String code) throws ItemNotFoundException {
		return get("/api/lookup/businessClassification", new DataObject().
				with("code", code))
			.getArray("options").stream()
			.map(o -> new BusinessClassification(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return get("/api/lookup/businessClassification", new DataObject().
				with("name", name))
			.getArray("options").stream()
			.map(o -> new BusinessClassification(
				((DataObject)o).getString("@value"), 
				((DataObject)o).getString("@en"), 
				((DataObject)o).getString("@fr")))
			.findFirst().get();
	}

	@Override
	public OrganizationDetails createOrganization(String displayName) {
		DataObject result = post("/api/organizations", new DataObject()
			.with("displayName", displayName));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
	}

	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		DataObject result = get("/api/organizations/" + organizationId + "/summary");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		DataObject result = get("/api/organizations/" + organizationId);
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocationId") ? new Identifier(result.getString("mainLocationId")) : null);
	}

	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String displayName) {
		DataObject result = patch("/api/organizations/" + organizationId, new DataObject()
			.with("displayName", displayName));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		DataObject result = patch("/api/organizations/" + organizationId, new DataObject()
			.with("mainLocationId", locationId.toString()));
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
	}

	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		DataObject result = put("/api/organizations/" + organizationId + "/enable");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}

	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		DataObject result = put("/api/organizations/" + organizationId + "/disable");
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		return get("/api/organizations/count", new DataObject()
				.with("displayName", filter.getDisplayName())
				.with("status", filter.getStatus().toString().toLowerCase()))
			.getLong("total");
	}
	
	@Override
	public Page<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		DataObject result = get("/api/organizations", new DataObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus().toString().toLowerCase())
			.with("page", paging.getPageNumber())
			.with("limit", paging.getPageSize()));
		List<OrganizationSummary> items = result.getArray("content").stream()
					.map(item -> (DataObject)item)
					.map(item -> new OrganizationSummary(
				new Identifier(item.getString("organizationId")),
				Status.valueOf(item.getString("status").toUpperCase()),
				item.getString("displayName")))
			.collect(Collectors.toList());
		long total = result.getLong("total");
		return new PageImpl<OrganizationSummary>(items, paging, total);
	}
	
	@Override
	public Page<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		DataObject result = get("/api/organizations", new DataObject()
			.with("displayName", filter.getDisplayName())
			.with("status", filter.getStatus() != null ? filter.getStatus().toString().toLowerCase() : null)
			.with("page", paging.getPageNumber())
			.with("limit", paging.getPageSize()));
		List<OrganizationDetails> items = result.getArray("content").stream()
					.map(item -> (DataObject)item)
					.map(item -> new OrganizationDetails(
				new Identifier(item.getString("organizationId")),
				Status.valueOf(item.getString("status").toUpperCase()),
				item.getString("displayName"),
				item.contains("mainLocationId") ? new Identifier(item.getString("mainLocationId")) : null))
			.collect(Collectors.toList());
		long total = result.getLong("total");
		return new PageImpl<OrganizationDetails>(items, paging, total);
	}
	
	@Override
	public LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address) {
		DataObject result = post("/api/locations", new DataObject()
			.with("organizationId", organizationId.toString())
			.with("reference", reference)
			.with("displayName", displayName)
			.with("address", new DataObject()
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
				findCountryByLocalizedName(locale, result.getObject("address").getString("country")).getCode(),
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
	public Page<LocationDetails> findLocationDetails(LocationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<LocationSummary> findLocationSummaries(LocationsFilter filter, Paging paging) {
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
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserById(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User findUserByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User addUserRole(Identifier userId, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User setUserRoles(Identifier userId, List<String> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User setUserPassword(Identifier userId, String password, boolean encoded) {
		// TODO Auto-generated method stub
		return null;
	}

}
