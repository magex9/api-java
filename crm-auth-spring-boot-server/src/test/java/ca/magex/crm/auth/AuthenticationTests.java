package ca.magex.crm.auth;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.spring.security.AuthClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTests {

	@LocalServerPort private int randomPort;
	
	@Test
	public void testAuth() throws Exception {
		Logger logger = LoggerFactory.getLogger(getClass());
		
		AuthClient authClient = new AuthClient("http://localhost:" + randomPort);

		JSONObject jsonResponse = authClient.acquireJwtToken("app_crm", "NutritionFactsPer1Can");
		Assert.assertTrue(jsonResponse.getString("status"), jsonResponse.getBoolean("valid"));
		logger.info("Acquired token: " + jsonResponse.getString("token"));

		JSONObject tokenDetailsResponse = authClient.validateToken(jsonResponse.getString("token"), jsonResponse.getString("token"));
		Assert.assertTrue(tokenDetailsResponse.getString("status"), tokenDetailsResponse.getBoolean("valid"));
		logger.info("Token Details : " + tokenDetailsResponse.toString());

		JSONObject userDetailsResponse = authClient.getAuthDetails("app_crm", jsonResponse.getString("token"));
		Assert.assertTrue(userDetailsResponse.getString("status"), userDetailsResponse.getBoolean("valid"));
		logger.info("Acquired user details: " + userDetailsResponse.getString("username") + " with authorities " + userDetailsResponse.getJSONArray("grantedAuthorities"));

		authClient.close();
	}
}
