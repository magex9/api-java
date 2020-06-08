package ca.magex.crm.caching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.config.MockConfig;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, TestConfig.class, MockConfig.class })
@ActiveProfiles(profiles = { MagexCrmProfiles.CRM_NO_AUTH })
public class CachingOrganizationServiceTests {

	@Autowired private CrmOrganizationService delegate;
	@Autowired @Qualifier("caching") private CrmOrganizationService organizationService;
	
	@Test
	public void testCaching() {
		final AtomicInteger orgIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation)-> {			
			return new OrganizationDetails(new Identifier(Integer.toString(orgIndex.incrementAndGet())), Status.ACTIVE, invocation.getArgument(0), null, null, invocation.getArgument(1));
		}).given(delegate).createOrganization(Mockito.anyString(), Mockito.anyList());
		
		OrganizationDetails orgDetails = organizationService.createOrganization("hello", List.of("ORG"));

		OrganizationDetails cached = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());

		cached = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());

		organizationService.findOrganizationDetails(new Identifier("ABC"));
		organizationService.findOrganizationDetails(new Identifier("ABC"));
		
	}
}
