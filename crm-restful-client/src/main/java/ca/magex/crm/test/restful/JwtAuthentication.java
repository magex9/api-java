package ca.magex.crm.test.restful;

import org.json.JSONObject;

import com.mashape.unirest.http.Unirest;

public class JwtAuthentication {

	public static void main(String[] args) throws Exception {
		String token = getToken("admin", "admin");
		System.out.println(token);
		String config = getConfig(token);
		System.out.println(config);
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
