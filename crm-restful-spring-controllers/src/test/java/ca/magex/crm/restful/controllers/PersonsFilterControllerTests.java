package ca.magex.crm.restful.controllers;

import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.DEVELOPER;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXECS_CEO;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXECS_CIO;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_OWNER;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.IMIT_DIRECTOR;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.SYS_ADMINISTRATOR;
import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.CA_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.CHLOE;
import static ca.magex.crm.test.CrmAsserts.DAN;
import static ca.magex.crm.test.CrmAsserts.DE_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ELAINE;
import static ca.magex.crm.test.CrmAsserts.EN_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.FRANCOIS;
import static ca.magex.crm.test.CrmAsserts.FR_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.HOME_COMMUNICATIONS;
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
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.json.model.JsonObject;

public class PersonsFilterControllerTests extends AbstractControllerTests {
	
	private OrganizationIdentifier org1;
	
	private OrganizationIdentifier org2;
	
	private PersonIdentifier adamId;
	
	private PersonIdentifier bobId;
	
	private PersonIdentifier chloeId;
	
	private PersonIdentifier danId;
	
	private PersonIdentifier elaineId;
	
	private PersonIdentifier francoisId;
	
	private OrganizationIdentifier systemOrgId;
	
	private PersonIdentifier systemPersonId;
	
	@Before
	public void setup() {
		initialize();

		systemOrgId = getSystemOrganizationIdentifier();
		systemPersonId = getSystemAdminIdentifier();

		org1 = createTestOrganization("Org 1");
		org2 = createTestOrganization("Org 2");
				
		adamId = crm.createPerson(org1, ADAM, CA_ADDRESS, HOME_COMMUNICATIONS, List.of(EXECS_CEO)).getPersonId();
		bobId = crm.disablePerson(crm.createPerson(org1, BOB, US_ADDRESS, HOME_COMMUNICATIONS, List.of(EXECS_CIO)).getPersonId()).getPersonId();
		chloeId = crm.createPerson(org1, CHLOE, MX_ADDRESS, WORK_COMMUNICATIONS, List.of(IMIT_DIRECTOR)).getPersonId();
		danId = crm.createPerson(org2, DAN, EN_ADDRESS, HOME_COMMUNICATIONS, List.of(SYS_ADMINISTRATOR)).getPersonId();
		elaineId = crm.disablePerson(crm.createPerson(org2, ELAINE, DE_ADDRESS, WORK_COMMUNICATIONS, List.of(DEVELOPER)).getPersonId()).getPersonId();
		francoisId = crm.createPerson(org2, FRANCOIS, FR_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId();
	}
	
	@Test
	public void testPersonsFilterDefaultRoot() throws Exception {
		JsonObject json = get("/persons");
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals("http://api.magex.ca/crm/schema/system/Page", json.getString("@context"));
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(0).getString("@context"));
		assertEquals(Crm.REST_BASE + systemPersonId.toString(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(Crm.REST_BASE + systemOrgId.toString(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(0).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(0).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", json.getArray("content").getObject(0).getObject("status").getString("@id"));
		assertEquals("ACTIVE", json.getArray("content").getObject(0).getObject("status").getString("@value"));
		assertEquals("Active", json.getArray("content").getObject(0).getObject("status").getString("@en"));
		assertEquals("Actif", json.getArray("content").getObject(0).getObject("status").getString("@fr"));
		assertEquals("Admin, System", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(1).getString("@context"));
		assertEquals(Crm.REST_BASE + adamId.toString(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(1).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(1).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", json.getArray("content").getObject(1).getObject("status").getString("@id"));
		assertEquals("ACTIVE", json.getArray("content").getObject(1).getObject("status").getString("@value"));
		assertEquals("Active", json.getArray("content").getObject(1).getObject("status").getString("@en"));
		assertEquals("Actif", json.getArray("content").getObject(1).getObject("status").getString("@fr"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(2).getString("@context"));
		assertEquals(Crm.REST_BASE + chloeId.toString(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(2).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(2).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", json.getArray("content").getObject(2).getObject("status").getString("@id"));
		assertEquals("ACTIVE", json.getArray("content").getObject(2).getObject("status").getString("@value"));
		assertEquals("Active", json.getArray("content").getObject(2).getObject("status").getString("@en"));
		assertEquals("Actif", json.getArray("content").getObject(2).getObject("status").getString("@fr"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(3).getString("@context"));
		assertEquals(Crm.REST_BASE + francoisId.toString(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(Crm.REST_BASE + org2.toString(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(3).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(3).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", json.getArray("content").getObject(3).getObject("status").getString("@id"));
		assertEquals("ACTIVE", json.getArray("content").getObject(3).getObject("status").getString("@value"));
		assertEquals("Active", json.getArray("content").getObject(3).getObject("status").getString("@en"));
		assertEquals("Actif", json.getArray("content").getObject(3).getObject("status").getString("@fr"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(4).getString("@context"));
		assertEquals(Crm.REST_BASE + elaineId.toString(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(Crm.REST_BASE + org2.toString(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(4).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(4).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/inactive", json.getArray("content").getObject(4).getObject("status").getString("@id"));
		assertEquals("INACTIVE", json.getArray("content").getObject(4).getObject("status").getString("@value"));
		assertEquals("Inactive", json.getArray("content").getObject(4).getObject("status").getString("@en"));
		assertEquals("Inactif", json.getArray("content").getObject(4).getObject("status").getString("@fr"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(5).getString("@context"));
		assertEquals(Crm.REST_BASE + danId.toString(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(Crm.REST_BASE + org2.toString(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(5).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(5).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", json.getArray("content").getObject(5).getObject("status").getString("@id"));
		assertEquals("ACTIVE", json.getArray("content").getObject(5).getObject("status").getString("@value"));
		assertEquals("Active", json.getArray("content").getObject(5).getObject("status").getString("@en"));
		assertEquals("Actif", json.getArray("content").getObject(5).getObject("status").getString("@fr"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonSummary", json.getArray("content").getObject(6).getString("@context"));
		assertEquals(Crm.REST_BASE + bobId.toString(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(Crm.REST_BASE + org1.toString(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getArray("content").getObject(6).getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", json.getArray("content").getObject(6).getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/inactive", json.getArray("content").getObject(6).getObject("status").getString("@id"));
		assertEquals("INACTIVE", json.getArray("content").getObject(6).getObject("status").getString("@value"));
		assertEquals("Inactive", json.getArray("content").getObject(6).getObject("status").getString("@en"));
		assertEquals("Inactif", json.getArray("content").getObject(6).getObject("status").getString("@fr"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}
	
	@Test
	public void testPersonsFilterDefaultEnglish() throws Exception {
		JsonObject json = get("/persons", Lang.ENGLISH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(systemPersonId.getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(systemOrgId.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Admin, System", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(adamId.getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(4).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(5).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(6).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}
	
	@Test
	public void testPersonsFilterDefaultFrench() throws Exception {
		JsonObject json = get("/persons", Lang.FRENCH, HttpStatus.OK);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(systemPersonId.getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(systemOrgId.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Admin, System", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(adamId.getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("Anderson, Adam A", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(2).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(2).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(3).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(3).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(3).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(4).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(4).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(4).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(5).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals("Actif", json.getArray("content").getObject(5).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(5).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(6).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(6).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(6).getString("displayName"));
	}

	@Test
	public void testFilterByDisplayNameCaseAccentInsensitive() throws Exception {
		JsonObject json = get("/persons", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("displayName", "C"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("LaRue, Chloé", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(1).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByInactifDesc() throws Exception {
		JsonObject json = get("/persons", Lang.FRENCH, HttpStatus.OK, new JsonObject()
			.with("status", "Inactif")
			.with("order", "displayName")
			.with("direction", "desc"));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org1.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Robert, Bob K", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactif", json.getArray("content").getObject(1).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getString("displayName"));
	}
	
	@Test
	public void testFilterByOrganization() throws Exception {
		JsonObject json = get("/persons", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("organization", org2.toString()));
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(0).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals("Mátyás, François", json.getArray("content").getObject(0).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(1).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals("Inactive", json.getArray("content").getObject(1).getString("status"));
		assertEquals("McKay, Elaine M", json.getArray("content").getObject(1).getString("displayName"));
		assertEquals(List.of("personId", "organizationId", "status", "displayName"), json.getArray("content").getObject(2).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals(org2.getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals("O'Sullivan, Daniel D", json.getArray("content").getObject(2).getString("displayName"));
	}

}
