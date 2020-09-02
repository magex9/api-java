package ca.magex.crm.restful.client;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.mashape.unirest.http.Unirest;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.json.util.FormattedStringBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { RestfulClientTestConfig.class })
@ActiveProfiles(profiles = {
		AuthProfiles.EMBEDDED_HMAC,
		CrmProfiles.BASIC_NO_AUTH,
		CrmProfiles.DEV
})
public class RestfulSampleTest {
	
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
	
	@Test
	public void testBuildExamples() throws Exception {
		FormattedStringBuilder sb = new FormattedStringBuilder();
		sb.append("<html>");
		sb.append("<body>");
		
		sb.append("<h1>CRM Restful API Usage</h1>");
		
		sb.append("<p>The following documentation will explain how to create a new organization and some of the options available for the system to use.</p>");
		sb.append("<p>Note that this assumes that you have already setup an instance of the system and will use \"admin\" for both the username and password of the system administrator that was setup.");
		sb.append("<p>Although this is being done via \"curl\", you can use java, python, nodejs or any other tools to do the same thing.");
		
		sb.indent("<pre>");
		sb.append(Unirest.get("http://localhost:" + randomPort + "/crm/rest/actions").asString().getBody());
		sb.unindent("</pre>");
		//OrganizationDetails details = crm.createOrganization("Sample Organization", List.of(AuthenticationGroupIdentifier.ORG), List.of(BusinessGroupIdentifier.EXTERNAL));
		
		
		
		sb.append("</body>");
		sb.append("</html>");
		
		File output = new File("src/main/resources/docs/example.html");
		FileUtils.writeStringToFile(output, sb.toString(), StandardCharsets.UTF_8);
		Logger.getLogger(RestfulSampleTest.class).info("Writing: " + output.getAbsolutePath());
	}
	
}
