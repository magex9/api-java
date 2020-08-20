package ca.magex.crm.spring.security.jwt.impl;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.ResourceAccessException;

import ca.magex.crm.spring.security.auth.AuthClient;
import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.spring.security.jwt.JwtAuthDetailsService;
import ca.magex.crm.spring.security.jwt.JwtAuthenticatedPrincipal;
import ca.magex.crm.spring.security.jwt.JwtAuthenticationToken;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.spring.security.jwt.JwtTokenUtils;
import ca.magex.json.model.JsonObject;
import io.jsonwebtoken.JwtException;

/**
 * An implementation of the JwtAuthDetailsService that uses a remote
 * authentication server to return the jwt authentication token
 * 
 * @author Jonny
 */
@Service
@Profile(AuthProfiles.REMOTE_HMAC)
public class RemoteJwtAuthDetailsService implements JwtAuthDetailsService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${jwt.auth.protocol:http}") private String authenticationServerProtocol;
	@Value("${jwt.auth.host:localhost}") private String authenticationServerHost;	
	@Value("${jwt.auth.port:server.port}") private Integer authenticationServerPort;
	@Value("${jwt.auth.context}") private String authenticationServerContext;
	@Value("${jwt.auth.username}") private String authenticationUsername;
	@Value("${jwt.auth.password}") private String authenticationPassword;

	private AuthClient authClient = null;
	private String authToken;

	@PostConstruct
	public void initialize() {
		logger.info("Acquiring Authentication Token for Authentication Server Access");
		this.authClient = new AuthClient(
				authenticationServerProtocol,
				authenticationServerHost, 
				authenticationServerPort, 
				authenticationServerContext);		
		acquireValidToken();
	}
	
	private synchronized boolean acquireValidToken() {
		if (this.authToken != null) {
			/* ensure the token is not expired */
			JsonObject json = JwtTokenUtils.getClaims(this.authToken);
			Long exp = json.getLong("exp");
			/* if our token is less than 5 second from expiring, then throw it away */
			if (exp - System.currentTimeMillis() < TimeUnit.SECONDS.toMillis(5)) {
				this.authToken = null;
			}
		}
		
		if (this.authToken == null) {
			try {
				ResponseEntity<JwtToken> authResponse = authClient.acquireJwtToken(authenticationUsername, authenticationPassword);
				if (authResponse.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					logger.error("Invalid username/password for remote authentication token");
					return false;
				}
				Assert.isTrue(authResponse.getStatusCode().is2xxSuccessful(), authResponse.toString());
				this.authToken = authResponse.getBody().getToken();
			}
			catch(ResourceAccessException e) {
				logger.warn("Auth Server unavailable: " + e.getMessage());
			}
			catch(Exception e) {
				logger.error("Error acquiring auth token", e);
				return false;
			}
		}
		
		return true;
	}

	@Override
	public JwtAuthenticationToken buildAuthenticationToken(String token) {
		if (!acquireValidToken()) {
			throw new JwtException("Cannot authenticate with Auth Server");
		}
		ResponseEntity<AuthDetails> authDetails = authClient.validateJwtToken(token, authToken);
		Assert.isTrue(authDetails.getStatusCode().is2xxSuccessful(), authDetails.toString());
		Assert.isTrue(authDetails.getBody().isSuccessful(), authDetails.getBody().getFailureReason());
		return new JwtAuthenticationToken(
				new JwtAuthenticatedPrincipal(authDetails.getBody().getUsername()),
				null,
				authDetails.getBody().getGrantedAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}
}