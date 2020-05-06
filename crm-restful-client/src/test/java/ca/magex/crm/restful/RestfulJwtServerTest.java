package ca.magex.crm.restful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.mashape.unirest.http.Unirest;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;
import ca.magex.crm.test.restful.RestfulCrmServices;

@Ignore
public class RestfulJwtServerTest {
	
	private String username = "admin";
	
	private String password = "admin";
	
	private String key = null;

	private String server = "http://localhost:9002";
	
	private Locale locale = Lang.ENGLISH;
	
	private CrmServices crm;

	@Before
	public void setup() {
		crm = new RestfulCrmServices(server, locale);
	}
	
	@Test
	public void testAuthenticationKey() throws Exception {
		String token = getToken();
		assertThat(token, CoreMatchers.notNullValue());
	}
	
	@Test
	public void testConfigJson() throws Exception {
		String config = getConfig();
		DataObject data = DataParser.parseObject(config);
		assertEquals("3.0.0", data.getString("openapi"));
		assertTrue(data.getObject("paths").contains("/organizations", DataObject.class));
	}
	
	@Test
	public void testCountryLookup() throws Exception {
		List<Country> countries = crm.findCountries();
		assertEquals(253, countries.size());
		Country ca = crm.findCountryByCode("CA");
		assertEquals("CA", ca.getCode());
		assertEquals("Canada", ca.getName(Lang.ENGLISH));
		Country canada = crm.findCountryByLocalizedName(Lang.ENGLISH, "Canada");
		assertEquals("CA", canada.getCode());
		assertEquals("Canada", canada.getName(Lang.ENGLISH));
	}
	
	@Test
	public void testOrganizationCrud() throws Exception {
		String time = LocalDateTime.now().toString();
		Page<OrganizationDetails> organizations1 = crm.findOrganizationDetails(new OrganizationsFilter(), new Paging(Sort.by("displayName")));
		System.out.println(organizations1.getContent());
		int initialOrgCount = organizations1.getContent().size();
		OrganizationDetails organizationA1 = crm.createOrganization("Org " + time);
		assertEquals("Org " + time, organizationA1.getDisplayName());
		assertEquals(Status.ACTIVE, organizationA1.getStatus());
		assertNull(organizationA1.getMainLocationId());
		Page<OrganizationDetails> organizations2 = crm.findOrganizationDetails(new OrganizationsFilter(), new Paging(Sort.by("displayName")));
		System.out.println(organizations2.getContent());
		assertEquals(initialOrgCount + 1, organizations2.getContent().size());
		
		LocationDetails locationA1 = crm.createLocation(organizationA1.getOrganizationId(), "Location " + time, "LOC_A",
				new MailingAddress("123 Main St", "Ottawa", "Ontario", "Canada", "K1K1K1"));
		
		assertEquals(organizationA1.getOrganizationId(), locationA1.getOrganizationId());
		OrganizationDetails organizationA2 = crm.findOrganizationDetails(organizationA1.getOrganizationId());
		assertNull(organizationA2.getMainLocationId());
		
		crm.updateOrganizationDisplayName(organizationA1.getOrganizationId(), "Updated " + time);
		crm.updateOrganizationMainLocation(organizationA1.getOrganizationId(), locationA1.getLocationId());
		
		OrganizationDetails organizationA3 = crm.findOrganizationDetails(organizationA1.getOrganizationId());
		assertEquals(organizationA3.getMainLocationId(), locationA1.getLocationId());
		
		OrganizationSummary organizationA4 = crm.findOrganizationSummary(organizationA1.getOrganizationId());
		
	}

	public String getConfig() throws Exception {
		return Unirest.get(server + "/api.json")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.asString()
			.getBody();
	}

	private String getToken() throws Exception {
		if (key == null) {
			key = new JSONObject(Unirest.post(server + "/authenticate")
				.header("Content-Type", "application/json")
				.body("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }")
				.asString()
				.getBody()).getString("token");
		}
		return key;
	}
//	
//	private DataArray getAllOrganizations() throws Exception {
//		return DataParser.parseArray(Unirest.get(server + "/api/organizations")
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.asString()
//			.getBody());
//	}
//	
//	private OrganizationDetails createOrganization(String displayName) throws Exception {
//		DataObject body = new DataObject()
//			.with("displayName", displayName);
//		DataObject result = DataParser.parseObject(Unirest.post(server + "/api/organizations")
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.body(body.toString())
//			.asString()
//			.getBody());
//		return new OrganizationDetails(
//			new Identifier(result.getString("organizationId")),
//			Status.valueOf(result.getString("status").toUpperCase()),
//			result.getString("displayName"),
//			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
//	}
//	
//	private OrganizationDetails getOrganizationDetails(Identifier organzationId) throws Exception {
//		DataObject result = DataParser.parseObject(Unirest.get(server + "/api/organizations/" + organzationId)
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.asString()
//			.getBody());
//		return new OrganizationDetails(
//			new Identifier(result.getString("organizationId")),
//			Status.valueOf(result.getString("status").toUpperCase()),
//			result.getString("displayName"),
//			result.contains("mainLocationId") ? new Identifier(result.getString("mainLocationId")) : null);
//	}
//	
//	private OrganizationSummary getOrganizationSummary(Identifier organzationId) throws Exception {
//		DataObject result = DataParser.parseObject(Unirest.get(server + "/api/organizations/" + organzationId + "/summary")
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.asString()
//			.getBody());
//		return new OrganizationSummary(
//			new Identifier(result.getString("organizationId")),
//			Status.valueOf(result.getString("status").toUpperCase()),
//			result.getString("displayName"));
//	}
//	
//	private OrganizationDetails updateOrganization(Identifier organizationId, String displayName, Identifier locationId) throws Exception {
//		DataObject body = new DataObject()
//			.with("displayName", displayName)
//			.with("mainLocationId", locationId.toString());
//		DataObject result = DataParser.parseObject(Unirest.patch(server + "/api/organizations/" + organizationId)
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.body(body.toString())
//			.asString()
//			.getBody());
//		return new OrganizationDetails(
//			new Identifier(result.getString("organizationId")),
//			Status.valueOf(result.getString("status").toUpperCase()),
//			result.getString("displayName"),
//			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
//	}
//
//
//	private LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address) throws Exception {
//		DataObject body = new DataObject()
//			.with("organizationId", organizationId.toString())
//			.with("reference", reference)
//			.with("displayName", displayName)
//			.with("address", new DataObject()
//				.with("street", address.getStreet())
//				.with("city", address.getCity())
//				.with("province", address.getProvince())
//				.with("country", address.getCountry().getName(locale))
//				.with("postalCode", address.getPostalCode()));
//		DataObject result = DataParser.parseObject(Unirest.post(server + "/api/locations")
//			.header("Content-Type", "application/json")
//			.header("Authorization", "Bearer " + getToken())
//			.body(body.toString())
//			.asString()
//			.getBody());
//		return new LocationDetails(new Identifier(result.getString("locationId")), 
//			new Identifier(result.getString("organizationId")),
//			Status.valueOf(result.getString("status").toUpperCase()),
//			result.getString("reference"),
//			result.getString("displayName"),
//			new MailingAddress(
//				result.getObject("address").getString("street"),
//				result.getObject("address").getString("city"),
//				result.getObject("address").getString("province"),
//				lookups.findCountryByLocalizedName(locale, result.getObject("address").getString("country")),
//				result.getObject("address").getString("postalCode")));
//	}
	
	

	
}
