package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.buf.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
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
		Assert.assertEquals(result.getErrors().toString(), 0, result.getErrors().size());
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
	public void permissionsDataFetching() throws Exception {
		/* create new group */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(englishName: %s, frenchName: %s) { groupId status englishName frenchName } }",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));
	}
}
