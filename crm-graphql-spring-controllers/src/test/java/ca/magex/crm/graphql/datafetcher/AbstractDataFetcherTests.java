package ca.magex.crm.graphql.datafetcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.GraphQLTestConfig;
import ca.magex.crm.graphql.service.GraphQLCrmServices;
import ca.magex.crm.test.config.UnauthenticatedTestConfig;
import graphql.ExecutionInput;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UnauthenticatedTestConfig.class, GraphQLTestConfig.class })
public abstract class AbstractDataFetcherTests {

	protected Logger log = LoggerFactory.getLogger(getClass());
	protected ObjectMapper objectMapper = new ObjectMapper();

	@Autowired private GraphQLCrmServices graphQl;
	@Autowired private CrmConfigurationService config;

	@Before
	public void before() {
		config.reset();
		config.initializeSystem("johnnuy", new PersonName(null, "Jonny", "Alex", "Thomson"), "jonny@johnnuy.org", "admin", "admin");
	}

	@SuppressWarnings("unchecked")
	protected <T> T execute(String queryName, String query, Object... args) throws Exception {
		String formattedQuery = String.format(query, Arrays.asList(args).stream()
				.map((arg) -> (arg instanceof String || arg instanceof Identifier) ? ("\"" + arg + "\"") : arg)
				.map((arg) -> (arg instanceof List) ? "[" + ((List<Object>) arg).stream().map((obj) -> (obj instanceof Number ? obj.toString() : "\"" + obj.toString() + "\"")).collect(Collectors.joining(",")) + "]"
						: arg)
				.map((arg) -> arg == null ? "" : arg)
				.collect(Collectors.toList()).toArray());
		ExecutionResult result = graphQl.getGraphQL().execute(formattedQuery);
		if (result.getErrors().size() > 0) {
			log.error(result.toString());
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

	@SuppressWarnings("unchecked")
	protected <T> T executeWithVariables(String queryName, String query, Map<String, Object> variables) throws Exception {
		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.query(query)
				.variables(variables)
				.build();
		ExecutionResult result = graphQl.getGraphQL().execute(executionInput);
		if (result.getErrors().size() > 0) {
			String messages = result.getErrors().stream().map((e) -> e.getMessage()).collect(Collectors.joining());
			throw new ApiException("Errors encountered during " + queryName + " - " + messages);
		}
		Assert.assertEquals(StringUtils.join(result.getErrors(), '\n'), 0, result.getErrors().size());
		String resultAsJsonString = objectMapper.writeValueAsString(result.getData());
		log.info(queryName + " --> " + resultAsJsonString);
		JSONObject json = new JSONObject(resultAsJsonString);
		Object o = json.get(queryName);
		if (o == JSONObject.NULL) {
			return null;
		}
		return (T) o;
	}
}
