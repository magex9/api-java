package ca.magex.crm.spring.security;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ca.magex.crm.spring.security.jwt.JwtRequest;
import ca.magex.crm.spring.security.jwt.JwtToken;

public class AuthClient implements Closeable {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private CloseableHttpClient httpclient;
	private String protocol;
	private String host;
	private Integer port;
	private String context;
	private RestTemplate restTemplate = new RestTemplate();
	
	/**
	 * Creates a new authentication client for the given host
	 * @param host
	 */
	public AuthClient(String protocol, String host, Integer port, String context) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.context = context;
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
	public ResponseEntity<JwtToken> acquireJwtToken(String username, String password) {
		return restTemplate.exchange(
				RequestEntity
					.post(URI.create(protocol + "://" + host + ":" + port + context + "/authenticate"))
					.contentType(MediaType.APPLICATION_JSON)
					.body(new JwtRequest(username, password)), 
				JwtToken.class);
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