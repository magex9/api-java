package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.TestConfig;
import ca.magex.crm.graphql.service.GraphQLCrmServices;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		TestConfig.class
})
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public class PermissionsDataFetcherTests {

	private Logger log = LoggerFactory.getLogger(getClass());
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired private GraphQLCrmServices graphQl;
	@Autowired private AmnesiaDB amnesiaDb;
	
	@Before
	public void before() {
		amnesiaDb.reset();
	}
	
	@SuppressWarnings("unchecked")
	private <T> T execute(String queryName, String query, Object... args) throws Exception {
		String formattedQuery = String.format(query, Arrays.asList(args).stream()				
				.map((arg) -> (arg instanceof List) ? StringUtils.join((List<String>) arg) : arg)				
				.map((arg) -> (arg instanceof String || arg instanceof Identifier) ? ("\"" + arg + "\"") : arg)
				.map((arg) -> arg == null ? "" : arg)
				.collect(Collectors.toList()).toArray());
		ExecutionResult result = graphQl.getGraphQL().execute(formattedQuery);
		if (result.getErrors().size() > 0) {
			String messages = result.getErrors().stream().map((e) -> e.getMessage()).collect(Collectors.joining());
			throw new ApiException("Errors encountered during " + queryName + " - " + messages);
		}
		Assert.assertEquals(StringUtils.join(result.getErrors(), '\n'), 0, result.getErrors().size());
		String resultAsJsonString = objectMapper.writeValueAsString(result.getData());
		log.info(formattedQuery + " --> " + resultAsJsonString);
		JSONObject json = new JSONObject(resultAsJsonString);
		Object o = json.get(queryName);
		if (o == JSONObject.NULL) {
			return null;
		}
		return (T) o;
	}
	
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
				"{ findGroups(filter: {status: %s}, paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }",
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
		
		/* find the group by paging */
		developers = execute(
				"findRoles",
				"{ findRoles(paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }",
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
}
