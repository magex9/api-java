package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.service.GraphQLCrmServices;
import ca.magex.crm.test.TestConfig;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		TestConfig.class
})
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public abstract class AbstractDataFetcherTests {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	protected ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired private GraphQLCrmServices graphQl;	
	@Autowired private CrmInitializationService initializationService;
	
	@Before
	public void before() {
		initializationService.reset();
	}

	@SuppressWarnings("unchecked")
	protected <T> T execute(String queryName, String query, Object... args) throws Exception {
		String formattedQuery = String.format(query, Arrays.asList(args).stream()				
				.map((arg) -> (arg instanceof String || arg instanceof Identifier) ? ("\"" + arg + "\"") : arg)
				.map((arg) -> (arg instanceof List) ? 
						"[" + ((List<Object>) arg).stream().map((obj) -> (obj instanceof Number ? obj.toString() : "\"" + obj.toString() + "\"")).collect(Collectors.joining(",")) + "]" 
						: arg)
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
}
