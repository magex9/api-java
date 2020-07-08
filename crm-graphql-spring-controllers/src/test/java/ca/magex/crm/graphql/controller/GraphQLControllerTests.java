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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.system.Localized;
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
	public void reset() {
		crm.reset();
	}

	@Test
	public void doGraphQlPostQuery() throws Exception {		
		String query = "mutation { createOption (type: \"COUNTRIES\", english: \"Petoria\", french: \"Pètorie\") { optionId } }";
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

//	@Test
//	public void doJsonPostQueryWithoutVariables() throws Exception {
//		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
//		JSONObject request = new JSONObject();
//		request.put("query", query);
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content(request.toString(3));
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
//				
//	}
//
//	@Test
//	public void doJsonPostQueryWithBlankVariables() throws Exception {
//		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
//		JSONObject request = new JSONObject();
//		request.put("query", query);
//		request.put("variables", new JSONObject());
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content(request.toString(3));
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
//	}
//
//	@Test
//	public void doJsonPostQueryWithNullVariables() throws Exception {
//		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
//		JSONObject request = new JSONObject();
//		request.put("query", query);
//		request.put("variables", JSONObject.NULL);
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content(request.toString(3));
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
//	}
//
//	@Test
//	public void doJsonPostQueryWithVariables() throws Exception {
//		String query = "mutation ($code: String!, $englishName: String!, $frenchName: String!) { createGroup(code: $code, englishName: $englishName, frenchName: $frenchName) { groupId code englishName frenchName status } }";
//		JSONObject variables = new JSONObject();
//		variables.put("code", "DEV");
//		variables.put("englishName", "Developers");
//		variables.put("frenchName", "Developeurs");
//
//		JSONObject request = new JSONObject();
//		request.put("query", query);
//		request.put("variables", variables);
//
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content(request.toString(3));
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
//		
//		/* add an array variable */
//		query = "mutation ($displayName: String!, $groups: [String]!) { createOrganization(displayName: $displayName, groups: $groups) { organizationId } }";
//		variables = new JSONObject();
//		variables.put("displayName", "MyOrg");
//		JSONArray jsonArray = new JSONArray();
//		jsonArray.put("DEV");
//		variables.put("groups", jsonArray);
//		
//		request = new JSONObject();
//		request.put("query", query);
//		request.put("variables", variables);
//		
//		postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content(request.toString(3));
//		
//		response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//		
//		/* parse response */
//		jsonResponse = new JSONObject(response);
//		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createOrganization"));
//		
//	}
//
//	@Test
//	public void doJsonPostBadRequest() throws Exception {
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content("hi my name is...");
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("Non parseable contents provided", response);
//	}
//
//	@Test
//	public void doContentlessPostRequest() throws Exception {
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/json")
//				.content("");
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("No content provided", response);
//	}
//
//	@Test
//	public void doUnknownPost() throws Exception {
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.header(HttpHeaders.CONTENT_TYPE, "application/text")
//				.content("hello world!");
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("Unknown Content-Type specified, expected one of 'application/json' or 'application/graphql'", response);
//	}
//
//	@Test
//	public void doContentTypeLessPost() throws Exception {
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.post("/graphql")
//				.headers(HttpHeaders.EMPTY)
//				.content("hello world!");
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("No Content-Type specified, expected one of 'application/json' or 'application/graphql'", response);
//	}
//
//	@Test
//	public void doGraphqlGetQueryWithoutVariables() throws Exception {		
//		permissionService.createGroup(new Localized("ADM", "Admin", "Admin"));
//		String query = "{ findGroups(filter: { code: \"ADM\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }";
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.get("/graphql")
//				.principal(new Principal() {					
//					@Override
//					public String getName() {
//						return "johnnuy";
//					}
//				})
//				.param("query", query);
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findGroups"));
//		
//		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findGroups");
//		Assert.assertEquals(1, page.getJSONArray("content").length());
//	}
//		
//	@Test
//	public void doGraphqlGetQueryWithEmptyVariables() throws Exception {		
//		permissionService.createGroup(new Localized("ADM", "Admin", "Admin"));
//		String query = "{ findGroups(filter: { code: \"ADM\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }";
//		
//		JSONObject variables = new JSONObject();
//		
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.get("/graphql")
//				.param("query", query)
//				.param("variables", variables.toString());
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findGroups"));
//		
//		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findGroups");
//		Assert.assertEquals(1, page.getJSONArray("content").length());
//	}
//
//	@Test
//	public void doGraphqlGetQueryWithVariables() throws Exception {
//		permissionService.createGroup(new Localized("ADM", "Admin", "Admin"));
//		String query = "query ($code: String!) { findGroups(filter: { code: $code }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }";
//		
//		JSONObject variables = new JSONObject();
//		variables.put("code", "ADM");
//		
//		RequestBuilder getQueryRequest = MockMvcRequestBuilders
//				.get("/graphql")
//				.param("query", query)
//				.param("variables", variables.toString());
//
//		String response = mockMvc.perform(getQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().isOk())
//				.andReturn().getResponse().getContentAsString();
//
//		/* parse response */
//		JSONObject jsonResponse = new JSONObject(response);
//		Assert.assertEquals(jsonResponse.getJSONArray("errors").toString(3), 0, jsonResponse.getJSONArray("errors").length());
//		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
//		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("findGroups"));
//		
//		JSONObject page = jsonResponse.getJSONObject("data").getJSONObject("findGroups");
//		Assert.assertEquals(1, page.getJSONArray("content").length());		
//	}
//	
//	@Test
//	public void doGetBadVariablesBadRequest() throws Exception {
//		String query = "{ findGroups(filter: { code: \"ADM\" }, paging: {pageNumber: 1, pageSize: 5, sortField: [\"englishName\"], sortOrder: [\"ASC\"]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }";
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.get("/graphql")
//				.param("query", query)
//				.param("variables", "Hello World!");
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("Non parseable variables provided", response);
//	}
//	
//	@Test
//	public void doGetMutationBadRequest() throws Exception {
//		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
//		RequestBuilder postQueryRequest = MockMvcRequestBuilders
//				.get("/graphql")
//				.param("query", query);
//
//		String response = mockMvc.perform(postQueryRequest)
//				.andDo(MockMvcResultHandlers.print())
//				.andExpect(MockMvcResultMatchers.status().is4xxClientError())
//				.andReturn().getResponse().getContentAsString();
//
//		Assert.assertEquals("Cannot process mutation over GET method", response);
//	}
}
