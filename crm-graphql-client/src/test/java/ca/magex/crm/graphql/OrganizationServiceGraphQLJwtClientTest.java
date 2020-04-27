package ca.magex.crm.graphql;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.graphql.client.CrmServicesGraphQLClientImpl;
import ca.magex.crm.spring.security.jwt.JwtAuthDetailsService;
import ca.magex.crm.spring.security.jwt.JwtAuthenticatedPrincipal;
import ca.magex.crm.spring.security.jwt.JwtAuthenticationToken;
import ca.magex.crm.spring.security.jwt.JwtTokenService;
import ca.magex.crm.test.CrmServicesTestSuite;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrganizationServiceGraphQLJwtClientTest {

	@LocalServerPort private int randomPort;
	
	@Autowired private JwtTokenService tokenService;

	@MockBean private JwtAuthDetailsService authDetailsService;
	
	@Test
	public void runTests() {
		CrmServicesGraphQLClientImpl crmServices = new CrmServicesGraphQLClientImpl("http://localhost:" + randomPort + "/crm/graphql");
		
		/* generate a fake token to use with our mocked details service and set the token in the client */
		Authentication auth = Mockito.mock(Authentication.class);
		BDDMockito.willReturn("CXA0").given(auth).getName();
		String jwtToken = tokenService.generateToken(auth);		
		ReflectionTestUtils.setField(crmServices, "jwtToken", jwtToken);
		
		/* mock out the auth details to recognize the fake token */
		JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtAuthenticatedPrincipal("CXA0"), null, Set.of(new SimpleGrantedAuthority("CRM_ADMIN")));
		Mockito.when(authDetailsService.getJwtAuthenticationTokenForUsername(jwtToken)).thenReturn(authentication);		

		CrmServicesTestSuite testSuite = new CrmServicesTestSuite();
		ReflectionTestUtils.setField(testSuite, "lookupService", crmServices);
		ReflectionTestUtils.setField(testSuite, "organizationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "locationService", crmServices);
		ReflectionTestUtils.setField(testSuite, "personService", crmServices);

		testSuite.runAllTests();
	}
}
