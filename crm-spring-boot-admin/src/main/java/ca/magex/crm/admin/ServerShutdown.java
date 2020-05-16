package ca.magex.crm.admin;


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
	
	private static RestTemplate restTemplate = new RestTemplate();
	private static String authToken;
	
	public static void main(String[] args) {
		if (args.length != 4) {
			System.out.println("Invalid ServerShutdown usage: authServerUri username password actuatorUri");
			System.exit(1);
		}
		
		authToken = restTemplate.exchange(
				RequestEntity
					.post(URI.create(args[0]))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest(args[1], args[2])), 
				JwtToken.class).getBody().getToken();
		
		System.out.println("Obtained authToken: " + authToken);
		
		doShutdown(args[3]);
	}
	
	public static void doShutdown(String actuatorUri) {				
		try {
			ResponseEntity<String> response = restTemplate.exchange(
					RequestEntity
						.post(URI.create(actuatorUri))
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + authToken)
						.body(""),
					String.class);		
			if (response.getStatusCode().is2xxSuccessful()) {				
				LOG.info("Shutdown Successfuly:\n" + response.getBody());
			}
			else {
				LOG.error("Shutdown Failed\n" + response.getStatusCode().getReasonPhrase());
			}
		}
		catch(Exception e) {
			LOG.error("Shutdown Failed\n", e);
		}
	}
}
