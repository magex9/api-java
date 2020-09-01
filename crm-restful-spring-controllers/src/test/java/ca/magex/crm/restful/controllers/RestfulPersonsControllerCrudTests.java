package ca.magex.crm.restful.controllers;

import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_OWNER;
import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.HOME_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.transform.json.CommunicationJsonTransformer;
import ca.magex.crm.transform.json.IdentifierJsonTransformer;
import ca.magex.crm.transform.json.MailingAddressJsonTransformer;
import ca.magex.crm.transform.json.OptionJsonTransformer;
import ca.magex.crm.transform.json.PersonNameJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class RestfulPersonsControllerCrudTests extends AbstractControllerTests {

	private OrganizationIdentifier organizationId;
	
	@Before
	public void setup() {
		initialize();
		organizationId = createTestOrganization("Test Org");
	}
	
	@Test
	public void testCreatePerson() throws Exception {
		// Get the initial list of groups to make sure they are blank
		JsonObject orig = get("/persons");
		//JsonAsserts.print(orig, "orig");
		assertEquals(List.of("@context", "page", "limit", "total", "hasNext", "hasPrevious", "content"), orig.keys());
		assertEquals("http://api.magex.ca/crm/schema/system/Page", orig.getString("@context"));
		assertEquals(1, orig.getNumber("page"));
		assertEquals(10, orig.getNumber("limit"));
		assertEquals(1, orig.getNumber("total"));
		assertEquals(false, orig.getBoolean("hasNext"));
		assertEquals(false, orig.getBoolean("hasPrevious"));
		assertEquals(1, orig.getArray("content").size());
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "lastModified", "actions"), orig.getArray("content").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", orig.getArray("content").getObject(0).getString("@context"));
		assertEquals(Crm.REST_BASE + getSystemAdminIdentifier(), orig.getArray("content").getObject(0).getString("personId"));
		assertEquals(Crm.REST_BASE + getSystemOrganizationIdentifier(), orig.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), orig.getArray("content").getObject(0).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", orig.getArray("content").getObject(0).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", orig.getArray("content").getObject(0).getObject("status").getString("@id"));
		assertEquals("ACTIVE", orig.getArray("content").getObject(0).getObject("status").getString("@value"));
		assertEquals("Active", orig.getArray("content").getObject(0).getObject("status").getString("@en"));
		assertEquals("Actif", orig.getArray("content").getObject(0).getObject("status").getString("@fr"));
		assertEquals("Admin", orig.getArray("content").getObject(0).getString("displayName"));

		JsonObject create = post("/persons", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
				.with("organizationId", organizationId.toString())
				.with("displayName", CrmAsserts.displayName(ADAM))
				.with("legalName", new PersonNameJsonTransformer(crm).format(ADAM, Lang.ENGLISH))
				.with("address", new MailingAddressJsonTransformer(crm).format(MAILING_ADDRESS, Lang.ENGLISH))
				.with("communication", new CommunicationJsonTransformer(crm).format(WORK_COMMUNICATIONS, Lang.ENGLISH))
				.with("businessRoleIds", List.of(new IdentifierJsonTransformer(crm).format(EXTERNAL_OWNER, Lang.ENGLISH))));
		//JsonAsserts.print(create, "create");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), create.keys());
		assertEquals(organizationId.getCode(), create.getString("organizationId"));
		assertEquals("Active", create.getString("status"));
		assertEquals("Anderson, Adam A", create.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), create.getObject("legalName").keys());
		assertEquals("Mr.", create.getObject("legalName").getString("salutation"));
		assertEquals("Adam", create.getObject("legalName").getString("firstName"));
		assertEquals("A", create.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", create.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), create.getObject("address").keys());
		assertEquals("123 Main St", create.getObject("address").getString("street"));
		assertEquals("Ottawa", create.getObject("address").getString("city"));
		assertEquals("Quebec", create.getObject("address").getString("province"));
		assertEquals("Canada", create.getObject("address").getString("country"));
		assertEquals("K1K1K1", create.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), create.getObject("communication").keys());
		assertEquals("Developer", create.getObject("communication").getString("jobTitle"));
		assertEquals("English", create.getObject("communication").getString("language"));
		assertEquals("user@work.ca", create.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), create.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", create.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", create.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", create.getObject("communication").getString("faxNumber"));
		assertEquals(1, create.getArray("businessRoleIds").size());
		assertEquals("Owner", create.getArray("businessRoleIds").getString(0));

		PersonIdentifier personId = new PersonIdentifier(create.getString("personId"));
		
		JsonObject fetch = get(personId + "/details", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(fetch, "fetch");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), fetch.keys());
		assertEquals(personId.getCode(), fetch.getString("personId"));
		assertEquals(organizationId.getCode(), fetch.getString("organizationId"));
		assertEquals("ACTIVE", fetch.getString("status"));
		assertEquals("Anderson, Adam A", fetch.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), fetch.getObject("legalName").keys());
		assertEquals("MR", fetch.getObject("legalName").getString("salutation"));
		assertEquals("Adam", fetch.getObject("legalName").getString("firstName"));
		assertEquals("A", fetch.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", fetch.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), fetch.getObject("address").keys());
		assertEquals("123 Main St", fetch.getObject("address").getString("street"));
		assertEquals("Ottawa", fetch.getObject("address").getString("city"));
		assertEquals("CA/QC", fetch.getObject("address").getString("province"));
		assertEquals("CA", fetch.getObject("address").getString("country"));
		assertEquals("K1K1K1", fetch.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), fetch.getObject("communication").keys());
		assertEquals("Developer", fetch.getObject("communication").getString("jobTitle"));
		assertEquals("EN", fetch.getObject("communication").getString("language"));
		assertEquals("user@work.ca", fetch.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), fetch.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", fetch.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", fetch.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", fetch.getObject("communication").getString("faxNumber"));
		assertEquals(1, fetch.getArray("businessRoleIds").size());
		assertEquals("EXTERNAL/OWNER", fetch.getArray("businessRoleIds").getString(0));

		JsonObject english = get(personId + "/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Anderson, Adam A", english.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Adam", english.getObject("legalName").getString("firstName"));
		assertEquals("A", english.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", english.getObject("legalName").getString("lastName"));
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
		assertEquals("Owner", english.getArray("businessRoleIds").getString(0));
		
		JsonObject french = get(personId + "/details", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Anderson, Adam A", french.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Adam", french.getObject("legalName").getString("firstName"));
		assertEquals("A", french.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", french.getObject("legalName").getString("lastName"));
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
		assertEquals("Propriétaire", french.getArray("businessRoleIds").getString(0));
		
		JsonObject jsonld = get(personId + "/details", null, HttpStatus.OK);
		//JsonAsserts.print(jsonld, "jsonld");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), jsonld.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", jsonld.getString("@context"));
		assertEquals(Crm.REST_BASE + personId, jsonld.getString("personId"));
		assertEquals(Crm.REST_BASE + organizationId, jsonld.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", jsonld.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", jsonld.getObject("status").getString("@id"));
		assertEquals("ACTIVE", jsonld.getObject("status").getString("@value"));
		assertEquals("Active", jsonld.getObject("status").getString("@en"));
		assertEquals("Actif", jsonld.getObject("status").getString("@fr"));
		assertEquals("Anderson, Adam A", jsonld.getString("displayName"));
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), jsonld.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", jsonld.getObject("legalName").getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("legalName").getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", jsonld.getObject("legalName").getObject("salutation").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", jsonld.getObject("legalName").getObject("salutation").getString("@id"));
		assertEquals("MR", jsonld.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", jsonld.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", jsonld.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Adam", jsonld.getObject("legalName").getString("firstName"));
		assertEquals("A", jsonld.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", jsonld.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), jsonld.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", jsonld.getObject("address").getString("@context"));
		assertEquals("123 Main St", jsonld.getObject("address").getString("street"));
		assertEquals("Ottawa", jsonld.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", jsonld.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/qc", jsonld.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/QC", jsonld.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", jsonld.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", jsonld.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", jsonld.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", jsonld.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", jsonld.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", jsonld.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", jsonld.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", jsonld.getObject("address").getString("postalCode"));
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), jsonld.getObject("communication").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", jsonld.getObject("communication").getString("@context"));
		assertEquals("Developer", jsonld.getObject("communication").getString("jobTitle"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("communication").getObject("language").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Languages", jsonld.getObject("communication").getObject("language").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/languages/en", jsonld.getObject("communication").getObject("language").getString("@id"));
		assertEquals("EN", jsonld.getObject("communication").getObject("language").getString("@value"));
		assertEquals("English", jsonld.getObject("communication").getObject("language").getString("@en"));
		assertEquals("Anglais", jsonld.getObject("communication").getObject("language").getString("@fr"));
		assertEquals("user@work.ca", jsonld.getObject("communication").getString("email"));
		assertEquals(List.of("@context", "number", "extension"), jsonld.getObject("communication").getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", jsonld.getObject("communication").getObject("homePhone").getString("@context"));
		assertEquals("5551234567", jsonld.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", jsonld.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", jsonld.getObject("communication").getString("faxNumber"));
		assertEquals(1, jsonld.getArray("businessRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getArray("businessRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", jsonld.getArray("businessRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/owner", jsonld.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("EXTERNAL/OWNER", jsonld.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Owner", jsonld.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Propriétaire", jsonld.getArray("businessRoleIds").getObject(0).getString("@fr"));
		
		JsonObject paging = get("/persons", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(paging, "paging");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), paging.keys());
		assertEquals(1, paging.getNumber("page"));
		assertEquals(10, paging.getNumber("limit"));
		assertEquals(2, paging.getNumber("total"));
		assertEquals(false, paging.getBoolean("hasNext"));
		assertEquals(false, paging.getBoolean("hasPrevious"));
		assertEquals(2, paging.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "lastModified", "actions"), paging.getArray("content").getObject(0).keys());
		assertEquals(getSystemAdminIdentifier().getCode(), paging.getArray("content").getObject(0).getString("personId"));
		assertEquals(getSystemOrganizationIdentifier().getCode(), paging.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", paging.getArray("content").getObject(0).getString("status"));
		assertEquals("Admin", paging.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "lastModified", "actions"), paging.getArray("content").getObject(1).keys());
		assertEquals(personId.getCode(), paging.getArray("content").getObject(1).getString("personId"));
		assertEquals(organizationId.getCode(), paging.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", paging.getArray("content").getObject(1).getString("status"));
		assertEquals("Anderson, Adam A", paging.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testGetPersonDetails() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(BOB), BOB, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId + "/details", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Robert, Bob K", root.getString("displayName"));
		assertEquals(List.of("firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("Bob", root.getObject("legalName").getString("firstName"));
		assertEquals("K", root.getObject("legalName").getString("middleName"));
		assertEquals("Robert", root.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("465 Huntington Ave", root.getObject("address").getString("street"));
		assertEquals("Boston", root.getObject("address").getString("city"));
		assertEquals("US/MA", root.getObject("address").getString("province"));
		assertEquals("US", root.getObject("address").getString("country"));
		assertEquals("02115", root.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), root.getObject("communication").keys());
		assertEquals("Developer", root.getObject("communication").getString("jobTitle"));
		assertEquals("EN", root.getObject("communication").getString("language"));
		assertEquals("user@work.ca", root.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), root.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", root.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getObject("communication").getString("faxNumber"));
		assertEquals(1, root.getArray("businessRoleIds").size());
		assertEquals("EXTERNAL/OWNER", root.getArray("businessRoleIds").getString(0));
				
		JsonObject english = get(personId + "/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Robert, Bob K", english.getString("displayName"));
		assertEquals(List.of("firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Bob", english.getObject("legalName").getString("firstName"));
		assertEquals("K", english.getObject("legalName").getString("middleName"));
		assertEquals("Robert", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("465 Huntington Ave", english.getObject("address").getString("street"));
		assertEquals("Boston", english.getObject("address").getString("city"));
		assertEquals("Massachusetts", english.getObject("address").getString("province"));
		assertEquals("United States", english.getObject("address").getString("country"));
		assertEquals("02115", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(1, english.getArray("businessRoleIds").size());
		assertEquals("Owner", english.getArray("businessRoleIds").getString(0));
				
		JsonObject french = get(personId + "/details", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Robert, Bob K", french.getString("displayName"));
		assertEquals(List.of("firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("Bob", french.getObject("legalName").getString("firstName"));
		assertEquals("K", french.getObject("legalName").getString("middleName"));
		assertEquals("Robert", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("465 Huntington Ave", french.getObject("address").getString("street"));
		assertEquals("Boston", french.getObject("address").getString("city"));
		assertEquals("Massachusetts", french.getObject("address").getString("province"));
		assertEquals("États-Unis d'Amérique", french.getObject("address").getString("country"));
		assertEquals("02115", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(1, french.getArray("businessRoleIds").size());
		assertEquals("Propriétaire", french.getArray("businessRoleIds").getString(0));
				
		JsonObject linked = get(personId + "/details", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + personId, linked.getString("personId"));
		assertEquals(Crm.REST_BASE + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Robert, Bob K", linked.getString("displayName"));
		assertEquals(List.of("@context", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getObject("legalName").getString("@context"));
		assertEquals("Bob", linked.getObject("legalName").getString("firstName"));
		assertEquals("K", linked.getObject("legalName").getString("middleName"));
		assertEquals("Robert", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("465 Huntington Ave", linked.getObject("address").getString("street"));
		assertEquals("Boston", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/us/ma", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("US/MA", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Massachusetts", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Massachusetts", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/us", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("US", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("United States", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("États-Unis d'Amérique", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("02115", linked.getObject("address").getString("postalCode"));
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
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/owner", linked.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("EXTERNAL/OWNER", linked.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Owner", linked.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Propriétaire", linked.getArray("businessRoleIds").getObject(0).getString("@fr"));
	}
	
	@Test
	public void testGetPersonSummary() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(BOB), BOB, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId, Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "lastModified"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Robert, Bob K", root.getString("displayName"));
		
		JsonObject english = get(personId, Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "lastModified"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Robert, Bob K", english.getString("displayName"));
				
		JsonObject french = get(personId, Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "lastModified"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Robert, Bob K", french.getString("displayName"));
				
		JsonObject linked = get(personId, null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "lastModified"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + personId, linked.getString("personId"));
		assertEquals(Crm.REST_BASE + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Robert, Bob K", linked.getString("displayName"));
	}
	
	@Test
	public void testGetPersonName() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId + "/details/legalName", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), root.keys());
		assertEquals("MR", root.getString("salutation"));
		assertEquals("Adam", root.getString("firstName"));
		assertEquals("A", root.getString("middleName"));
		assertEquals("Anderson", root.getString("lastName"));
		
		JsonObject english = get(personId + "/details/legalName", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.keys());
		assertEquals("Mr.", english.getString("salutation"));
		assertEquals("Adam", english.getString("firstName"));
		assertEquals("A", english.getString("middleName"));
		assertEquals("Anderson", english.getString("lastName"));
		
		JsonObject french = get(personId + "/details/legalName", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.keys());
		assertEquals("M.", french.getString("salutation"));
		assertEquals("Adam", french.getString("firstName"));
		assertEquals("A", french.getString("middleName"));
		assertEquals("Anderson", french.getString("lastName"));
		
		JsonObject linked = get(personId + "/details/legalName", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", linked.getObject("salutation").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", linked.getObject("salutation").getString("@id"));
		assertEquals("MR", linked.getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("salutation").getString("@fr"));
		assertEquals("Adam", linked.getString("firstName"));
		assertEquals("A", linked.getString("middleName"));
		assertEquals("Anderson", linked.getString("lastName"));
	}
	
	@Test
	public void testGetPersonAddress() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId + "/details/address", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.keys());
		assertEquals("465 Huntington Ave", root.getString("street"));
		assertEquals("Boston", root.getString("city"));
		assertEquals("US/MA", root.getString("province"));
		assertEquals("US", root.getString("country"));
		assertEquals("02115", root.getString("postalCode"));
		
		JsonObject english = get(personId + "/details/address", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("465 Huntington Ave", english.getString("street"));
		assertEquals("Boston", english.getString("city"));
		assertEquals("Massachusetts", english.getString("province"));
		assertEquals("United States", english.getString("country"));
		assertEquals("02115", english.getString("postalCode"));
		
		JsonObject french = get(personId + "/details/address", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("465 Huntington Ave", french.getString("street"));
		assertEquals("Boston", french.getString("city"));
		assertEquals("Massachusetts", french.getString("province"));
		assertEquals("États-Unis d'Amérique", french.getString("country"));
		assertEquals("02115", french.getString("postalCode"));
		
		JsonObject linked = get(personId + "/details/address", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getString("@context"));
		assertEquals("465 Huntington Ave", linked.getString("street"));
		assertEquals("Boston", linked.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/us/ma", linked.getObject("province").getString("@id"));
		assertEquals("US/MA", linked.getObject("province").getString("@value"));
		assertEquals("Massachusetts", linked.getObject("province").getString("@en"));
		assertEquals("Massachusetts", linked.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/us", linked.getObject("country").getString("@id"));
		assertEquals("US", linked.getObject("country").getString("@value"));
		assertEquals("United States", linked.getObject("country").getString("@en"));
		assertEquals("États-Unis d'Amérique", linked.getObject("country").getString("@fr"));
		assertEquals("02115", linked.getString("postalCode"));
	}
	
	@Test
	public void testGetPersonCommunication() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId + "/details/communication", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), root.keys());
		assertEquals("Developer", root.getString("jobTitle"));
		assertEquals("EN", root.getString("language"));
		assertEquals("user@work.ca", root.getString("email"));
		assertEquals(List.of("number", "extension"), root.getObject("homePhone").keys());
		assertEquals("5551234567", root.getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getString("faxNumber"));
		
		JsonObject english = get(personId + "/details/communication", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.keys());
		assertEquals("Developer", english.getString("jobTitle"));
		assertEquals("English", english.getString("language"));
		assertEquals("user@work.ca", english.getString("email"));
		assertEquals(List.of("number", "extension"), english.getObject("homePhone").keys());
		assertEquals("5551234567", english.getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getString("faxNumber"));
		
		JsonObject french = get(personId + "/details/communication", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), french.keys());
		assertEquals("Developer", french.getString("jobTitle"));
		assertEquals("Anglais", french.getString("language"));
		assertEquals("user@work.ca", french.getString("email"));
		assertEquals(List.of("number", "extension"), french.getObject("homePhone").keys());
		assertEquals("5551234567", french.getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getString("faxNumber"));
		
		JsonObject linked = get(personId + "/details/communication", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", linked.getString("@context"));
		assertEquals("Developer", linked.getString("jobTitle"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("language").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Languages", linked.getObject("language").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/languages/en", linked.getObject("language").getString("@id"));
		assertEquals("EN", linked.getObject("language").getString("@value"));
		assertEquals("English", linked.getObject("language").getString("@en"));
		assertEquals("Anglais", linked.getObject("language").getString("@fr"));
		assertEquals("user@work.ca", linked.getString("email"));
		assertEquals(List.of("@context", "number", "extension"), linked.getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", linked.getObject("homePhone").getString("@context"));
		assertEquals("5551234567", linked.getObject("homePhone").getString("number"));
		assertEquals("42", linked.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", linked.getString("faxNumber"));
	}
	
	@Test
	public void testGetPersonRoles() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject root = get(personId + "/details/businessRoles", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("total", "content"), root.keys());
		assertEquals(1, root.getNumber("total"));
		assertEquals(1, root.getArray("content").size());
		assertEquals("EXTERNAL/OWNER", root.getArray("content").getString(0));
		
		JsonObject english = get(personId + "/details/businessRoles", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("total", "content"), english.keys());
		assertEquals(1, english.getNumber("total"));
		assertEquals(1, english.getArray("content").size());
		assertEquals("Owner", english.getArray("content").getString(0));
		
		JsonObject french = get(personId + "/details/businessRoles", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("total", "content"), french.keys());
		assertEquals(1, french.getNumber("total"));
		assertEquals(1, french.getArray("content").size());
		assertEquals("Propriétaire", french.getArray("content").getString(0));
		
		JsonObject linked = get(personId + "/details/businessRoles", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("total", "content"), linked.keys());
		assertEquals(1, linked.getNumber("total"));
		assertEquals(1, linked.getArray("content").size());
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/owner", linked.getArray("content").getString(0));
	}
	
	@Test
	public void testUpdatingDisplayName() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		
		JsonObject json = patch(personId + "/details", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
				.with("displayName", "Chloé")
				.with("legalName", new PersonNameJsonTransformer(crm).format(CHLOE, Lang.ENGLISH)));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), json.keys());
		assertEquals(personId.getCode(), json.getString("personId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Chloé", json.getString("displayName"));
		assertEquals(List.of("firstName", "lastName"), json.getObject("legalName").keys());
		assertEquals("Chloé", json.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(1, json.getArray("businessRoleIds").size());
		assertEquals("Owner", json.getArray("businessRoleIds").getString(0));
	}
	
	@Test
	public void testUpdatingAddress() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();

		JsonObject json = patch(personId + "/details", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("address", new MailingAddressJsonTransformer(crm).format(MX_ADDRESS, Lang.ENGLISH)));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), json.keys());
		assertEquals(personId.getCode(), json.getString("personId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("Mr.", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("120 Col. Hipodromo Condesa", json.getObject("address").getString("street"));
		assertEquals("Monterrey", json.getObject("address").getString("city"));
		assertEquals("Nuevo Leon", json.getObject("address").getString("province"));
		assertEquals("Mexico", json.getObject("address").getString("country"));
		assertEquals("06100", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), json.getObject("communication").keys());
		assertEquals("Developer", json.getObject("communication").getString("jobTitle"));
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@work.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", json.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", json.getObject("communication").getString("faxNumber"));
		assertEquals(1, json.getArray("businessRoleIds").size());
		assertEquals("Owner", json.getArray("businessRoleIds").getString(0));
	}

	@Test
	public void testUpdatingCommunication() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();

		JsonObject json = patch(personId + "/details", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("communication", new CommunicationJsonTransformer(crm).format(HOME_COMMUNICATIONS, Lang.ENGLISH)));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), json.keys());
		assertEquals(personId.getCode(), json.getString("personId"));
		assertEquals(organizationId.getCode(), json.getString("organizationId"));
		assertEquals("Active", json.getString("status"));
		assertEquals("Anderson, Adam A", json.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), json.getObject("legalName").keys());
		assertEquals("Mr.", json.getObject("legalName").getString("salutation"));
		assertEquals("Adam", json.getObject("legalName").getString("firstName"));
		assertEquals("A", json.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", json.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), json.getObject("address").keys());
		assertEquals("465 Huntington Ave", json.getObject("address").getString("street"));
		assertEquals("Boston", json.getObject("address").getString("city"));
		assertEquals("Massachusetts", json.getObject("address").getString("province"));
		assertEquals("United States", json.getObject("address").getString("country"));
		assertEquals("02115", json.getObject("address").getString("postalCode"));
		assertEquals(List.of("language", "email", "homePhone"), json.getObject("communication").keys());
		assertEquals("English", json.getObject("communication").getString("language"));
		assertEquals("user@home.ca", json.getObject("communication").getString("email"));
		assertEquals(List.of("number"), json.getObject("communication").getObject("homePhone").keys());
		assertEquals("5558883333", json.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals(1, json.getArray("businessRoleIds").size());
		assertEquals("Owner", json.getArray("businessRoleIds").getString(0));
	}

	@Test
	public void testUpdatingPosition() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(ADAM), ADAM, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();

		JsonObject root = patch(personId + "/details", Lang.ROOT, HttpStatus.OK, new JsonObject()
			.with("businessRoleIds", new JsonArray()
				.with(new IdentifierJsonTransformer(crm).format(BusinessRoleIdentifier.EXECS_CEO, Lang.ROOT))
				.with(new IdentifierJsonTransformer(crm).format(BusinessRoleIdentifier.IMIT_DIRECTOR, Lang.ROOT))));
		
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Anderson, Adam A", root.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("MR", root.getObject("legalName").getString("salutation"));
		assertEquals("Adam", root.getObject("legalName").getString("firstName"));
		assertEquals("A", root.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", root.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("465 Huntington Ave", root.getObject("address").getString("street"));
		assertEquals("Boston", root.getObject("address").getString("city"));
		assertEquals("US/MA", root.getObject("address").getString("province"));
		assertEquals("US", root.getObject("address").getString("country"));
		assertEquals("02115", root.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), root.getObject("communication").keys());
		assertEquals("Developer", root.getObject("communication").getString("jobTitle"));
		assertEquals("EN", root.getObject("communication").getString("language"));
		assertEquals("user@work.ca", root.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), root.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", root.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getObject("communication").getString("faxNumber"));
		assertEquals(2, root.getArray("businessRoleIds").size());
		assertEquals("EXECS/CEO", root.getArray("businessRoleIds").getString(0));
		assertEquals("IMIT/DIRECTOR", root.getArray("businessRoleIds").getString(1));

		JsonObject english = patch(personId + "/details", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("businessRoleIds", new JsonArray()
				.with(new IdentifierJsonTransformer(crm).format(BusinessRoleIdentifier.SYS_ADMINISTRATOR, Lang.ENGLISH))
				.with(new IdentifierJsonTransformer(crm).format(BusinessRoleIdentifier.DEVELOPER, Lang.ENGLISH))));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Anderson, Adam A", english.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Adam", english.getObject("legalName").getString("firstName"));
		assertEquals("A", english.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("465 Huntington Ave", english.getObject("address").getString("street"));
		assertEquals("Boston", english.getObject("address").getString("city"));
		assertEquals("Massachusetts", english.getObject("address").getString("province"));
		assertEquals("United States", english.getObject("address").getString("country"));
		assertEquals("02115", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(2, english.getArray("businessRoleIds").size());
		assertEquals("System Administrator", english.getArray("businessRoleIds").getString(0));
		assertEquals("Developer", english.getArray("businessRoleIds").getString(1));

		JsonObject french = patch(personId + "/details", Lang.FRENCH, HttpStatus.OK, new JsonObject()
			.with("businessRoleIds", new JsonArray()
					.with(new IdentifierJsonTransformer(crm).format(BusinessRoleIdentifier.TESTER, Lang.FRENCH))));
			
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Anderson, Adam A", french.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Adam", french.getObject("legalName").getString("firstName"));
		assertEquals("A", french.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("465 Huntington Ave", french.getObject("address").getString("street"));
		assertEquals("Boston", french.getObject("address").getString("city"));
		assertEquals("Massachusetts", french.getObject("address").getString("province"));
		assertEquals("États-Unis d'Amérique", french.getObject("address").getString("country"));
		assertEquals("02115", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(1, french.getArray("businessRoleIds").size());
		assertEquals("Testeur de qualité", french.getArray("businessRoleIds").getString(0));

		JsonObject linked = patch(personId + "/details", null, HttpStatus.OK, new JsonObject()
			.with("businessRoleIds", new JsonArray()
				.with(new OptionJsonTransformer(crm).format(crm.findOption(BusinessRoleIdentifier.EXTERNAL_OWNER), null))
				.with(new OptionJsonTransformer(crm).format(crm.findOption(BusinessRoleIdentifier.EXTERNAL_EMPLOYEE), null))
				.with(new OptionJsonTransformer(crm).format(crm.findOption(BusinessRoleIdentifier.EXTERNAL_CONTACT), null))));
			
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds", "lastModified"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + personId, linked.getString("personId"));
		assertEquals(Crm.REST_BASE + organizationId, linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Anderson, Adam A", linked.getString("displayName"));
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getObject("legalName").getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("legalName").getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", linked.getObject("legalName").getObject("salutation").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", linked.getObject("legalName").getObject("salutation").getString("@id"));
		assertEquals("MR", linked.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Adam", linked.getObject("legalName").getString("firstName"));
		assertEquals("A", linked.getObject("legalName").getString("middleName"));
		assertEquals("Anderson", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("465 Huntington Ave", linked.getObject("address").getString("street"));
		assertEquals("Boston", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/us/ma", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("US/MA", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Massachusetts", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Massachusetts", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/us", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("US", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("United States", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("États-Unis d'Amérique", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("02115", linked.getObject("address").getString("postalCode"));
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
		assertEquals(3, linked.getArray("businessRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", linked.getArray("businessRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/owner", linked.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("EXTERNAL/OWNER", linked.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Owner", linked.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Propriétaire", linked.getArray("businessRoleIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessRoleIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", linked.getArray("businessRoleIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/employee", linked.getArray("businessRoleIds").getObject(1).getString("@id"));
		assertEquals("EXTERNAL/EMPLOYEE", linked.getArray("businessRoleIds").getObject(1).getString("@value"));
		assertEquals("Employee", linked.getArray("businessRoleIds").getObject(1).getString("@en"));
		assertEquals("Employé", linked.getArray("businessRoleIds").getObject(1).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessRoleIds").getObject(2).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", linked.getArray("businessRoleIds").getObject(2).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/contact", linked.getArray("businessRoleIds").getObject(2).getString("@id"));
		assertEquals("EXTERNAL/CONTACT", linked.getArray("businessRoleIds").getObject(2).getString("@value"));
		assertEquals("Contact", linked.getArray("businessRoleIds").getObject(2).getString("@en"));
		assertEquals("Contact", linked.getArray("businessRoleIds").getObject(2).getString("@fr"));
	}

	@Test
	public void testEnableDisablePerson() throws Exception {
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.displayName(BOB), BOB, US_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error1 = put(personId + "/actions/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(personId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("Error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("Field is required", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error2 = put(personId + "/actions/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(personId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("Error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("Field is required", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonArray error3 = put(personId + "/actions/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(personId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("Error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Format is invalid", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());

		JsonObject disable = put(personId + "/actions/disable", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(personId.getCode(), disable.getString("personId"));
		assertEquals(organizationId.getCode(), disable.getString("organizationId"));
		assertEquals("Inactive", disable.getString("status"));
		assertEquals("Robert, Bob K", disable.getString("displayName"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
		
		JsonArray error4 = put(personId + "/actions/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(personId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("Error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("Field is required", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
		
		JsonArray error5 = put(personId + "/actions/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(personId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("Error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("Field is required", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
		
		JsonArray error6 = put(personId + "/actions/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(personId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("Error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Format is invalid", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findPersonSummary(personId).getStatus());
	
		JsonObject enable = put(personId + "/actions/enable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(personId.getCode(), enable.getString("personId"));
		assertEquals(organizationId.getCode(), enable.getString("organizationId"));
		assertEquals("Actif", enable.getString("status"));
		assertEquals("Robert, Bob K", enable.getString("displayName"));
		assertEquals(Status.ACTIVE, crm.findPersonSummary(personId).getStatus());
	}
	
}
