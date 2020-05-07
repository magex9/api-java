package ca.magex.crm.graphql.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.graphql.TestConfig;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { 
		TestConfig.class 
})
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
	})
public class GraphQLCrmServicesTests {

	@Autowired private GraphQLCrmServices graphQl;
	@Autowired private PasswordEncoder passwordEncoder;
	
	/* autowired services */
	@Autowired private CrmLookupService lookupService;	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	@Autowired private CrmPermissionService permissionService;
	
	/* autowired policies */
	@Autowired private CrmOrganizationPolicy organizationPolicy;
	@Autowired private CrmLocationPolicy locationPolicy;
	@Autowired private CrmPersonPolicy personPolicy;
	@Autowired private CrmUserPolicy userPolicy;
	@Autowired private CrmPermissionPolicy permissionPolicy;

	private <T> T execute(String queryName, String query) throws Exception {
		ExecutionResult result = graphQl.getGraphQL().execute(query);
		Assert.assertEquals(result.getErrors().toString(), 0, result.getErrors().size());
		JSONObject json = new JSONObject(result.getData().toString());
		Object o = json.get(queryName);
		if (o == JSONObject.NULL) {
			return null;
		}
		return (T) o;
	}
	
	@Test
	public void testGraphQl() throws Exception {		
		JSONObject johnnuy = execute("createOrganization", "mutation { createOrganization( organizationDisplayName: \"Johnnuy\") { organizationId status displayName } }");
		Assert.assertNotNull(johnnuy);
		Assert.assertEquals("Johnnuy", johnnuy.getString("displayName"));
		
//		BDDMockito.willReturn(new Role("A", "A_en", "A_fr")).given(crm).findRoleByCode("A");
//		BDDMockito.willReturn(new Role("B", "B_en", "B_fr")).given(crm).findRoleByCode("B");
//
//		ExecutionResult result = graphQl.getGraphQL().execute("mutation { setUserRoles(userId: \"ABC\", roles: [\"A\", \"B\"]) { userId personId username roles } }");
//		Assert.assertEquals(result.getErrors().toString(), 0, result.getErrors().size());
//		JSONObject json = new JSONObject(result.getData().toString());
//		Assert.assertEquals(json.toString(3), 2, json.getJSONObject("setUserRoles").getJSONArray("roles").length());
	}
}
