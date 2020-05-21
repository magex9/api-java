package ca.magex.crm.graphql.datafetcher;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;

public class PermissionDataFetcherTests extends AbstractDataFetcherTests {

	@Test
	public void groupsDataFetching() throws Exception {
		/* create new group */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				"DEV",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* activate already active group */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId code englishName frenchName status } }",
				devId,
				"active");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* inactivate group */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId code englishName frenchName status } }",
				devId,
				"inactive");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("INACTIVE", developers.get("status"));
		
		/* inactivate already inactive group */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId code englishName frenchName status } }",
				devId,
				"inactive");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("INACTIVE", developers.get("status"));
		
		/* activate group */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId code englishName frenchName status } }",
				devId,
				"active");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* pass invalid status */
		try {
			execute(
					"updateGroup",
					"mutation { updateGroup(groupId: %s, status: %s) { groupId code englishName frenchName status } }",
					devId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		}
		catch(ApiException api) {
			Assert.assertEquals("Errors encountered during updateGroup - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
						
		/* update english name with no change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, englishName: %s) { groupId code englishName frenchName status } }",
				devId,
				"developers");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* update english name with change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, englishName: %s) { groupId code englishName frenchName status } }",
				devId,
				"devs");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("devs", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* update french name with no change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				devId,
				"developeurs");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("devs", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* update french name with change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				devId,
				"dévs");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("devs", developers.get("englishName"));
		Assert.assertEquals("dévs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* reset both names */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				devId,
				"developers",
				"developeurs");
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* find the group by id */
		developers = execute(
				"findGroup",
				"{ findGroup(groupId: %s) { groupId code englishName frenchName status } }",
				devId);
		Assert.assertEquals("DEV", developers.get("code"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
		
		/* find the group by page */
		developers = execute(
				"findGroups",
				"{ findGroups(filter: {code: %s, englishName: %s, frenchName: %s, status: %s}, paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }",
				"DEV",
				"developers",
				"developeurs",
				"active",
				1,
				5,
				"englishName",
				"ASC");
		Assert.assertEquals(1, developers.getInt("number"));
		Assert.assertEquals(1, developers.getInt("numberOfElements"));
		Assert.assertEquals(5, developers.getInt("size"));
		Assert.assertEquals(1, developers.getInt("totalPages"));
		Assert.assertEquals(1, developers.getInt("totalElements"));
		JSONArray devContents = developers.getJSONArray("content");
		Assert.assertEquals("developers", devContents.getJSONObject(0).get("englishName"));
		Assert.assertEquals("developeurs", devContents.getJSONObject(0).get("frenchName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
	
	@Test
	public void rolesDataFetching() throws Exception {
		/* create an initial group to add roles to */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				"DEV",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));

		JSONObject admin = execute(
				"createRole",
				"mutation { createRole(groupId: %s, code: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				devId,
				"ADM",
				"administrator",
				"administrateur");
		Identifier adminId = new Identifier(admin.getString("roleId"));
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));

		/* activate already active role */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"active");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* inactivate active role */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"inactive");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("INACTIVE", admin.get("status"));
		
		/* inactivate already inactive role */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"inactive");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("INACTIVE", admin.get("status"));
		
		/* activate inactive role */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"active");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* pass invalid status */
		try {
			execute(
					"updateRole",
					"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
					adminId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		}
		catch(ApiException api) {
			Assert.assertEquals("Errors encountered during updateRole - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update english name no change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, englishName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"administrator");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* update english name with change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, englishName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"admin");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("admin", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* update french name no change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"administrateur");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("admin", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* update french name with change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"admine");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("admin", admin.get("englishName"));
		Assert.assertEquals("admine", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* reset both names */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"administrator",
				"administrateur");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* find role by id */
		admin = execute(
				"findRole",
				"{ findRole(roleId: %s) { roleId code groupId status englishName frenchName } }",
				adminId);		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		/* find the group by paging */
		developers = execute(
				"findRoles",
				"{ findRoles(filter: {groupId: %s, code: %s, englishName: %s, frenchName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }",
				devId,
				"ADM",
				"administrator",
				"administrateur",
				"active",
				1,
				5,
				"englishName",
				"ASC");
		Assert.assertEquals(1, developers.getInt("number"));
		Assert.assertEquals(1, developers.getInt("numberOfElements"));
		Assert.assertEquals(5, developers.getInt("size"));
		Assert.assertEquals(1, developers.getInt("totalPages"));
		Assert.assertEquals(1, developers.getInt("totalElements"));
		JSONArray devContents = developers.getJSONArray("content");
		Assert.assertEquals("administrator", devContents.getJSONObject(0).get("englishName"));
		Assert.assertEquals("administrateur", devContents.getJSONObject(0).get("frenchName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
