package ca.magex.crm.spring.security;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.Charset;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

import io.jsonwebtoken.JwtException;

public class AuthClient implements Closeable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private CloseableHttpClient httpclient;
	private String host;
	
	/**
	 * Creates a new authentication client for the given nost
	 * @param host
	 */
	public AuthClient(String host) {
		this.host = host;
		this.httpclient = HttpClients.createDefault();
	}
	
	@Override
	public void close() throws IOException {
		logger.debug("Closing http client");
		httpclient.close();
	}
	
	/**
	 * returns a JSONObject with the following format
	 * {
	 * 	"valid" : "true/false",
	 *  "status" : "HTTP response status line",
	 *  "token" : "jwtToken if valid = true"
	 * }
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public JSONObject acquireJwtToken(String username, String password) {		
		try {
			try {
				HttpPost httpPost = new HttpPost(host + "/auth/authenticate");
				JSONObject json = new JSONObject();
				json.put("username", username);
				json.put("password", password);
				logger.debug("Acquiring Jwt Token using credentials: " + json.toString());
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
				throw new JwtException("Error during authentication", e);
			}
		}
		catch(JSONException jsone) {
			throw new JwtException("Error during authentication", jsone);
		}
	}
	
	/**
	 * returns a JSONObject with the following format 
	 * {
	 * 	"valid" : "true/false",
	 *  "status" : "HTTP response status line",
	 *  "username" : "username if valid = true"
	 *  "grantedAuthorities" : [
	 *  	{
	 *  		"authority" : "ROLE1"
	 * 		},
	 * 		{
	 *  		"authority" : "ROLE2"
	 * 		},
	 *  ]
	 * }
	 * @param udEndpoint
	 * @param username
	 * @param authToken
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getAuthDetails(String username, String authToken) {
		try {
			try {
				HttpPost httpPost = new HttpPost(host + "/auth/userDetails");
				JSONObject json = new JSONObject();
				json.put("username", username);
				logger.debug("Getting auth details: " + json.toString() + " using token " + authToken);
				httpPost.setEntity(new StringEntity(json.toString()));
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("Authorization", "Bearer " + authToken);
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
				throw new JwtException("Error during getAuthDetails(" + username + ")", e);
			}
		}
		catch(JSONException jsone) {
			throw new JwtException("Error during getAuthDetails(" + username + ")", jsone);
		}
	}
	
	/**
	 * returns a JSONObject with the following format 
	 * {
	 * 	"valid" : "true/false",
	 *  "status" : "HTTP response status line",
	 *  "username" : "username if valid = true"  
	 * }
	 * @param udEndpoint
	 * @param username
	 * @param token
	 * @return
	 * @throws JSONException
	 */
	public JSONObject validateToken(String tokenToValidate, String authToken) {
		try {
			try {
				HttpPost httpPost = new HttpPost(host + "/auth/validateToken");
				JSONObject json = new JSONObject();
				json.put("token", tokenToValidate);
				logger.debug("validating token details: " + json.toString() + " using token " + authToken);
				httpPost.setEntity(new StringEntity(json.toString()));
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setHeader("Authorization", "Bearer " + authToken);
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
				throw new JwtException("Error during validateToken(" + tokenToValidate + ")", e);
			}
		}
		catch(JSONException jsone) {
			throw new JwtException("Error during validateToken(" + tokenToValidate + ")", jsone);
		}
	}
}