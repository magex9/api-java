package ca.magex.crm.test.restful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mashape.unirest.http.Unirest;

import ca.magex.crm.amnesia.services.AmnesiaLookupService;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.mapping.data.DataArray;
import ca.magex.crm.mapping.data.DataObject;
import ca.magex.crm.mapping.data.DataParser;

public class RestfulJwtServerTest {
	
	private String username = "admin";
	
	private String password = "admin";
	
	private String server = "http://localhost:8080";
	
	private String key = null;
	
	private Locale locale = Lang.ENGLISH;
	
	private CrmLookupService lookups;

	@Before
	public void setup() {
		lookups = new AmnesiaLookupService();
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
	public void testOrganizationCrud() throws Exception {
		String time = LocalDateTime.now().toString();
		DataArray organizations1 = getAllOrganizations();
		System.out.println(organizations1);
		int initialOrgCount = organizations1.size();
		OrganizationDetails organizationA1 = createOrganization("Org " + time);
		assertEquals("Org " + time, organizationA1.getDisplayName());
		assertEquals(Status.ACTIVE, organizationA1.getStatus());
		assertNull(organizationA1.getMainLocationId());
		DataArray organizations2 = getAllOrganizations();
		System.out.println(organizations2);
		assertEquals(initialOrgCount + 1, organizations2.size());
		
		LocationDetails locationA1 = createLocation(organizationA1.getOrganizationId(), "Location " + time, "LOC_A",
				new MailingAddress("123 Main St", "Ottawa", "Ontario", lookups.findCountryByCode("CA"), "K1K1K1"));
		
		assertEquals(organizationA1.getOrganizationId(), locationA1.getOrganizationId());
		OrganizationDetails organizationA2 = getOrganizationDetails(organizationA1.getOrganizationId());
		assertNull(organizationA2.getMainLocationId());
		
		updateOrganization(organizationA1.getOrganizationId(), "Updated " + time, locationA1.getLocationId());
		
		OrganizationDetails organizationA3 = getOrganizationDetails(organizationA1.getOrganizationId());
		assertEquals(organizationA3.getMainLocationId(), locationA1.getLocationId());
		
		OrganizationSummary organizationA4 = getOrganizationSummary(organizationA1.getOrganizationId());
		
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
	
	private DataArray getAllOrganizations() throws Exception {
		return DataParser.parseArray(Unirest.get(server + "/api/organizations")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.asString()
			.getBody());
	}
	
	private OrganizationDetails createOrganization(String displayName) throws Exception {
		DataObject body = new DataObject()
			.with("displayName", displayName);
		DataObject result = DataParser.parseObject(Unirest.post(server + "/api/organizations")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.body(body.toString())
			.asString()
			.getBody());
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
	}
	
	private OrganizationDetails getOrganizationDetails(Identifier organzationId) throws Exception {
		DataObject result = DataParser.parseObject(Unirest.get(server + "/api/organizations/" + organzationId)
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.asString()
			.getBody());
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocationId") ? new Identifier(result.getString("mainLocationId")) : null);
	}
	
	private OrganizationSummary getOrganizationSummary(Identifier organzationId) throws Exception {
		DataObject result = DataParser.parseObject(Unirest.get(server + "/api/organizations/" + organzationId + "/summary")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.asString()
			.getBody());
		return new OrganizationSummary(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"));
	}
	
	private OrganizationDetails updateOrganization(Identifier organizationId, String displayName, Identifier locationId) throws Exception {
		DataObject body = new DataObject()
			.with("displayName", displayName)
			.with("mainLocationId", locationId.toString());
		DataObject result = DataParser.parseObject(Unirest.patch(server + "/api/organizations/" + organizationId)
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.body(body.toString())
			.asString()
			.getBody());
		return new OrganizationDetails(
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("displayName"),
			result.contains("mainLocation") ? new Identifier(result.getString("mainLocation")) : null);
	}


	private LocationDetails createLocation(Identifier organizationId, String displayName, String reference, MailingAddress address) throws Exception {
		DataObject body = new DataObject()
			.with("organizationId", organizationId.toString())
			.with("reference", reference)
			.with("displayName", displayName)
			.with("address", new DataObject()
				.with("street", address.getStreet())
				.with("city", address.getCity())
				.with("province", address.getProvince())
				.with("country", address.getCountry().getName(locale))
				.with("postalCode", address.getPostalCode()));
		DataObject result = DataParser.parseObject(Unirest.post(server + "/api/locations")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + getToken())
			.body(body.toString())
			.asString()
			.getBody());
		return new LocationDetails(new Identifier(result.getString("locationId")), 
			new Identifier(result.getString("organizationId")),
			Status.valueOf(result.getString("status").toUpperCase()),
			result.getString("reference"),
			result.getString("displayName"),
			new MailingAddress(
				result.getObject("address").getString("street"),
				result.getObject("address").getString("city"),
				result.getObject("address").getString("province"),
				lookups.findCountryByLocalizedName(locale, result.getObject("address").getString("country")),
				result.getObject("address").getString("postalCode")));
	}
	
	

	
}
