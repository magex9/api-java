package ca.magex.crm.restful.client.sample;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.restful.client.RestTemplateClient;
import ca.magex.crm.restful.client.config.RestfulClientTestConfig;
import ca.magex.crm.spring.security.auth.AuthProfiles;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		AuthProfiles.EMBEDDED_HMAC,
		CrmProfiles.BASIC,
		CrmProfiles.DEV
})
public class RestfulClientTest {
	
	@LocalServerPort private int randomPort;
	
	@Test
	public void testSomething() throws Exception {
		CrmServices crm = new RestTemplateClient("http://localhost:" + randomPort + "/crm", null, "admin", "admin").getServices();
		crm.findOrganizationDetails(new OrganizationsFilter()).getContent().forEach(o -> System.out.println(o.getDisplayName() + " (" + o.getOrganizationId() + ")"));
		OrganizationDetails org = crm.createOrganization("Scotts Org", List.of(AuthenticationGroupIdentifier.ORG), List.of(BusinessGroupIdentifier.EXTERNAL));
		System.out.println(org.getDisplayName() + " (" + org.getOrganizationId() + ")");
	}
	
}
