package ca.magex.crm.graphql.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public class GraphQLControllerTests {

	@Autowired private MockMvc mockMvc;
	
	@Autowired private CrmInitializationService initializationService;
	
	@Before
	public void reset() {
		initializationService.reset();
	}
	
	@Test
	public void doPostQueryWithoutVariables() throws Exception {
		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.content(request.toString(3));
		
		String response = mockMvc.perform(postQueryRequest)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
	}
	
	@Test
	public void doPostQueryWithBlankVariables() throws Exception {
		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", new JSONObject());
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.content(request.toString(3));
		
		String response = mockMvc.perform(postQueryRequest)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
	}
	
	@Test
	public void doPostQueryWithNullVariables() throws Exception {
		String query = "mutation { createGroup(code: \"DEV\", englishName: \"Developers\", frenchName: \"Developeurs\") { groupId code englishName frenchName status } }";
		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", JSONObject.NULL);
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.content(request.toString(3));
		
		String response = mockMvc.perform(postQueryRequest)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
	}
	
	@Test
	public void doPostQueryWithVariables() throws Exception {
		String query = "mutation ($code: String!, $englishName: String!, $frenchName: String!) { createGroup(code: $code, englishName: $englishName, frenchName: $frenchName) { groupId code englishName frenchName status } }";
		JSONObject variables = new JSONObject();
		variables.put("code", "DEV");
		variables.put("englishName", "Developers");
		variables.put("frenchName", "Developeurs");		
		
		JSONObject request = new JSONObject();
		request.put("query", query);
		request.put("variables", variables);
		
		RequestBuilder postQueryRequest = MockMvcRequestBuilders
				.post("/graphql")
				.content(request.toString(3));
		
		String response = mockMvc.perform(postQueryRequest)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn().getResponse().getContentAsString();
		
		/* parse response */
		JSONObject jsonResponse = new JSONObject(response);
		Assert.assertEquals(0, jsonResponse.getJSONArray("errors").length());
		Assert.assertNotNull(jsonResponse.getJSONObject("data"));
		Assert.assertNotNull(jsonResponse.getJSONObject("data").getJSONObject("createGroup"));
	}
}
