package ca.magex.crm.restful.controllers;

import static ca.magex.crm.test.CrmAsserts.CEO;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG_NAME;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.assertSingleJsonMessage;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.transform.json.IdentifierJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.LoremIpsumGenerator;

public class OrganizationsControllerTests extends AbstractControllerTests {
	
	@Before
	public void setup() {
		initialize();
	}
	
	@Test
	public void testCreateOrganization() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject orig = get("/organizations");
		assertEquals(1, orig.getInt("page"));
		assertEquals(1, orig.getInt("total"));
		assertEquals(false, orig.getBoolean("hasNext"));
		assertEquals(false, orig.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, orig.get("content").getClass());
		assertEquals(1, orig.getArray("content").size());
		assertEquals("System", orig.getArray("content").getObject(0).getString("displayName"));
		
		JsonObject create = post("/organizations", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("displayName", ORG_NAME.getEnglishName())
			.with("authenticationGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new AuthenticationGroupIdentifier("ORG"), Lang.ENGLISH)))
			.with("businessGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new BusinessGroupIdentifier("IMIT"), Lang.ENGLISH))));
		assertEquals(List.of("organizationId", "status", "displayName", "authenticationGroupIds", "businessGroupIds"), create.keys());
		assertTrue(create.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", create.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), create.getString("displayName"));
		assertEquals(new JsonArray().with("Organization"), create.getArray("authenticationGroupIds"));
		assertEquals(new JsonArray().with("IM/IT"), create.getArray("businessGroupIds"));
		OrganizationIdentifier organizationId = new OrganizationIdentifier(create.getString("organizationId"));
		
		JsonObject fetch = get(organizationId, Lang.ROOT, HttpStatus.OK);
		assertEquals(List.of("organizationId", "status", "displayName", "authenticationGroupIds", "businessGroupIds"), fetch.keys());
		assertEquals(organizationId.getCode(), fetch.getString("organizationId"));
		assertEquals("ACTIVE", fetch.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), fetch.getString("displayName"));
		assertEquals(new JsonArray().with("ORG"), fetch.getArray("authenticationGroupIds"));
		assertEquals(new JsonArray().with("IMIT"), fetch.getArray("businessGroupIds"));

		JsonObject french = get(organizationId, Lang.FRENCH, HttpStatus.OK);
		assertEquals(List.of("organizationId", "status", "displayName", "authenticationGroupIds", "businessGroupIds"), french.keys());
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), french.getString("displayName"));
		assertEquals(new JsonArray().with("Organisation"), french.getArray("authenticationGroupIds"));
		assertEquals(new JsonArray().with("GI/TI"), french.getArray("businessGroupIds"));

		JsonObject jsonld = get(organizationId, null, HttpStatus.OK);
		assertEquals(List.of("@context", "organizationId", "status", "displayName", "authenticationGroupIds", "businessGroupIds"), jsonld.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationDetails", jsonld.getString("@context"));
		assertEquals(Crm.REST_BASE + organizationId, jsonld.getString("organizationId"));
		assertEquals("ACTIVE", jsonld.getObject("status").getString("@value"));
		assertEquals(ORG_NAME.getEnglishName(), jsonld.getString("displayName"));
		assertEquals(1, jsonld.getArray("authenticationGroupIds").size());
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-groups/org", jsonld.getArray("authenticationGroupIds").getObject(0).getString("@id"));
		assertEquals(1, jsonld.getArray("businessGroupIds").size());
		assertEquals("http://api.magex.ca/crm/rest/options/business-groups/imit", jsonld.getArray("businessGroupIds").getObject(0).getString("@id"));

		JsonObject paging = get("/organizations", Lang.ENGLISH, HttpStatus.OK);
		assertEquals(1, paging.getInt("page"));
		assertEquals(2, paging.getInt("total"));
		assertEquals(false, paging.getBoolean("hasNext"));
		assertEquals(false, paging.getBoolean("hasPrevious"));
		assertEquals(JsonArray.class, paging.get("content").getClass());
		assertEquals(2, paging.getArray("content").size());
		paging.getArray("content").values().forEach(el -> {
			assertEquals(JsonObject.class, el.getClass());
			assertEquals(List.of("organizationId", "status", "displayName"), ((JsonObject)el).keys());
		});
		
		assertEquals(organizationId.getCode(), paging.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", paging.getArray("content").getObject(0).getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), paging.getArray("content").getObject(0).getString("displayName"));

		assertEquals("Active", paging.getArray("content").getObject(1).getString("status"));
		assertEquals(SYSTEM_ORG, paging.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetOrganizationSummary() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();
		crm.updateOrganizationMainLocation(organizationId, locationId);
		crm.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject linked = get(organizationId + "/summary", null, HttpStatus.OK);
		assertEquals(List.of("@context", "organizationId", "status", "displayName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/OrganizationSummary", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest" + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Organization", linked.getString("displayName"));
		
		JsonObject root = get(organizationId + "/summary", Lang.ROOT, HttpStatus.OK);
		assertEquals(List.of("organizationId", "status", "displayName"), root.keys());
		assertTrue(root.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), root.getString("displayName"));
		
		JsonObject english = get(organizationId + "/summary", Lang.ENGLISH, HttpStatus.OK);
		assertEquals(List.of("organizationId", "status", "displayName"), english.keys());
		assertTrue(english.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", english.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), english.getString("displayName"));
		
		JsonObject french = get(organizationId + "/summary", Lang.FRENCH, HttpStatus.OK);;
		assertEquals(List.of("organizationId", "status", "displayName"), french.keys());
		assertTrue(french.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), french.getString("displayName"));
	}
	
	@Test
	public void testGetMainLocation() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS.withProvince(new Choice<ProvinceIdentifier>(new ProvinceIdentifier("CA/NL")))).getLocationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();
		crm.updateOrganizationMainLocation(organizationId, locationId);
		crm.updateOrganizationMainContact(organizationId, personId);

		JsonObject linked = get(organizationId + "/mainLocation", null, HttpStatus.OK);
		assertEquals(List.of("@context", "locationId", "organizationId", "status", "reference", "displayName", "address"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/LocationDetails", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest" + locationId, linked.getString("locationId"));
		assertEquals("http://api.magex.ca/crm/rest" + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("MAIN", linked.getString("reference"));
		assertEquals("Main Location", linked.getString("displayName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/nl", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/NL", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Newfoundland and Labrador", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Terre-Neuve et Labrador", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		
		JsonObject data = get(organizationId + "/mainLocation", Lang.ROOT, HttpStatus.OK);
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), data.keys());
		assertEquals(locationId.getCode(), data.getString("locationId"));
		assertEquals(organizationId.getCode(), data.getString("organizationId"));
		assertEquals("ACTIVE", data.getString("status"));
		assertEquals("Main Location", data.getString("displayName"));
		assertEquals("MAIN", data.getString("reference"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), data.getObject("address").keys());
		assertEquals(MAILING_ADDRESS.getStreet(), data.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), data.getObject("address").getString("city"));
		assertEquals("CA/NL", data.getObject("address").getString("province"));
		assertEquals("CA", data.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), data.getObject("address").getString("postalCode"));

		JsonObject english = get(organizationId + "/mainLocation", Lang.ENGLISH, HttpStatus.OK);
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), english.keys());
		assertEquals(locationId.getCode(), english.getString("locationId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Main Location", english.getString("displayName"));
		assertEquals("MAIN", english.getString("reference"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals(MAILING_ADDRESS.getStreet(), english.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), english.getObject("address").getString("city"));
		assertEquals("Newfoundland and Labrador", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), english.getObject("address").getString("postalCode"));
		
		JsonObject french = get(organizationId + "/mainLocation", Lang.FRENCH, HttpStatus.OK);
		assertEquals(List.of("locationId", "organizationId", "status", "reference", "displayName", "address"), french.keys());
		assertEquals(locationId.getCode(), french.getString("locationId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Main Location", french.getString("displayName"));
		assertEquals("MAIN", french.getString("reference"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals(MAILING_ADDRESS.getStreet(), french.getObject("address").getString("street"));
		assertEquals(MAILING_ADDRESS.getCity(), french.getObject("address").getString("city"));
		assertEquals("Terre-Neuve et Labrador", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals(MAILING_ADDRESS.getPostalCode(), french.getObject("address").getString("postalCode"));
	}
	
	@Test
	public void testGetMainContact() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();
		crm.updateOrganizationMainContact(organizationId, personId);
		
		JsonObject linked = get(organizationId + "/mainContact", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest" + personId, linked.getString("personId"));
		assertEquals("http://api.magex.ca/crm/rest" + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getObject("legalName").getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("legalName").getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", linked.getObject("legalName").getObject("salutation").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", linked.getObject("legalName").getObject("salutation").getString("@id"));
		assertEquals("MR", linked.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Chris", linked.getObject("legalName").getString("firstName"));
		assertEquals("P", linked.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/qc", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.getObject("communication").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", linked.getObject("communication").getString("@context"));
		assertEquals("Developer", linked.getObject("communication").getString("jobTitle"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("communication").getObject("language").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Languages", linked.getObject("communication").getObject("language").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/languages/en", linked.getObject("communication").getObject("language").getString("@id"));
		assertEquals("EN", linked.getObject("communication").getObject("language").getString("@value"));
		assertEquals("English", linked.getObject("communication").getObject("language").getString("@en"));
		assertEquals("Anglais", linked.getObject("communication").getObject("language").getString("@fr"));
		assertEquals("user@work.ca", linked.getObject("communication").getString("email"));
		assertEquals(List.of("@context", "number", "extension"), linked.getObject("communication").getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", linked.getObject("communication").getObject("homePhone").getString("@context"));
		assertEquals("5551234567", linked.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", linked.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", linked.getObject("communication").getString("faxNumber"));
		assertEquals(1, linked.getArray("businessRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", linked.getArray("businessRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/execs/ceo", linked.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("EXECS/CEO", linked.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Chief Executive Officer", linked.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Directeur général", linked.getArray("businessRoleIds").getObject(0).getString("@fr"));

		JsonObject root = get(organizationId + "/mainContact", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("MR", root.getObject("legalName").getString("salutation"));
		assertEquals("Chris", root.getObject("legalName").getString("firstName"));
		assertEquals("P", root.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", root.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("123 Main St", root.getObject("address").getString("street"));
		assertEquals("Ottawa", root.getObject("address").getString("city"));
		assertEquals("CA/QC", root.getObject("address").getString("province"));
		assertEquals("CA", root.getObject("address").getString("country"));
		assertEquals("K1K1K1", root.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), root.getObject("communication").keys());
		assertEquals("Developer", root.getObject("communication").getString("jobTitle"));
		assertEquals("EN", root.getObject("communication").getString("language"));
		assertEquals("user@work.ca", root.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), root.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", root.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getObject("communication").getString("faxNumber"));
		assertEquals(1, root.getArray("businessRoleIds").size());
		assertEquals("EXECS/CEO", root.getArray("businessRoleIds").getString(0));

		JsonObject english = get(organizationId + "/mainContact", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Chris", english.getObject("legalName").getString("firstName"));
		assertEquals("P", english.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(1, english.getArray("businessRoleIds").size());
		assertEquals("Chief Executive Officer", english.getArray("businessRoleIds").getString(0));
		
		JsonObject french = get(organizationId + "/mainContact", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Chris", french.getObject("legalName").getString("firstName"));
		assertEquals("P", french.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(1, french.getArray("businessRoleIds").size());
		assertEquals("Directeur général", french.getArray("businessRoleIds").getString(0));
	}
	
	@Test
	public void testUpdatingFullOrganization() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();

		OrganizationDetails org = crm.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals(ORG_NAME.getEnglishName(), org.getDisplayName());
		assertNull(org.getMainLocationId());
		assertNull(org.getMainContactId());
		
		JsonObject json = patch(organizationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("displayName", "Updated name")
			.with("mainLocationId", locationId.toString())
			.with("mainContactId", personId.toString()));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(locationId.getCode(), json.getString("mainLocationId"));
		assertEquals(personId.getCode(), json.getString("mainContactId"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
		
		org = crm.findOrganizationDetails(organizationId);
		assertEquals(organizationId, org.getOrganizationId());
		assertEquals("Updated name", org.getDisplayName());
		assertEquals(locationId, org.getMainLocationId());
		assertEquals(personId, org.getMainContactId());
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		
		JsonObject json = patch(organizationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
				.with("displayName", "Updated name"));
				
		assertEquals(List.of("organizationId", "status", "displayName", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Updated name", json.getString("displayName"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
	}
	
	@Test
	public void testUpdatingMainLocation() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();

		JsonObject json = patch(organizationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("mainLocationId", locationId.toString()));

		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(locationId.getCode(), json.getString("mainLocationId"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
	}
	
	@Test
	public void testUpdatingMainLocationAsNull() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "MAIN", "Main Location", MAILING_ADDRESS).getLocationId();
		crm.updateOrganizationMainLocation(organizationId, locationId);

		JsonObject json = patch(organizationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("mainLocationId", null));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("organizationId", "status", "displayName", "mainLocationId", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(locationId.getCode(), json.getString("mainLocationId"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
	}
	
	@Test
	public void testUpdatingMainContact() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();
		
		JsonObject json = patch(organizationId, Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("mainContactId", personId.toString()));
		
		assertEquals(List.of("organizationId", "status", "displayName", "mainContactId", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertTrue(json.getString("organizationId").matches("[A-Za-z0-9]+"));
		assertEquals("Active", json.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), json.getString("displayName"));
		assertEquals(personId.getCode(), json.getString("mainContactId"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
	}
	
	@Test
	public void testUpdatingMainContactAsNull() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		PersonIdentifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(CEO)).getPersonId();
		crm.updateOrganizationMainContact(organizationId, personId);

		JsonObject json = patch(organizationId, new JsonObject()
			.with("mainContactId", null));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("organizationId", "status", "displayName", "mainContactId", "authenticationGroupIds", "businessGroupIds"), json.keys());
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Organization", json.getString("displayName"));
		assertEquals(personId.getCode(), json.getString("mainContactId"));
		assertEquals(1, json.getArray("authenticationGroupIds").size());
		assertEquals("Organization", json.getArray("authenticationGroupIds").getString(0));
		assertEquals(1, json.getArray("businessGroupIds").size());
		assertEquals("IM/IT", json.getArray("businessGroupIds").getString(0));
	}
	
	@Test
	public void testEnableDisableOrganization() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization(ORG_NAME.getEnglishName(), List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("IMIT"))).getOrganizationId();
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error1 = put(organizationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(organizationId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("Field is required", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error2 = put(organizationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(organizationId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("Field is required", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonArray error3 = put(organizationId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(organizationId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Format is invalid", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());

		JsonObject disable = put(organizationId + "/disable", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(organizationId.getCode(), disable.getString("organizationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error4 = put(organizationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(organizationId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("Field is required", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error5 = put(organizationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(organizationId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("Field is required", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
		
		JsonArray error6 = put(organizationId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(organizationId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals(new MessageTypeIdentifier("ERROR").toString(), error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Format is invalid", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
	
		JsonObject enable = put(organizationId + "/enable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(organizationId.getCode(), enable.getString("organizationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals(ORG_NAME.getEnglishName(), disable.getString("displayName"));
		assertEquals(Status.ACTIVE, crm.findOrganizationSummary(organizationId).getStatus());
	}
	
	@Test
	public void testOrganizationWithLongName() throws Exception {
		JsonArray json = post("/organizations", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject()
			.with("displayName", LoremIpsumGenerator.buildWords(20))
			.with("authenticationGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new AuthenticationGroupIdentifier("ORG"), Lang.ENGLISH)))
			.with("businessGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new BusinessGroupIdentifier("IMIT"), Lang.ENGLISH))));
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "displayName", "Field too long");
	}

	@Test
	public void testOrganizationWithNoName() throws Exception {
		JsonArray json = post("/organizations", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject()
			.with("authenticationGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new AuthenticationGroupIdentifier("ORG"), Lang.ENGLISH)))
			.with("businessGroupIds", List.of(new IdentifierJsonTransformer(crm).format(new BusinessGroupIdentifier("IMIT"), Lang.ENGLISH))));
		assertSingleJsonMessage(json, null, MessageTypeIdentifier.ERROR, "displayName", "Field is required");
	}
	
}
