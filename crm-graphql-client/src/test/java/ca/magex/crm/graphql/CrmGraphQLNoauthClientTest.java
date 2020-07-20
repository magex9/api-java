package ca.magex.crm.graphql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.graphql.client.GraphQLClient;
import ca.magex.crm.graphql.client.service.GraphQLOptionService;
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
		
		Option au = optionService.createOption(null, Type.COUNTRY, new Localized("AU", "Australia", "Australie"));
		
		Option nsw = optionService.createOption(au.getOptionId(), Type.PROVINCE, new Localized("AU/NSW", "New South Wales", "New South Wales FR"));
		
		optionService.updateOptionName(nsw.getOptionId(), new Localized("AU/NSW", "new-south-wales", "new-south-wales-fr"));
		
		optionService.disableOption(nsw.getOptionId());
		
		optionService.enableOption(nsw.getOptionId());
		
		long v = optionService.countOptions(new OptionsFilter(null, new CountryIdentifier("AU"), Type.PROVINCE, null));
		
		int k = optionService.findOptions(new OptionsFilter(null, new CountryIdentifier("AU"), Type.PROVINCE, null)).getSize();
		System.out.println(v + "," + k);
		
		
		
//		GraphQLOrganizationService organizationService = new GraphQLOrganizationService(client);
//		
//		OrganizationDetails o1 = organizationService.createOrganization(
//				"Maple Leafs", 
//				List.of(new AuthenticationGroupIdentifier("CRM")),
//				List.of(new BusinessGroupIdentifier("EXECS")));
		
		
		

		/* we are running these tests with an embedded authentication server so everything is on the same servlet */
		//		CrmServicesGraphQLClientImpl crmServices = new CrmServicesGraphQLClientImpl("http://localhost:" + randomPort + "/crm/graphql");
		//		CrmServicesTestSuite testSuite = new CrmServicesTestSuite(new Crm(
		//				Mockito.mock(CrmInitializationService.class), 
		//				crmServices, 
		//				new BasicPolicies(crmServices, crmServices, crmServices, crmServices, crmServices, crmServices)));

		//		testSuite.runAllTests();
	}
}
