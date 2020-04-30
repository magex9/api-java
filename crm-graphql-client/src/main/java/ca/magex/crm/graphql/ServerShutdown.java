package ca.magex.crm.graphql;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.auth.AuthClient;

public class ServerShutdown {

	private static final Logger LOG = LoggerFactory.getLogger(ServerShutdown.class);
	
	private static int applicationPort = 9002; 
	private static int[] managmentPorts = new int [] {9003};
	
	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();
		
		AuthClient authClient = new AuthClient("http", "localhost", applicationPort, "/crm");
		
		String authToken = authClient.acquireJwtToken("sysadmin", "sysadmin").getBody().getToken();
		LOG.info("Obtained token: " + authToken);
		
		for (int managementPort : managmentPorts) {
			try {
				ResponseEntity<String> response = restTemplate.exchange(
						RequestEntity
							.post(URI.create("http://localhost:" + managementPort + "/actuator/shutdown"))
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", "Bearer " + authToken)
							.body(""),
						String.class);		
				if (response.getStatusCode().is2xxSuccessful()) {				
					LOG.info("Shutdown on management port: " + managementPort + "\n" + response.getBody());
				}
				else {
					LOG.error("Error shutting down on management port: " + managementPort + "\n" + response.getStatusCode().getReasonPhrase());
				}
			}
			catch(Exception e) {
				LOG.error("Unable to shutdown management port: " + managementPort, e);
			}
		}
	}
	
//	/**
//	 * runs our authentication mechanism
//	 * 
//	 * @param authEndpoint
//	 * @param username
//	 * @param password
//	 */
//	private static JSONObject executeShutdown(String authEndpoint, String token) {
//		return 
//		
//		long t1 = System.currentTimeMillis();
//		try {
//			HttpPost httpPost = new HttpPost(authEndpoint);
//			httpPost.setHeader("Authorization", "Bearer " + token);
//			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
//				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//					return new JSONObject(StreamUtils.copyToString(response.getEntity().getContent(), Charset.forName("UTF-8")));
//				} else {
//					throw new GraphQLClientException("Status: " + response.getStatusLine().getStatusCode());
//				}
//			}
//		} catch (Exception e) {
//			throw new GraphQLClientException("Error during executeShutdown()", e);
//		} finally {
//			if (LOG.isDebugEnabled()) {
//				LOG.debug("execution of executeShutdown() took " + (System.currentTimeMillis() - t1) + "ms.");
//			}
//		}
//	}
}
