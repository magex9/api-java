package ca.magex.crm.auth;

import java.net.ConnectException;
import java.nio.charset.Charset;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

public class AuthenticationTests {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private String port = "9012";
	
	@Test
	public void testAuth() throws JSONException {
		JSONObject jsonResponse = authenticateJwt("http://localhost:" + port + "/auth/authenticate", "app_crm", "NutritionFactsPer1Can");
		Assert.assertTrue(jsonResponse.getString("status"), jsonResponse.getBoolean("valid"));
		logger.info("Acquired token: " + jsonResponse.getString("token"));
		
		JSONObject userDetailsResponse = userDetails("http://localhost:" + port + "/auth/userDetails", "app_crm", jsonResponse.getString("token"));
		Assert.assertTrue(userDetailsResponse.getString("status"), userDetailsResponse.getBoolean("valid"));
		logger.info("Acquired user details: " + userDetailsResponse.getString("username") + " with authorities " + userDetailsResponse.getJSONArray("grantedAuthorities"));
		
//		System.out.println(authenticateJwt("http://localhost:" + 9012 + "/auth/authenticate", "sysadmin", "admin"));
//		System.out.println(authenticateJwt("http://localhost:" + 9012 + "/auth/authenticate", "sysadmin", "sysadmin"));
	}

	/**
	 * runs our authentication mechanism
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 */
	public JSONObject authenticateJwt(String authEndpoint, String username, String password) throws JSONException {
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
					JSONObject jsonResponse = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					jsonResponse.put("valid", Boolean.TRUE);
					jsonResponse.put("status", response.getStatusLine());					
					return jsonResponse;
				} else {
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("valid", Boolean.FALSE);
					jsonResponse.put("status", response.getStatusLine());
					return jsonResponse;
				}
			}
		} catch (ConnectException ce) {
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("valid", Boolean.FALSE);
			jsonResponse.put("status", ce.getMessage());
			return jsonResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error during authentication", e);
		}
	}
	
	public JSONObject userDetails(String udEndpoint, String username, String token) throws JSONException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(udEndpoint);
			JSONObject json = new JSONObject();
			json.put("username", username);
			httpPost.setEntity(new StringEntity(json.toString()));
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Authorization", "Bearer " + token);
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject jsonResponse = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					jsonResponse.put("valid", Boolean.TRUE);
					jsonResponse.put("status", response.getStatusLine());
					return jsonResponse;
				} else {
					JSONObject jsonResponse = new JSONObject();
					jsonResponse.put("valid", Boolean.FALSE);
					jsonResponse.put("status", response.getStatusLine());
					return jsonResponse;
				}
			}
		} catch (ConnectException ce) {
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("valid", Boolean.FALSE);
			jsonResponse.put("status", ce.getMessage());
			return jsonResponse;
		} catch (Exception e) {
			throw new RuntimeException("Error during userDetails", e);
		}
	}
}
