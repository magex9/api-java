package ca.magex.crm.graphql;

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

public class RemoteShutdown {

	private static Logger LOG = LoggerFactory.getLogger(RemoteShutdown.class);
	private static String JWT_TOKEN = null;
	
	private static int [] ports = new int [] {9002, 9004};
	
	public static void main(String[] args) throws Exception {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			for (int port : ports) {
				LOG.info("Attempting to shut down node on port: " + port + "...");
				try {
					/* token can be reused across nodes */
					if (JWT_TOKEN == null) {
						authenticateJwt(httpclient, "http://localhost:" + port + "/crm/authenticate", "SXA1", "sysadmin");
						LOG.info("Authentication Token: " + JWT_TOKEN);
					}
					
					shutdownServer(httpclient, "http://localhost:" + (port + 1) + "/actuator/shutdown");
				}
				catch(GraphQLClientException e) {
					StringBuilder message = new StringBuilder(e.getMessage());
					Throwable t = e.getCause();
					while(t != null) {
						message.append(" --> " + t.getMessage());
						t = t.getCause();
					}
					LOG.error(message.toString());
				}
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
	private static void authenticateJwt(CloseableHttpClient httpclient, String authEndpoint, String username, String password) {
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
					JWT_TOKEN = token.getString("token");
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error during authentication", e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of authenticate() took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}

	private static void shutdownServer(CloseableHttpClient httpclient, String shutdownEndpoint) {
		long t1 = System.currentTimeMillis();
		try {
			HttpPost httpPost = new HttpPost(shutdownEndpoint);			
			httpPost.setHeader("Authorization", "Bearer " + JWT_TOKEN);			
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					JSONObject shutdownMessage = new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
					LOG.info(shutdownMessage.toString(3));
				} else {
					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
				}
			}
		} catch (Exception e) {
			throw new GraphQLClientException("Error during shutdown", e);
		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug("execution of authenticate() took " + (System.currentTimeMillis() - t1) + "ms.");
			}
		}
	}
}
