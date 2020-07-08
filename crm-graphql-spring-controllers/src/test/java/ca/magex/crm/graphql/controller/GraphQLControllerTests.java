package ca.magex.crm.graphql.controller;

import java.security.Principal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.graphql.GraphQLTestConfig;
import ca.magex.crm.test.config.UnauthenticatedTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = { 
		UnauthenticatedTestConfig.class
	   ,GraphQLTestConfig.class
})
public class GraphQLControllerTests {

	@Autowired private MockMvc mockMvc;

	@Autowired private Crm crm;

	@Before
	public void before() {
		crm.reset();
		crm.initializeSystem("johnnuy", new PersonName(null, "Jonny", "Alex", "Thomson"), "jonny@johnnuy.org", "admin", "admin");
	}

	@Test
	public void doGraphQlPostQuery() throws Exception {		
		String query = "mutation { createOption (type: \"COUNTRIES\", name: {code: \"PT\", english: \"Petoria\", french: \"Pètorie\"} ) { optionId } }";
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/graphql")
				.principal(new Principal() {					
					@Override
					public String getName() {
						return "johnnuy";
					}
				})
				.content(query);

		String response = mockMvc.perform(postQueryRequest)				
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOption"));
	}

	@Test
	public void doJsonPostQueryWithoutVariables() throws Exception {
		String query = "mutation { createOption (type: \"COUNTRIES\", name: {code: \"PT\", english: \"Petoria\", french: \"Pètorie\"} ) { optionId } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(request.toString(3));

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOption"));
				
	}

	@Test
	public void doJsonPostQueryWithBlankVariables() throws Exception {
		String query = "mutation { createOption (type: \"COUNTRIES\", name: {code: \"PT\", english: \"Petoria\", french: \"Pètorie\"} ) { optionId } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", new JSONObject());
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(request.toString(3));

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOption"));
	}

	@Test
	public void doJsonPostQueryWithNullVariables() throws Exception {
		String query = "mutation { createOption (type: \"COUNTRIES\", name: {code: \"PT\", english: \"Petoria\", french: \"Pètorie\"} ) { optionId } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", JSONObject.NULL);
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(request.toString(3));

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOption"));
	}

	@Test
	public void doJsonPostQueryWithVariables() throws Exception {
		String query = "mutation ($code: String!, $english: String!, $french: String!) { createOption(type: \"COUNTRIES\", name: {code: $code, english: $english, french: $french}) { optionId } }";
		JSONObject variables = new JSONObject();
		variables.put("code", "PT");
		variables.put("english", "Petoria");
		variables.put("french", "Pètorie");

		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", variables);

		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(request.toString(3));

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOption"));
		
		/* add an array variable */
		query = "mutation ($displayName: String!, $authenticationGroups: [String]!, $businessGroups: [String]!) { " + 
				"createOrganization(displayName: $displayName, authenticationGroups: $authenticationGroups, businessGroups: $businessGroups) { " +
				"organizationId } }";
		variables = new JSONObject();
		variables.put("displayName", "MyOrg");
		JSONArray authenticationGroupsArray = new JSONArray();
		authenticationGroupsArray.put("CRM");
		variables.put("authenticationGroups", authenticationGroupsArray);
		JSONArray businessGroupsArray = new JSONArray();
		businessGroupsArray.put("IMIT");
		variables.put("businessGroups", businessGroupsArray);
		
		request = new JSONObject();
		request.put("query", query);
		request.put("variables", variables);
		
		postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content(request.toString(3));
		
		response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();
		
		/* parse response */
		jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOrganization"));	
	}

	@Test
	public void doJsonPostBadRequest() throws Exception {
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content("hi my name is...");

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("Non parseable contents provided", response);
	}

	@Test
	public void doContentlessPostRequest() throws Exception {
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/json")
				.content("");

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("No content provided", response);
	}

	@Test
	public void doUnknownPost() throws Exception {
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.header(HttpHeaders.CONTENT_TYPE, "application/text")
				.content("hello world!");

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("Unknown Content-Type specified, expected one of 'application/json' or 'application/graphql'", response);
	}

	@Test
	public void doContentTypeLessPost() throws Exception {
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.headers(HttpHeaders.EMPTY)
				.content("hello world!");

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("No Content-Type specified, expected one of 'application/json' or 'application/graphql'", response);
	}

	@Test
	public void doGraphqlGetQueryWithoutVariables() throws Exception {		
		String query = "{ findOptions(filter: { code: \"ADMIN\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { optionId } } }";
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.get("/graphql")
				.principal(new Principal() {					
					@Override
					public String getName() {
						return "johnnuy";
					}
				})
				.param("query", query);

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findOptions"));
		
		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findOptions");
		Assert.assertEquals(4, page.getJSONArray("content").length());
	}
		
	@Test
	public void doGraphqlGetQueryWithEmptyVariables() throws Exception {		
		String query = "{ findOptions(filter: { code: \"ADMIN\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { optionId } } }";
		
		JSONObject variables = new JSONObject();
		
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.get("/graphql")
				.param("query", query)
				.param("variables", variables.toString());

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findOptions"));
		
		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findOptions");
		Assert.assertEquals(4, page.getJSONArray("content").length());
	}

	@Test
	public void doGraphqlGetQueryWithVariables() throws Exception {
		String query = "query ($code: String!) { findOptions(filter: { code: $code }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { optionId } } }";
		
		JSONObject variables = new JSONObject();
		variables.put("code", "ADMIN");
		
		RequestBuilder getQueryRequest = MockMvcRequestBuilders
				.get("/graphql")
				.param("query", query)
				.param("variables", variables.toString());

		String response = mockMvc.perform(getQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn().getResponse().getContentAsString();

		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findOptions"));
		
		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findOptions");
		Assert.assertEquals(4, page.getJSONArray("content").length());		
	}
	
	@Test
	public void doGetBadVariablesBadRequest() throws Exception {
		String query = "{ findOptions(filter: { code: \"ADMIN\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { optionId } } }";
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.get("/graphql")
				.param("query", query)
				.param("variables", "Hello World!");

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("Non parseable variables provided", response);
	}
	
	@Test
	public void doGetMutationBadRequest() throws Exception {
		String query = "mutation { createOption(type: \"COUNTIRES\", name: {code: \"PT\", englishName: \"Petoria\", frenchName: \"Pètorie\") { optionId } }";
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.get("/graphql")
				.param("query", query);

		String response = mockMvc.perform(postQueryRequest)
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
				.andReturn().getResponse().getContentAsString();

		Assert.assertEquals("Cannot process mutation over GET method", response);
	}
}
