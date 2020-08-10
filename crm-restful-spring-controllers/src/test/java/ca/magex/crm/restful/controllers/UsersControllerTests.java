package ca.magex.crm.restful.controllers;

import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_OWNER;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.transform.json.IdentifierJsonTransformer;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonObject;

public class UsersControllerTests extends AbstractControllerTests {

	private OrganizationIdentifier systemOrgId;
	
	private PersonIdentifier systemPersonId;
	
	private UserIdentifier systemUserId;
	
	private OrganizationIdentifier testOrgId;
	
	private PersonIdentifier testPersonId;
	
	@Before
	public void setup() {
		initialize();
		systemOrgId = getSystemOrganizationIdentifier();
		systemPersonId = getSystemAdminIdentifier();
		systemUserId = getSystemUserIdentifier();
		testOrgId = createTestOrganization("Test Org");
		testPersonId = crm.createPerson(testOrgId, CrmAsserts.displayName(CHLOE), CHLOE, MAILING_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
	}
	
	@Test
	public void testCreateUser() throws Exception {
		JsonObject orig = get("/users/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(orig, "orig");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), orig.keys());
		assertEquals(1, orig.getNumber("page"));
		assertEquals(10, orig.getNumber("limit"));
		assertEquals(1, orig.getNumber("total"));
		assertEquals(false, orig.getBoolean("hasNext"));
		assertEquals(false, orig.getBoolean("hasPrevious"));
		assertEquals(1, orig.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), orig.getArray("content").getObject(0).keys());
		assertEquals(systemUserId.getCode(), orig.getArray("content").getObject(0).getString("userId"));
		assertEquals(systemOrgId.getCode(), orig.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(systemPersonId.getCode(), orig.getArray("content").getObject(0).getString("personId"));
		assertEquals("admin", orig.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", orig.getArray("content").getObject(0).getString("status"));
		assertEquals(4, orig.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", orig.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("System Actuator", orig.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
		assertEquals("System Access", orig.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(2));
		assertEquals("CRM Admin", orig.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(3));
		
		JsonObject create = post("/users", Lang.ENGLISH, HttpStatus.OK, new JsonObject()
			.with("personId", testPersonId.toString())
			.with("username", "chloe")
			.with("authenticationRoleIds", List.of(
				new IdentifierJsonTransformer(crm).format(AuthenticationRoleIdentifier.ORG_ADMIN, Lang.ENGLISH),
				new IdentifierJsonTransformer(crm).format(AuthenticationRoleIdentifier.CRM_ADMIN, Lang.ENGLISH)
			)));
		//JsonAsserts.print(create, "create");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), create.keys());
		assertEquals(testOrgId.getCode(), create.getString("organizationId"));
		assertEquals(testPersonId.getCode(), create.getString("personId"));
		assertEquals("chloe", create.getString("username"));
		assertEquals("Active", create.getString("status"));
		assertEquals(2, create.getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", create.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", create.getArray("authenticationRoleIds").getString(1));		
		UserIdentifier userId = new UserIdentifier(create.getString("userId"));
		
		JsonObject fetch = get(userId, Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(fetch, "fetch");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), fetch.keys());
		assertEquals(userId.getCode(), fetch.getString("userId"));
		assertEquals(testOrgId.getCode(), fetch.getString("organizationId"));
		assertEquals(testPersonId.getCode(), fetch.getString("personId"));
		assertEquals("chloe", fetch.getString("username"));
		assertEquals("ACTIVE", fetch.getString("status"));
		assertEquals(2, fetch.getArray("authenticationRoleIds").size());
		assertEquals("ORG/ADMIN", fetch.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM/ADMIN", fetch.getArray("authenticationRoleIds").getString(1));
		
		JsonObject english = get(userId, Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), english.keys());
		assertEquals(userId.getCode(), english.getString("userId"));
		assertEquals(testOrgId.getCode(), english.getString("organizationId"));
		assertEquals(testPersonId.getCode(), english.getString("personId"));
		assertEquals("chloe", english.getString("username"));
		assertEquals("Active", english.getString("status"));
		assertEquals(2, english.getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", english.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", english.getArray("authenticationRoleIds").getString(1));
		
		JsonObject french = get(userId, Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), french.keys());
		assertEquals(userId.getCode(), french.getString("userId"));
		assertEquals(testOrgId.getCode(), french.getString("organizationId"));
		assertEquals(testPersonId.getCode(), french.getString("personId"));
		assertEquals("chloe", french.getString("username"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(2, french.getArray("authenticationRoleIds").size());
		assertEquals("Administrateur de l'organisation", french.getArray("authenticationRoleIds").getString(0));
		assertEquals("Administrateur GRC", french.getArray("authenticationRoleIds").getString(1));
		
		JsonObject jsonld = get(userId, null, HttpStatus.OK);
		//JsonAsserts.print(jsonld, "jsonld");
		assertEquals(List.of("@context", "userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), jsonld.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/UserDetails", jsonld.getString("@context"));
		assertEquals(Crm.REST_BASE + userId, jsonld.getString("userId"));
		assertEquals(Crm.REST_BASE + testOrgId, jsonld.getString("organizationId"));
		assertEquals(Crm.REST_BASE + testPersonId, jsonld.getString("personId"));
		assertEquals("chloe", jsonld.getString("username"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", jsonld.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", jsonld.getObject("status").getString("@id"));
		assertEquals("ACTIVE", jsonld.getObject("status").getString("@value"));
		assertEquals("Active", jsonld.getObject("status").getString("@en"));
		assertEquals("Actif", jsonld.getObject("status").getString("@fr"));
		assertEquals(2, jsonld.getArray("authenticationRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getArray("authenticationRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", jsonld.getArray("authenticationRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/org/admin", jsonld.getArray("authenticationRoleIds").getObject(0).getString("@id"));
		assertEquals("ORG/ADMIN", jsonld.getArray("authenticationRoleIds").getObject(0).getString("@value"));
		assertEquals("Organization Admin", jsonld.getArray("authenticationRoleIds").getObject(0).getString("@en"));
		assertEquals("Administrateur de l'organisation", jsonld.getArray("authenticationRoleIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), jsonld.getArray("authenticationRoleIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", jsonld.getArray("authenticationRoleIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/crm/admin", jsonld.getArray("authenticationRoleIds").getObject(1).getString("@id"));
		assertEquals("CRM/ADMIN", jsonld.getArray("authenticationRoleIds").getObject(1).getString("@value"));
		assertEquals("CRM Admin", jsonld.getArray("authenticationRoleIds").getObject(1).getString("@en"));
		assertEquals("Administrateur GRC", jsonld.getArray("authenticationRoleIds").getObject(1).getString("@fr"));
		
		JsonObject paging = get("/users/details", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(paging, "paging");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), paging.keys());
		assertEquals(1, paging.getNumber("page"));
		assertEquals(10, paging.getNumber("limit"));
		assertEquals(2, paging.getNumber("total"));
		assertEquals(false, paging.getBoolean("hasNext"));
		assertEquals(false, paging.getBoolean("hasPrevious"));
		assertEquals(2, paging.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), paging.getArray("content").getObject(0).keys());
		assertEquals(systemUserId.getCode(), paging.getArray("content").getObject(0).getString("userId"));
		assertEquals(systemOrgId.getCode(), paging.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(systemPersonId.getCode(), paging.getArray("content").getObject(0).getString("personId"));
		assertEquals("admin", paging.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", paging.getArray("content").getObject(0).getString("status"));
		assertEquals(4, paging.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", paging.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("System Actuator", paging.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
		assertEquals("System Access", paging.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(2));
		assertEquals("CRM Admin", paging.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(3));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), paging.getArray("content").getObject(1).keys());
		assertEquals(userId.getCode(), paging.getArray("content").getObject(1).getString("userId"));
		assertEquals(testOrgId.getCode(), paging.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(testPersonId.getCode(), paging.getArray("content").getObject(1).getString("personId"));
		assertEquals("chloe", paging.getArray("content").getObject(1).getString("username"));
		assertEquals("Active", paging.getArray("content").getObject(1).getString("status"));
		assertEquals(2, paging.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", paging.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", paging.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testGetUser() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();
		
		JsonObject root = get(userId, Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), root.keys());
		assertEquals(userId.getCode(), root.getString("userId"));
		assertEquals(testOrgId.getCode(), root.getString("organizationId"));
		assertEquals(testPersonId.getCode(), root.getString("personId"));
		assertEquals("chloe", root.getString("username"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals(2, root.getArray("authenticationRoleIds").size());
		assertEquals("ORG/ADMIN", root.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM/ADMIN", root.getArray("authenticationRoleIds").getString(1));

		assertEquals(root, get("/user/chloe", Lang.ROOT, HttpStatus.OK));
		
		JsonObject english = get(userId, Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), english.keys());
		assertEquals(userId.getCode(), english.getString("userId"));
		assertEquals(testOrgId.getCode(), english.getString("organizationId"));
		assertEquals(testPersonId.getCode(), english.getString("personId"));
		assertEquals("chloe", english.getString("username"));
		assertEquals("Active", english.getString("status"));
		assertEquals(2, english.getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", english.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", english.getArray("authenticationRoleIds").getString(1));
		
		assertEquals(english, get("/user/chloe", Lang.ENGLISH, HttpStatus.OK));
		
		JsonObject french = get(userId, Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), french.keys());
		assertEquals(userId.getCode(), french.getString("userId"));
		assertEquals(testOrgId.getCode(), french.getString("organizationId"));
		assertEquals(testPersonId.getCode(), french.getString("personId"));
		assertEquals("chloe", french.getString("username"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(2, french.getArray("authenticationRoleIds").size());
		assertEquals("Administrateur de l'organisation", french.getArray("authenticationRoleIds").getString(0));
		assertEquals("Administrateur GRC", french.getArray("authenticationRoleIds").getString(1));
		
		assertEquals(french, get("/user/chloe", Lang.FRENCH, HttpStatus.OK));
		
		JsonObject linked = get(userId, null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/UserDetails", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + userId.toString(), linked.getString("userId"));
		assertEquals(Crm.REST_BASE + testOrgId.toString(), linked.getString("organizationId"));
		assertEquals(Crm.REST_BASE + testPersonId.toString(), linked.getString("personId"));
		assertEquals("chloe", linked.getString("username"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals(2, linked.getArray("authenticationRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("authenticationRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", linked.getArray("authenticationRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/org/admin", linked.getArray("authenticationRoleIds").getObject(0).getString("@id"));
		assertEquals("ORG/ADMIN", linked.getArray("authenticationRoleIds").getObject(0).getString("@value"));
		assertEquals("Organization Admin", linked.getArray("authenticationRoleIds").getObject(0).getString("@en"));
		assertEquals("Administrateur de l'organisation", linked.getArray("authenticationRoleIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("authenticationRoleIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", linked.getArray("authenticationRoleIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/crm/admin", linked.getArray("authenticationRoleIds").getObject(1).getString("@id"));
		assertEquals("CRM/ADMIN", linked.getArray("authenticationRoleIds").getObject(1).getString("@value"));
		assertEquals("CRM Admin", linked.getArray("authenticationRoleIds").getObject(1).getString("@en"));
		assertEquals("Administrateur GRC", linked.getArray("authenticationRoleIds").getObject(1).getString("@fr"));

		assertEquals(linked, get("/user/chloe", null, HttpStatus.OK));
	}
	
	@Test
	public void testGetUserPerson() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();
		
		JsonObject root = get(userId + "/person", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), root.keys());
		assertEquals(testPersonId.getCode(), root.getString("personId"));
		assertEquals(testOrgId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("LaRue, Chloé", root.getString("displayName"));
		assertEquals(List.of("firstName", "lastName"), root.getObject("legalName").keys());
		assertEquals("Chloé", root.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", root.getObject("legalName").getString("lastName"));
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
		assertEquals("EXTERNAL/OWNER", root.getArray("businessRoleIds").getString(0));
		
		assertEquals(root, get("/user/chloe/person", Lang.ROOT, HttpStatus.OK));
		
		JsonObject english = get(userId + "/person", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), english.keys());
		assertEquals(testPersonId.getCode(), english.getString("personId"));
		assertEquals(testOrgId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("LaRue, Chloé", english.getString("displayName"));
		assertEquals(List.of("firstName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Chloé", english.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", english.getObject("legalName").getString("lastName"));
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
		
		assertEquals(english, get("/user/chloe/person", Lang.ENGLISH, HttpStatus.OK));
		
		JsonObject french = get(userId + "/person", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), french.keys());
		assertEquals(testPersonId.getCode(), french.getString("personId"));
		assertEquals(testOrgId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("LaRue, Chloé", french.getString("displayName"));
		assertEquals(List.of("firstName", "lastName"), french.getObject("legalName").keys());
		assertEquals("Chloé", french.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", french.getObject("legalName").getString("lastName"));
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
		
		assertEquals(french, get("/user/chloe/person", Lang.FRENCH, HttpStatus.OK));
		
		JsonObject linked = get(userId + "/person", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", linked.getString("@context"));
		assertEquals(Crm.REST_BASE + testPersonId.toString(), linked.getString("personId"));
		assertEquals(Crm.REST_BASE + testOrgId.toString(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("LaRue, Chloé", linked.getString("displayName"));
		assertEquals(List.of("@context", "firstName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getObject("legalName").getString("@context"));
		assertEquals("Chloé", linked.getObject("legalName").getString("firstName"));
		assertEquals("LaRue", linked.getObject("legalName").getString("lastName"));
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
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/external/owner", linked.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("EXTERNAL/OWNER", linked.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Owner", linked.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Propriétaire", linked.getArray("businessRoleIds").getObject(0).getString("@fr"));
		
		assertEquals(linked, get("/user/chloe/person", null, HttpStatus.OK));
	}
	
	@Test
	public void testUpdatingUsernameNotChanged() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();
		
		JsonObject json = patch(userId, Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("username", "updated"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.keys());
		assertEquals(userId.getCode(), json.getString("userId"));
		assertEquals(testOrgId.getCode(), json.getString("organizationId"));
		assertEquals(testPersonId.getCode(), json.getString("personId"));
		assertEquals("chloe", json.getString("username"));
		assertEquals("Active", json.getString("status"));
		assertEquals(2, json.getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", json.getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testUpdatingRoles() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();
		
		JsonObject json = patch(userId, Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("authenticationRoleIds", List.of("Organization Viewer", "System Administrator")));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.keys());
		assertEquals(userId.getCode(), json.getString("userId"));
		assertEquals(testOrgId.getCode(), json.getString("organizationId"));
		assertEquals(testPersonId.getCode(), json.getString("personId"));
		assertEquals("chloe", json.getString("username"));
		assertEquals("Active", json.getString("status"));
		assertEquals(2, json.getArray("authenticationRoleIds").size());
		assertEquals("Organization Viewer", json.getArray("authenticationRoleIds").getString(0));
		assertEquals("System Administrator", json.getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testUpdatingRolesByUsername() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.CRM_USER)).getUserId();
		
		JsonObject json = patch("/user/chloe", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("authenticationRoleIds", List.of("Visionneuse GRC")));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.keys());
		assertEquals(userId.getCode(), json.getString("userId"));
		assertEquals(testOrgId.getCode(), json.getString("organizationId"));
		assertEquals(testPersonId.getCode(), json.getString("personId"));
		assertEquals("chloe", json.getString("username"));
		assertEquals("Actif", json.getString("status"));
		assertEquals(1, json.getArray("authenticationRoleIds").size());
		assertEquals("Visionneuse GRC", json.getArray("authenticationRoleIds").getString(0));
	}
	
	@Test
	public void testGetUserRolesByUserId() throws Exception {
		Identifier userId = crm.createUser(testPersonId, "chloe", 
				List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();
		
		JsonObject root = get(userId + "/authenticationRoleIds", Lang.ROOT, HttpStatus.OK);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("total", "content"), root.keys());
		assertEquals(2, root.getNumber("total"));
		assertEquals(2, root.getArray("content").size());
		assertEquals("ORG/ADMIN", root.getArray("content").getString(0));
		assertEquals("CRM/ADMIN", root.getArray("content").getString(1));
		assertEquals(root, get("/user/chloe/authenticationRoleIds", Lang.ROOT, HttpStatus.OK));
				
		JsonObject english = get(userId + "/authenticationRoleIds", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("total", "content"), english.keys());
		assertEquals(2, english.getNumber("total"));
		assertEquals(2, english.getArray("content").size());
		assertEquals("Organization Admin", english.getArray("content").getString(0));
		assertEquals("CRM Admin", english.getArray("content").getString(1));
		assertEquals(english, get("/user/chloe/authenticationRoleIds", Lang.ENGLISH, HttpStatus.OK));
				
		JsonObject french = get(userId + "/authenticationRoleIds", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("total", "content"), french.keys());
		assertEquals(2, french.getNumber("total"));
		assertEquals(2, french.getArray("content").size());
		assertEquals("Administrateur de l'organisation", french.getArray("content").getString(0));
		assertEquals("Administrateur GRC", french.getArray("content").getString(1));
		assertEquals(french, get("/user/chloe/authenticationRoleIds", Lang.FRENCH, HttpStatus.OK));
				
		JsonObject linked = get(userId + "/authenticationRoleIds", null, HttpStatus.OK);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("total", "content"), linked.keys());
		assertEquals(2, linked.getNumber("total"));
		assertEquals(2, linked.getArray("content").size());
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/org/admin", linked.getArray("content").getString(0));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/crm/admin", linked.getArray("content").getString(1));
		assertEquals(linked, get("/user/chloe/authenticationRoleIds", null, HttpStatus.OK));
	}

	@Test
	public void testEnableDisablePerson() throws Exception {
		UserIdentifier userId = crm.createUser(testPersonId, "chloe", List.of(AuthenticationRoleIdentifier.ORG_ADMIN, AuthenticationRoleIdentifier.CRM_ADMIN)).getUserId();

		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());

		JsonArray error1 = put(userId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(userId.toString(), error1.getObject(0).getString("identifier"));
		assertEquals("Error", error1.getObject(0).getString("type"));
		assertEquals("confirm", error1.getObject(0).getString("path"));
		assertEquals("Field is required", error1.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());

		JsonArray error2 = put(userId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(userId.toString(), error2.getObject(0).getString("identifier"));
		assertEquals("Error", error2.getObject(0).getString("type"));
		assertEquals("confirm", error2.getObject(0).getString("path"));
		assertEquals("Field is required", error2.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());

		JsonArray error3 = put(userId + "/disable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(userId.toString(), error3.getObject(0).getString("identifier"));
		assertEquals("Error", error3.getObject(0).getString("type"));
		assertEquals("confirm", error3.getObject(0).getString("path"));
		assertEquals("Format is invalid", error3.getObject(0).getString("reason"));
		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());

		JsonObject disable = put(userId + "/disable", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("confirm", true));
		//JsonAsserts.print(disable, "disable");
		assertEquals(List.of("userId", "organizationId", "username", "status"), disable.keys());
		assertEquals(userId.getCode(), disable.getString("userId"));
		assertEquals(testOrgId.getCode(), disable.getString("organizationId"));
		assertEquals("chloe", disable.getString("username"));
		assertEquals("Inactive", disable.getString("status"));
		
		JsonArray error4 = put(userId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, null);
		assertEquals(userId.toString(), error4.getObject(0).getString("identifier"));
		assertEquals("Error", error4.getObject(0).getString("type"));
		assertEquals("confirm", error4.getObject(0).getString("path"));
		assertEquals("Field is required", error4.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUserDetails(userId).getStatus());
		
		JsonArray error5 = put(userId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", false));
		assertEquals(userId.toString(), error5.getObject(0).getString("identifier"));
		assertEquals("Error", error5.getObject(0).getString("type"));
		assertEquals("confirm", error5.getObject(0).getString("path"));
		assertEquals("Field is required", error5.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUserDetails(userId).getStatus());
		
		JsonArray error6 = put(userId + "/enable", Lang.ENGLISH, HttpStatus.BAD_REQUEST, new JsonObject().with("confirm", "Test"));
		assertEquals(userId.toString(), error6.getObject(0).getString("identifier"));
		assertEquals("Error", error6.getObject(0).getString("type"));
		assertEquals("confirm", error6.getObject(0).getString("path"));
		assertEquals("Format is invalid", error6.getObject(0).getString("reason"));
		assertEquals(Status.INACTIVE, crm.findUserDetails(userId).getStatus());
	
		JsonObject enable = put(userId + "/enable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		//JsonAsserts.print(enable, "enable");
		assertEquals(List.of("userId", "organizationId", "username", "status"), disable.keys());
		assertEquals(userId.getCode(), enable.getString("userId"));
		assertEquals(testOrgId.getCode(), enable.getString("organizationId"));
		assertEquals("chloe", enable.getString("username"));
		assertEquals("Actif", enable.getString("status"));
		
		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());
		
		put("/user/chloe/disable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(Status.INACTIVE, crm.findUserDetails(userId).getStatus());

		put("/user/chloe/enable", Lang.FRENCH, HttpStatus.OK, new JsonObject().with("confirm", true));
		assertEquals(Status.ACTIVE, crm.findUserDetails(userId).getStatus());
	}
	
}
