package ca.magex.crm.restful.controllers;

import static ca.magex.crm.api.system.id.AuthenticationRoleIdentifier.CRM_ADMIN;
import static ca.magex.crm.api.system.id.AuthenticationRoleIdentifier.CRM_USER;
import static ca.magex.crm.api.system.id.AuthenticationRoleIdentifier.ORG_ADMIN;
import static ca.magex.crm.api.system.id.AuthenticationRoleIdentifier.ORG_USER;
import static ca.magex.crm.api.system.id.AuthenticationRoleIdentifier.SYS_ADMIN;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.DEVELOPER;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXECS_CEO;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_CONTACT;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_EMPLOYEE;
import static ca.magex.crm.api.system.id.BusinessRoleIdentifier.EXTERNAL_OWNER;
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

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.json.model.JsonObject;

public class UsersFilterControllerTests extends AbstractControllerTests {
	
	private OrganizationIdentifier org1;
	
	private OrganizationIdentifier org2;
	
	private UserIdentifier adamId;
	
	private UserIdentifier bobId;
	
	private UserIdentifier chloeId;
	
	private UserIdentifier danId;
	
	private UserIdentifier elaineId;
	
	private UserIdentifier francoisId;
	
	private UserIdentifier systemUserId;
	
	@Before
	public void setup() {
		initialize();

		systemUserId = getSystemUserIdentifier();

		org1 = createTestOrganization("Org 1");
		org2 = createTestOrganization("Org 2");
		
		adamId = crm.createUser(crm.createPerson(org1, ADAM, CA_ADDRESS, HOME_COMMUNICATIONS, List.of(SYS_ADMINISTRATOR)).getPersonId(), "adam", List.of(ORG_ADMIN, CRM_ADMIN)).getUserId();
		bobId = crm.disableUser(crm.createUser(crm.createPerson(org1, BOB, US_ADDRESS, HOME_COMMUNICATIONS, List.of(DEVELOPER)).getPersonId(), "bob", List.of(ORG_USER)).getUserId()).getUserId();
		chloeId = crm.createUser(crm.createPerson(org1, CHLOE, MX_ADDRESS, WORK_COMMUNICATIONS, List.of(EXECS_CEO)).getPersonId(), "chloe", List.of(CRM_USER)).getUserId();
		danId = crm.createUser(crm.createPerson(org2, DAN, EN_ADDRESS, HOME_COMMUNICATIONS, List.of(EXTERNAL_OWNER)).getPersonId(), "dan", List.of(CRM_USER)).getUserId();
		elaineId = crm.createUser(crm.createPerson(org2, ELAINE, DE_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_EMPLOYEE)).getPersonId(), "elaine", List.of(SYS_ADMIN)).getUserId();
		crm.disablePerson(crm.findUserDetails(elaineId).getPersonId());
		francoisId = crm.createUser(crm.createPerson(org2, FRANCOIS, FR_ADDRESS, WORK_COMMUNICATIONS, List.of(EXTERNAL_CONTACT)).getPersonId(), "francois", List.of(ORG_ADMIN, CRM_USER)).getUserId();
	}
	
	@Test
	public void testOrganizationFilterDefaultRoot() throws Exception {
		JsonObject json = get("/users", Lang.ROOT, HttpStatus.OK);

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(adamId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(adamId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(adamId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("ORG/ADMIN", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM/ADMIN", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(systemUserId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(systemUserId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(systemUserId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("admin", json.getArray("content").getObject(1).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(1).getString("status"));
		assertEquals(4, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("SYS/ADMIN", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals("SYS/ACTUATOR", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(1));
		assertEquals("SYS/ACCESS", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(2));
		assertEquals("CRM/ADMIN", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(3));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(2).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals(crm.findUserDetails(bobId).getOrganizationId().getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(crm.findUserDetails(bobId).getPersonId().getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals("bob", json.getArray("content").getObject(2).getString("username"));
		assertEquals("INACTIVE", json.getArray("content").getObject(2).getString("status"));
		assertEquals(1, json.getArray("content").getObject(2).getArray("authenticationRoleIds").size());
		assertEquals("ORG/USER", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(3).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(3).getString("userId"));
		assertEquals(crm.findUserDetails(chloeId).getOrganizationId().getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals(crm.findUserDetails(chloeId).getPersonId().getCode(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals("chloe", json.getArray("content").getObject(3).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(3).getString("status"));
		assertEquals(1, json.getArray("content").getObject(3).getArray("authenticationRoleIds").size());
		assertEquals("CRM/USER", json.getArray("content").getObject(3).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(4).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(4).getString("userId"));
		assertEquals(crm.findUserDetails(danId).getOrganizationId().getCode(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals(crm.findUserDetails(danId).getPersonId().getCode(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals("dan", json.getArray("content").getObject(4).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(4).getString("status"));
		assertEquals(1, json.getArray("content").getObject(4).getArray("authenticationRoleIds").size());
		assertEquals("CRM/USER", json.getArray("content").getObject(4).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(5).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(5).getString("userId"));
		assertEquals(crm.findUserDetails(elaineId).getOrganizationId().getCode(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals(crm.findUserDetails(elaineId).getPersonId().getCode(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals("elaine", json.getArray("content").getObject(5).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(5).getString("status"));
		assertEquals(1, json.getArray("content").getObject(5).getArray("authenticationRoleIds").size());
		assertEquals("SYS/ADMIN", json.getArray("content").getObject(5).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(6).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(6).getString("userId"));
		assertEquals(crm.findUserDetails(francoisId).getOrganizationId().getCode(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals(crm.findUserDetails(francoisId).getPersonId().getCode(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(6).getString("username"));
		assertEquals("ACTIVE", json.getArray("content").getObject(6).getString("status"));
		assertEquals(2, json.getArray("content").getObject(6).getArray("authenticationRoleIds").size());
		assertEquals("ORG/ADMIN", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM/USER", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testOrganizationFilterDefaultEnglish() throws Exception {
		JsonObject json = get("/users", Lang.ENGLISH, HttpStatus.OK);

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(adamId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(adamId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(adamId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Admin", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(systemUserId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(systemUserId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(systemUserId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("admin", json.getArray("content").getObject(1).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(4, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals("System Actuator", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(1));
		assertEquals("System Access", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(2));
		assertEquals("CRM Admin", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(3));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(2).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals(crm.findUserDetails(bobId).getOrganizationId().getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(crm.findUserDetails(bobId).getPersonId().getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals("bob", json.getArray("content").getObject(2).getString("username"));
		assertEquals("Inactive", json.getArray("content").getObject(2).getString("status"));
		assertEquals(1, json.getArray("content").getObject(2).getArray("authenticationRoleIds").size());
		assertEquals("Organization Viewer", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(3).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(3).getString("userId"));
		assertEquals(crm.findUserDetails(chloeId).getOrganizationId().getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals(crm.findUserDetails(chloeId).getPersonId().getCode(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals("chloe", json.getArray("content").getObject(3).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(3).getString("status"));
		assertEquals(1, json.getArray("content").getObject(3).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(3).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(4).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(4).getString("userId"));
		assertEquals(crm.findUserDetails(danId).getOrganizationId().getCode(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals(crm.findUserDetails(danId).getPersonId().getCode(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals("dan", json.getArray("content").getObject(4).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(4).getString("status"));
		assertEquals(1, json.getArray("content").getObject(4).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(4).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(5).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(5).getString("userId"));
		assertEquals(crm.findUserDetails(elaineId).getOrganizationId().getCode(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals(crm.findUserDetails(elaineId).getPersonId().getCode(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals("elaine", json.getArray("content").getObject(5).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(5).getString("status"));
		assertEquals(1, json.getArray("content").getObject(5).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", json.getArray("content").getObject(5).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(6).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(6).getString("userId"));
		assertEquals(crm.findUserDetails(francoisId).getOrganizationId().getCode(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals(crm.findUserDetails(francoisId).getPersonId().getCode(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(6).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(6).getString("status"));
		assertEquals(2, json.getArray("content").getObject(6).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Viewer", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testOrganizationFilterDefaultFrench() throws Exception {
		JsonObject json = get("/users", Lang.FRENCH, HttpStatus.OK);

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(7, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(7, json.getArray("content").size());
		assertEquals(adamId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(adamId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(adamId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("adam", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("Administrateur de l'organisation", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("Administrateur GRC", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(systemUserId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(systemUserId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(systemUserId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("admin", json.getArray("content").getObject(1).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(1).getString("status"));
		assertEquals(4, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("Adminstrator du système", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals("Actuator du système", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(1));
		assertEquals("Access du système", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(2));
		assertEquals("Administrateur GRC", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(3));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(2).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals(crm.findUserDetails(bobId).getOrganizationId().getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(crm.findUserDetails(bobId).getPersonId().getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals("bob", json.getArray("content").getObject(2).getString("username"));
		assertEquals("Inactif", json.getArray("content").getObject(2).getString("status"));
		assertEquals(1, json.getArray("content").getObject(2).getArray("authenticationRoleIds").size());
		assertEquals("Visionneuse d'organisation", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(3).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(3).getString("userId"));
		assertEquals(crm.findUserDetails(chloeId).getOrganizationId().getCode(), json.getArray("content").getObject(3).getString("organizationId"));
		assertEquals(crm.findUserDetails(chloeId).getPersonId().getCode(), json.getArray("content").getObject(3).getString("personId"));
		assertEquals("chloe", json.getArray("content").getObject(3).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(3).getString("status"));
		assertEquals(1, json.getArray("content").getObject(3).getArray("authenticationRoleIds").size());
		assertEquals("Visionneuse GRC", json.getArray("content").getObject(3).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(4).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(4).getString("userId"));
		assertEquals(crm.findUserDetails(danId).getOrganizationId().getCode(), json.getArray("content").getObject(4).getString("organizationId"));
		assertEquals(crm.findUserDetails(danId).getPersonId().getCode(), json.getArray("content").getObject(4).getString("personId"));
		assertEquals("dan", json.getArray("content").getObject(4).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(4).getString("status"));
		assertEquals(1, json.getArray("content").getObject(4).getArray("authenticationRoleIds").size());
		assertEquals("Visionneuse GRC", json.getArray("content").getObject(4).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(5).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(5).getString("userId"));
		assertEquals(crm.findUserDetails(elaineId).getOrganizationId().getCode(), json.getArray("content").getObject(5).getString("organizationId"));
		assertEquals(crm.findUserDetails(elaineId).getPersonId().getCode(), json.getArray("content").getObject(5).getString("personId"));
		assertEquals("elaine", json.getArray("content").getObject(5).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(5).getString("status"));
		assertEquals(1, json.getArray("content").getObject(5).getArray("authenticationRoleIds").size());
		assertEquals("Adminstrator du système", json.getArray("content").getObject(5).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(6).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(6).getString("userId"));
		assertEquals(crm.findUserDetails(francoisId).getOrganizationId().getCode(), json.getArray("content").getObject(6).getString("organizationId"));
		assertEquals(crm.findUserDetails(francoisId).getPersonId().getCode(), json.getArray("content").getObject(6).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(6).getString("username"));
		assertEquals("Actif", json.getArray("content").getObject(6).getString("status"));
		assertEquals(2, json.getArray("content").getObject(6).getArray("authenticationRoleIds").size());
		assertEquals("Administrateur de l'organisation", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(0));
		assertEquals("Visionneuse GRC", json.getArray("content").getObject(6).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testFilterByUsername() throws Exception {
		JsonObject json = get("/users", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("username", "e"));
		
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(2, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(2, json.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(0).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(chloeId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(chloeId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("chloe", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(elaineId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(elaineId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("elaine", json.getArray("content").getObject(1).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
	}
	
	@Test
	public void testFilterByRole() throws Exception {
		JsonObject json = get("/users", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("authenticationRoleId", "CRM Viewer"));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(0).keys());
		assertEquals(chloeId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(chloeId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(chloeId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("chloe", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(danId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(danId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("dan", json.getArray("content").getObject(1).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(2).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals(crm.findUserDetails(francoisId).getOrganizationId().getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(crm.findUserDetails(francoisId).getPersonId().getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(2).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals(2, json.getArray("content").getObject(2).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Viewer", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testFilterByPersonId() throws Exception {
		UserDetails user = crm.findUserDetails(francoisId);
		JsonObject json = get("/users", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("personId", user.getPersonId().toString()));
				
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(0).keys());
		assertEquals(user.getUserId().getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(user.getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(user.getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(2, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Viewer", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testFilterByOrgId() throws Exception {
		JsonObject json = get("/users", Lang.ENGLISH, HttpStatus.OK, new JsonObject().with("organizationId", org2.toString()));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(3, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(3, json.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(0).keys());
		assertEquals(danId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(danId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(danId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("dan", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(0).getString("status"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("CRM Viewer", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(1).keys());
		assertEquals(elaineId.getCode(), json.getArray("content").getObject(1).getString("userId"));
		assertEquals(crm.findUserDetails(elaineId).getOrganizationId().getCode(), json.getArray("content").getObject(1).getString("organizationId"));
		assertEquals(crm.findUserDetails(elaineId).getPersonId().getCode(), json.getArray("content").getObject(1).getString("personId"));
		assertEquals("elaine", json.getArray("content").getObject(1).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(1).getString("status"));
		assertEquals(1, json.getArray("content").getObject(1).getArray("authenticationRoleIds").size());
		assertEquals("System Administrator", json.getArray("content").getObject(1).getArray("authenticationRoleIds").getString(0));
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(2).keys());
		assertEquals(francoisId.getCode(), json.getArray("content").getObject(2).getString("userId"));
		assertEquals(crm.findUserDetails(francoisId).getOrganizationId().getCode(), json.getArray("content").getObject(2).getString("organizationId"));
		assertEquals(crm.findUserDetails(francoisId).getPersonId().getCode(), json.getArray("content").getObject(2).getString("personId"));
		assertEquals("francois", json.getArray("content").getObject(2).getString("username"));
		assertEquals("Active", json.getArray("content").getObject(2).getString("status"));
		assertEquals(2, json.getArray("content").getObject(2).getArray("authenticationRoleIds").size());
		assertEquals("Organization Admin", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(0));
		assertEquals("CRM Viewer", json.getArray("content").getObject(2).getArray("authenticationRoleIds").getString(1));
	}
	
	@Test
	public void testFilterByInactifDesc() throws Exception {
		JsonObject json = get("/users", Lang.FRENCH, HttpStatus.OK, new JsonObject()
			.with("status", "Inactif")
			.with("order", "displayName")
			.with("direction", "desc"));

		//JsonAsserts.print(json, "json");
		assertEquals(List.of("page", "limit", "total", "hasNext", "hasPrevious", "content"), json.keys());
		assertEquals(1, json.getNumber("page"));
		assertEquals(10, json.getNumber("limit"));
		assertEquals(1, json.getNumber("total"));
		assertEquals(false, json.getBoolean("hasNext"));
		assertEquals(false, json.getBoolean("hasPrevious"));
		assertEquals(1, json.getArray("content").size());
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "authenticationRoleIds"), json.getArray("content").getObject(0).keys());
		assertEquals(bobId.getCode(), json.getArray("content").getObject(0).getString("userId"));
		assertEquals(crm.findUserDetails(bobId).getOrganizationId().getCode(), json.getArray("content").getObject(0).getString("organizationId"));
		assertEquals(crm.findUserDetails(bobId).getPersonId().getCode(), json.getArray("content").getObject(0).getString("personId"));
		assertEquals("bob", json.getArray("content").getObject(0).getString("username"));
		assertEquals("Inactif", json.getArray("content").getObject(0).getString("status"));
		assertEquals(1, json.getArray("content").getObject(0).getArray("authenticationRoleIds").size());
		assertEquals("Visionneuse d'organisation", json.getArray("content").getObject(0).getArray("authenticationRoleIds").getString(0));
	}
	
}
