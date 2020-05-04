package ca.magex.crm.admin;


import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

public class AuthToken {
	
	private static RestTemplate restTemplate = new RestTemplate();
	private static String authToken;
	
	public static void main(String[] args) {
		authToken = restTemplate.exchange(
				RequestEntity
					.post(URI.create("http://localhost:8100/auth/authenticate"))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest("admin", "admin")), 
				JwtToken.class).getBody().getToken();
		
		System.out.println("Obtained authToken: " + authToken);
	}	
}
