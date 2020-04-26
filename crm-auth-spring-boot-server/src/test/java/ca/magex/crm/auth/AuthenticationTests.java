package ca.magex.crm.auth;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.spring.security.AuthClient;

public class AuthenticationTests {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private String port = "9012";
	
	@Test
	public void testAuth() throws Exception {
		AuthClient authClient = new AuthClient("http://localhost:" + port);
				
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
