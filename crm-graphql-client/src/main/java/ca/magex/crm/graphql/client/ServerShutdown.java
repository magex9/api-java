package ca.magex.crm.graphql.client;

import java.nio.charset.Charset;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.util.StreamUtils;

import ca.magex.crm.graphql.exceptions.GraphQLClientException;

public class ServerShutdown {

	private static final Logger LOG = LoggerFactory.getLogger(ServerShutdown.class);
	
	private static int applicationPort = 9002; 
	private static int[] managmentPorts = new int [] {9003};
	
	public static void main(String[] args) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		String jwt = authenticateJwt(httpclient, "http://localhost:" + applicationPort + "/crm/authenticate", "SXA2", "admin");
		LOG.info("Obtained token: " + jwt);
		
		for (int managementPort : managmentPorts) {
			try {
				JSONObject response = executeShutdown(httpclient, "http://localhost:" + managementPort + "/actuator/shutdown", jwt);
				LOG.info("Shutdown on management port: " + managementPort + "\n" + response.toString(3));
			}
			catch(Exception e) {
				LOG.error("Unable to shutdown management port: " + managementPort, e);
			}
		}
	}
	
	/**
	 * runs our authentication mechanism
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 */
	private static String authenticateJwt(CloseableHttpClient httpclient, String authEndpoint, String username, String password) {
		long t1 = System.currentTimeMillis();
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
					return token.getString("token");
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error during authenticate()", e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of authenticate() took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}
	
	/**
	 * runs our authentication mechanism
	 * 
	 * @param authEndpoint
	 * @param username
	 * @param password
	 */
	private static JSONObject executeShutdown(CloseableHttpClient httpclient, String authEndpoint, String token) {
		long t1 = System.currentTimeMillis();
		try {
			HttpPost httpPost = new HttpPost(authEndpoint);
			httpPost.setHeader("Authorization", "Bearer " + token);
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					return new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error during executeShutdown()", e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of executeShutdown() took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}
}
