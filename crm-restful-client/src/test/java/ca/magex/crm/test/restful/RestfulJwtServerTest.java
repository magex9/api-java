package ca.magex.crm.test.restful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hamcrest.CoreMatchers;
import org.json.JSONObject;
import org.junit.Test;

import com.mashape.unirest.http.Unirest;

public class RestfulJwtServerTest {
	
	private String username = "admin";
	
	private String password = "admin";

	@Test
	public void testAuthenticationKey() throws Exception {
		String token = getToken(username, password);
		assertThat(token, CoreMatchers.notNullValue());
	}
	
	@Test
	public void testConfigJson() throws Exception {
		String config = getConfig(getToken(username, password));
		JSONObject json = new JSONObject(config);
		assertEquals("3.0.0", json.getString("openapi"));
		assertTrue(json.getJSONObject("paths").has("/organizations"));
	}

	public static String getConfig(String key) throws Exception {
		return Unirest.get("http://localhost:8080/api.json")
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + key)
			.asString()
			.getBody();
	}

	private static String getToken(String username, String password) throws Exception {
		return new JSONObject(Unirest.post("http://localhost:8080/authenticate")
			.header("Content-Type", "application/json")
			.body("{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }")
			.asString()
			.getBody()).getString("token");
	}

	
}
