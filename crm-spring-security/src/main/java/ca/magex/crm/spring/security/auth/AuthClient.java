package ca.magex.crm.spring.security.auth;

import java.io.IOException;
import java.net.URI;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

public class AuthClient {

	private String protocol;
	private String host;
	private Integer port;
	private String context;
	private RestTemplate restTemplate = new RestTemplate();
	
	public static ResponseErrorHandler NoOpErrorHandler = new ResponseErrorHandler() {
		
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {			
			return false;
		}
		
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
		}
	};
	
	/**
	 * Creates a new authentication client for the given host
	 * @param host
	 */
	public AuthClient(String protocol, String host, Integer port, String context) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.context = context;
	}
	
	/**
	 * unsecured, returns a JwtToken object wrapped in a Response Entity
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public ResponseEntity<JwtToken> acquireJwtToken(String username, String password) {
		restTemplate.setErrorHandler(NoOpErrorHandler);
		return restTemplate.exchange(
				RequestEntity
					.post(URI.create(protocol + "://" + host + ":" + port + context + "/authenticate"))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest(username, password)), 
				JwtToken.class);
	}
	
	/**
	 * returns an Auth details, generally requires the token for an account with AUTH_REQUEST role
	 * 
	 * @param udEndpoint
	 * @param username
	 * @param token
	 * @return
	 * @throws JSONException
	 */
	public ResponseEntity<AuthDetails> validateJwtToken(String tokenToValidate, String authToken) {
		return restTemplate.exchange(
				RequestEntity
					.post(URI.create(protocol + "://" + host + ":" + port + context + "/validate"))
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + authToken)
					.body(new JwtToken(tokenToValidate)),
				AuthDetails.class);		
	}
	
	
}