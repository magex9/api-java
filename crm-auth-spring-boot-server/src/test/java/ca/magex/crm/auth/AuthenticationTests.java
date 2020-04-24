package ca.magex.crm.auth;

import java.net.ConnectException;
import java.nio.charset.Charset;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

public class AuthenticationTests {

	@Test
	public void testAuth() throws JSONException {
		System.out.println(authenticateJwt("http://localhost:" + 9012 + "/crm/authenticate", "admin", "admin"));
		System.out.println(authenticateJwt("http://localhost:" + 9012 + "/crm/authenticate", "sysadmin", "admin"));
		System.out.println(authenticateJwt("http://localhost:" + 9012 + "/crm/authenticate", "sysadmin", "sysadmin"));
	}

	/**
	 * runs our authentication mechanism
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 */
	public String authenticateJwt(String authEndpoint, String username, String password) throws JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(authEndpoint);
			JSONObject json = new JSONObject();
			json.put("username", username);
			json.put("password", password);
			httpPost.setEntity(new StringEntity(json.toString()));
			httpPost.setHeader("Content-Type", "application/json");
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject token = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					token.put("status", response.getStatusLine());
					return token.toString();
				} else {
					JSONObject status = new JSONObject();
					status.put("status", response.getStatusLine());
					return status.toString();
				}
			}
		} catch (ConnectException ce) {
			JSONObject status = new JSONObject();
			status.put("status", ce.getMessage());
			return status.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error during authentication", e);
		}
	}
}
