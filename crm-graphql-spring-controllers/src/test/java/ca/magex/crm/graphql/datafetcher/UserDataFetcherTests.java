package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;

public class UserDataFetcherTests extends AbstractDataFetcherTests {

	private String orgId;
	private String personId;
	
	@Before
	public void createGroup() throws Exception {
		JSONObject group = execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId } }",
				"DEV",
				"developers",
				"developeurs");

		execute(
				"createRole",
				"mutation { createRole(groupId: %s, code: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				group.getString("groupId"),
				"ADM",
				"administrator",
				"administrateur");
		
		execute(
				"createRole",
				"mutation { createRole(groupId: %s, code: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				group.getString("groupId"),
				"USR",
				"user",
				"utilisateur");
		
		JSONObject org = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s, groups: %s) { organizationId } }",
				"Johnnuy",
				List.of("DEV"));
		orgId = org.getString("organizationId");
		
		JSONObject person = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, name: { "
				+ "firstName: %s, middleName: %s, lastName: %s, salutation: %s}, address: { "
				+ "street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }, communication: {"
				+ "jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, position: {"
				+ "sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				orgId,
				"Jonny", "Michael", "Bigford", "Mr",
				"99 Blue Jays Way", "Toronto", "Ontario", "Canada", "L9K5I9",
				"Developer", "English", "jonny.bigford@johnnuy.org", "6135551212", "97", "6135551213",
				"IT", "Solutions", "Senior Developer");
		personId = person.getString("personId");
	}
	
	@Test
	public void userDataFetching() throws Exception {
		JSONObject user = execute(
				"createUser",
				"mutation { createUser(personId: %s, username: %s, roles: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				personId,
				"jbigford",
				Arrays.asList("ADM", "USR"));
		Identifier userId = new Identifier(user.getString("userId"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		
		/* activate active user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				"active");
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* inactivate active user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				"inactive");
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("INACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
	
		/* inactivate inactive user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				"inactive");
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("INACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* activate inactive user */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, status: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				"active");
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* pass invalid status */
		try {
			execute(
					"updateUser",
					"mutation { updateUser(userId: %s, status: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
					userId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updateUser - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update roles - no change */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, roles: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				Arrays.asList("ADM", "USR"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* update roles - remove role */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, roles: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				Arrays.asList("ADM"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(1, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));		
		
		/* update roles - change role */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, roles: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				Arrays.asList("USR"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(1, user.getJSONArray("roles").length());
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		
		/* update roles - reset roles */
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, roles: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId,
				Arrays.asList("ADM", "USR"));
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* find user by id */
		user = execute(
				"findUser",
				"{ findUser(userId: %s) { userId username status roles { englishName } person { displayName communication { email } } } }",
				userId);
		Assert.assertEquals("jbigford", user.getString("username"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		Assert.assertEquals(2, user.getJSONArray("roles").length());
		Assert.assertEquals("administrator", user.getJSONArray("roles").getJSONObject(0).getString("englishName"));
		Assert.assertEquals("user", user.getJSONArray("roles").getJSONObject(1).getString("englishName"));
		
		/* count users */
		int userCount = execute(
				"countUsers",
				"{ countUsers(filter: { status: %s } ) }",
				"active");
		Assert.assertEquals(1, userCount);
		
		userCount = execute(
				"countUsers",
				"{ countUsers(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, userCount);
		
		/* find users paging */
		JSONObject users = execute(
				"findUsers",
				"{ findUsers(filter: {organizationId: %s, personId: %s, status: %s, role: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { userId status username } } }",
				orgId,
				personId,				
				"active",
				"USR",
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
