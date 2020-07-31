package ca.magex.crm.restful.client;

import java.util.List;

import org.junit.Before;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestClientTestConfig.class })
@ActiveProfiles(profiles = {
	CrmProfiles.AUTH_EMBEDDED_JWT,
	CrmProfiles.CRM_NO_AUTH,
	CrmProfiles.DEV
})
public class RestSampleTest {
	
	@LocalServerPort private int randomPort;
	
	private CrmServices crm;

	@Before
	public void setup() {
		crm = new RestTemplateClient("http://localhost:" + randomPort + "/crm", null, "admin", "admin").getServices();
	}
	
	@Test
	public void testSomething() throws Exception {
		crm.findOrganizationDetails(new OrganizationsFilter()).getContent().forEach(o -> System.out.println(o.getDisplayName() + " (" + o.getOrganizationId() + ")"));
		OrganizationDetails org = crm.createOrganization("Scotts Org", List.of(AuthenticationGroupIdentifier.ORG), List.of(BusinessGroupIdentifier.EXTERNAL));
		System.out.println(org.getDisplayName() + " (" + org.getOrganizationId() + ")");
	}
	
}
