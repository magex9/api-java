package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class ContextDataFetcherTests extends AbstractDataFetcherTests {

	@Autowired CrmAuthenticationService authService;
	
	@Before
	public void setup() throws Exception {
		UserDetails user = new UserDetails(
				new UserIdentifier("admin"), 
				new OrganizationIdentifier("Magex"), 
				new PersonIdentifier("System"), 
				"sysadmin", 
				Status.ACTIVE, 
				List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.willReturn(user).given(authService).getAuthenticatedUser();
		BDDMockito.willReturn(user.getPersonId()).given(authService).getAuthenticatedPersonId();
		BDDMockito.willReturn(user.getOrganizationId()).given(authService).getAuthenticatedOrganizationId();
		BDDMockito.willReturn(user.getUserId()).given(authService).getAuthenticatedUserId();
	}
	
	@Test
	public void testContext() throws Exception {
		JSONObject context = execute(
				"context",
				"{ context { currentUser { username } createOrganization createOption } }");
		Assert.assertEquals("sysadmin", context.getJSONObject("currentUser").getString("username"));
		Assert.assertTrue(context.getBoolean("createOrganization"));
		Assert.assertEquals(11, context.getJSONArray("createOption").length());
	}
}
