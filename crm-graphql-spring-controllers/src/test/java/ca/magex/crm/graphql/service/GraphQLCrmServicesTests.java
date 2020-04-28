package ca.magex.crm.graphql.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.graphql.TestConfig;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class GraphQLCrmServicesTests extends AbstractJUnit4SpringContextTests {

	@Autowired private GraphQLCrmServices graphQl;
	@Autowired private Crm crm;

	@Before
	public void reset() {
		Mockito.reset(crm);		
		User user = new User(new Identifier("ABC"), new Identifier("O1"), new Identifier("P1"), "user", Arrays.asList());		
		BDDMockito.willReturn(user).given(crm).findUserById(user.getUserId());
		BDDMockito.willAnswer((invocation) -> {
			List<String> roles = invocation.getArgument(1);
			return user.withRoles(roles);
		}).given(crm).setUserRoles(Mockito.eq(user.getUserId()), Mockito.any());
	}

	@Test
	public void testSetUserRoles() throws Exception {				
		BDDMockito.willReturn(new Role("A", "A_en", "A_fr")).given(crm).findRoleByCode("A");
		BDDMockito.willReturn(new Role("B", "B_en", "B_fr")).given(crm).findRoleByCode("B");

		ExecutionResult result = graphQl.getGraphQL().execute("mutation { setUserRoles(userId: \"ABC\", roles: [\"A\", \"B\"]) { userId personId username roles } }");
		Assert.assertEquals(result.getErrors().toString(), 0, result.getErrors().size());
		JSONObject json = new JSONObject(result.getData().toString());
		Assert.assertEquals(json.toString(3), 2, json.getJSONObject("setUserRoles").getJSONArray("roles").length());
		
	}
}
