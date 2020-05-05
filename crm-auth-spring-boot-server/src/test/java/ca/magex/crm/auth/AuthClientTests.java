package ca.magex.crm.auth;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.spring.security.auth.AuthClient;
import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.jwt.JwtToken;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {MagexCrmProfiles.AUTH_EMBEDDED_JWT, MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED})
public class AuthenticationServerJwtTests {

	@LocalServerPort private int randomPort;
	
	@Value("${server.servlet.context-path}") private String context;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void testValidAuthenticationToken() throws Exception {				
		AuthClient authClient = new AuthClient("http", "localhost", randomPort, context);
		ResponseEntity<JwtToken> jwtToken = authClient.acquireJwtToken("app_crm", "NutritionFactsPer1Can");		
		Assert.assertTrue(jwtToken.toString(), jwtToken.getStatusCode().is2xxSuccessful());
		logger.info("Respons Headers:");
		jwtToken.getHeaders().entrySet().forEach((entry) -> logger.info(entry.getKey() + " --> " + entry.getValue()));
		logger.info("Acquired token: " + jwtToken.getBody().getToken());

		ResponseEntity<AuthDetails> authDetails = authClient.validateJwtToken(jwtToken.getBody().getToken(), jwtToken.getBody().getToken());
		Assert.assertTrue(authDetails.toString(), authDetails.getStatusCode().is2xxSuccessful());
		Assert.assertTrue(authDetails.getBody().getFailureReason(), authDetails.getBody().isSuccessful());
		logger.info("Respons Headers:");
		authDetails.getHeaders().entrySet().forEach((entry) -> logger.info(entry.getKey() + " --> " + entry.getValue()));
		logger.info("Token Details : " + authDetails.getBody());
	}
	
	@Test
	public void testInvalidAuthentication() throws Exception {
		AuthClient authClient = new AuthClient("http", "localhost", randomPort, context);
		ResponseEntity<JwtToken> jwtToken = authClient.acquireJwtToken("batman", "DarkKnightRises");		
		Assert.assertTrue(jwtToken.toString(), jwtToken.getStatusCode().is4xxClientError());
	}
}
