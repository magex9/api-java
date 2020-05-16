package ca.magex.crm.graphql;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

public class ServerShutdown {

	private static final Logger LOG = LoggerFactory.getLogger(ServerShutdown.class);
	
	private static int applicationPort = 9012; 
	private static int[] managmentPorts = new int [] {9013};
	private static RestTemplate restTemplate = new RestTemplate();
	private static String authToken;
	
	public static void main(String[] args) {						
		authToken = restTemplate.exchange(
				RequestEntity
					.post(URI.create("http://localhost:" + applicationPort + "/auth/authenticate"))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest("sysadmin", "sysadmin")), 
				JwtToken.class).getBody().getToken();
		
		System.out.println("Obtained authToken: " + authToken);
		
		doShutdown();
	}
	
	public static void doShutdown() {				
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
}
