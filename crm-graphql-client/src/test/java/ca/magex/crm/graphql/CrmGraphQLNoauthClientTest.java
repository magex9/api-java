package ca.magex.crm.graphql;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.service.GraphQLOptionService;
import ca.magex.crm.graphql.client.service.GraphQLOrganizationService;
import ca.magex.crm.graphql.config.GraphQLClientTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { GraphQLClientTestConfig.class })
@ActiveProfiles(profiles = {
		CrmProfiles.AUTH_EMBEDDED_JWT,
		CrmProfiles.CRM_NO_AUTH,
		CrmProfiles.DEV
})
public class CrmGraphQLNoauthClientTest {

	@LocalServerPort private int randomPort;

	@Test
	public void runTests() throws Exception {

		GraphQLClient client = new GraphQLClient(
				"http://localhost:" + randomPort + "/crm/graphql",
				"/organization-service-queries.properties");

		GraphQLOptionService optionService = new GraphQLOptionService(client);
		optionService.createOption(null, Type.PROVINCE, new Localized("AU/NSW", "New South Wales", "New South Wales FR"));
		
		GraphQLOrganizationService organizationService = new GraphQLOrganizationService(client);
		
		OrganizationDetails o1 = organizationService.createOrganization(
				"Maple Leafs", 
				List.of(new AuthenticationGroupIdentifier("CRM")),
				List.of(new BusinessGroupIdentifier("EXECS")));
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getAuthenticationGroupIds().size());
		Assert.assertEquals(new AuthenticationGroupIdentifier("CRM"), o1.getAuthenticationGroupIds().get(0));
		Assert.assertEquals(1, o1.getBusinessGroupIds().size());
		Assert.assertEquals(new BusinessGroupIdentifier("EXECS"), o1.getBusinessGroupIds().get(0));
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertNull(o1.getMainContactId());
//		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		

		/* we are running these tests with an embedded authentication server so everything is on the same servlet */
		//		CrmServicesGraphQLClientImpl crmServices = new CrmServicesGraphQLClientImpl("http://localhost:" + randomPort + "/crm/graphql");
		//		CrmServicesTestSuite testSuite = new CrmServicesTestSuite(new Crm(
		//				Mockito.mock(CrmInitializationService.class), 
		//				crmServices, 
		//				new BasicPolicies(crmServices, crmServices, crmServices, crmServices, crmServices, crmServices)));

		//		testSuite.runAllTests();
	}
}
