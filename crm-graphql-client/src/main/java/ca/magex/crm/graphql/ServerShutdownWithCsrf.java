package ca.magex.crm.graphql;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

public class ServerShutdownWithCsrf {

	private static final Logger LOG = LoggerFactory.getLogger(ServerShutdownWithCsrf.class);
	
	private static int applicationPort = 9002; 
	private static int[] managmentPorts = new int [] {9003};
	private static RestTemplate restTemplate = new RestTemplate();
	private static String authToken;
	private static String csrfToken;
	private static List<String> cookies;
	
	public static void main(String[] args) {
		ResponseEntity<String> csrf = restTemplate.exchange(RequestEntity
				.get(URI.create("http://localhost:" + applicationPort + "/crm"))
				.build(), String.class);
		
		System.out.println(csrf.getStatusCode());
		cookies = csrf.getHeaders().entrySet()
			.stream()
			.filter((entry) -> entry.getKey().equals("Set-Cookie"))
			.map((entry) -> entry.getValue())
			.findFirst()
			.orElse(Collections.emptyList());
		
		if (cookies.size() > 0) {
			System.out.println("Received cookies: " + cookies);
			csrfToken = Arrays.asList(cookies.get(0).split(";"))
					.stream()
					.map((c) -> c.split("="))
					.filter((c) -> c[0].contentEquals("XSRF-TOKEN"))
					.findFirst()
					.orElse(new String[] {"",""})[1];
			System.out.println("Received csrfToken: " + csrfToken);
		}
		
		
		authToken = restTemplate.exchange(
				RequestEntity
					.post(URI.create("http://localhost:" + applicationPort + "/crm/authenticate"))
					.contentType(MediaType.APPLICATION_JSON)
					.header("X-XSRF-TOKEN", csrfToken)
					.header("Cookie", cookies.get(0))
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
							.header("X-XSRF-TOKEN", csrfToken)
							.header("Cookie", cookies.get(0))
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
