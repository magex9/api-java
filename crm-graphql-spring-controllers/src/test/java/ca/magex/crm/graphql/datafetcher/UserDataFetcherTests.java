package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.graphql.util.MapBuilder;

public class UserDataFetcherTests extends AbstractDataFetcherTests {

	private OrganizationIdentifier orgId;
	private PersonIdentifier managerId;
	
	@Before
	public void setup() throws Exception {
		JSONObject johnnuy = executeWithVariables(
				"createOrganization",
				"mutation ($displayName: String!, $authenticationGroups: [String]!, $businessGroups: [String]!) { " + 
						"createOrganization(displayName: $displayName, authenticationGroups: $authenticationGroups, businessGroups: $businessGroups) { " + 
							"organizationId } }",
				new MapBuilder()
					.withEntry("displayName", "Johnnuy")
					.withEntry("authenticationGroups", List.of("SYS", "CRM"))
					.withEntry("businessGroups", List.of("IMIT", "IMIT/DEV")).build()
				);
		orgId = new OrganizationIdentifier(johnnuy.getString("organizationId"));
		
		JSONObject manager = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, " + 
						"name: {firstName: %s, middleName: %s, lastName: %s, salutation: {code: %s} }, " + 
						"address: {street: %s, city: %s, province: {code: %s}, country: {code: %s}, postalCode: %s }, " + 
						"communication: {jobTitle: %s, language: {code: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, " +
						"businessRoles: %s ) " + 
						"{ personId } }",
				orgId,
				"Jonny", "Michael", "Bigford", "MR",
				"99 Blue Jays Way", "Toronto", "CA/ON", "CA", "L9K5I9",
				"Developer", "EN", "jonny.bigford@johnnuy.org", "6135551212", "97", "613-555-1213",
				List.of("IMIT/DEV/MANAGER"));
		managerId = new PersonIdentifier(manager.getString("personId"));
	}
	
	@Test
	public void userDataFetching() throws Exception {
		JSONObject user = execute(
				"createUser",
				"mutation { createUser(personId: %s, username: %s, authenticationRoles: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				managerId,
				"jbigford",
				Arrays.asList("SYS/ADMIN", "CRM/ADMIN"));
		UserIdentifier userId = new UserIdentifier(user.getString("userId"));
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		
		/* activate active user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				"active");
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* inactivate active user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				"inactive");
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("INACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
	
		/* inactivate inactive user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				"inactive");
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("INACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* activate inactive user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				"active");
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* pass invalid status */
		try {
			execute(
					"updateUser",
					"mutation { updateUser(userId: %s, status: %s) { " + 
							"userId } }",
					userId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updateUser - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update roles - no change */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, authenticationRoles: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				Arrays.asList("SYS/ADMIN", "CRM/ADMIN"));
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* update roles - remove role */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, authenticationRoles: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				Arrays.asList("SYS/ADMIN"));
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(1, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));		
		
		/* update roles - change role */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, authenticationRoles: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				Arrays.asList("CRM/ADMIN"));
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(1, user.getJSONArray("authenticationRoles").length());		
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update roles - reset roles */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, authenticationRoles: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId,
				Arrays.asList("SYS/ADMIN", "CRM/ADMIN"));
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* find user by id */
		user = execute(
				"findUser",
				"{ findUser(userId: %s) { " + 
						"userId username status person { displayName communication { email } } organization { organizationId } authenticationRoles { name { code english french } } } }",
				userId);
		Assert.assertEquals(orgId.toString(), user.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("authenticationRoles").length());
		Assert.assertEquals("SYS/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System Administrator", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Adminstrator du système", user.getJSONArray("authenticationRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("CRM/ADMIN", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("CRM Admin", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Administrateur GRC", user.getJSONArray("authenticationRoles").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* count users */
		int userCount = execute(
				"countUsers",
				"{ countUsers(filter: { status: %s } ) }",
				"active");
		Assert.assertEquals(2, userCount);
		
		userCount = execute(
				"countUsers",
				"{ countUsers(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, userCount);
		
		/* find users paging */
		JSONObject users = execute(
				"findUsers",
				"{ findUsers(filter: {organizationId: %s, personId: %s, status: %s, authenticationRoleId: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { userId status username } } }",
				orgId,
				managerId,				
				"active",
				"SYS/ADMIN",
				1,
				5,
				"englishName",
				"DESC");
		Assert.assertEquals(1, users.getInt("number"));
		Assert.assertEquals(1, users.getInt("numberOfElements"));
		Assert.assertEquals(5, users.getInt("size"));
		Assert.assertEquals(1, users.getInt("totalPages"));
		Assert.assertEquals(1, users.getInt("totalElements"));
		JSONArray userContents = users.getJSONArray("content");
		Assert.assertEquals(userId.toString(), userContents.getJSONObject(0).get("userId"));
		Assert.assertEquals("jbigford", userContents.getJSONObject(0).get("username"));
		Assert.assertEquals("ACTIVE", userContents.getJSONObject(0).get("status"));
		
		/* reset user password */
		String tempPassword = execute(
				"resetUserPassword",
				"mutation { resetUserPassword(userId: %s) }",
				userId);
		Assert.assertNotNull(tempPassword);
		
		/* change user password from temporary password */		
		Boolean success = execute(
				"changeUserPassword",
				"mutation { changeUserPassword(userId: %s, currentPassword: %s, newPassword: %s) }",
				userId,
				tempPassword,
				"JungleGym2020!");
		Assert.assertTrue(success);
		
		/* change user password with wrong current password */
		success = execute(
				"changeUserPassword",
				"mutation { changeUserPassword(userId: %s, currentPassword: %s, newPassword: %s) }",
				userId,
				tempPassword,
				"JungleGym2020!");
		Assert.assertFalse(success);
	}
}
