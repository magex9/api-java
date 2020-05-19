package ca.magex.crm.spring.security.jwt.impl;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.spring.security.auth.AuthClient;
import ca.magex.crm.spring.security.auth.AuthDetails;
import ca.magex.crm.spring.security.jwt.JwtAuthDetailsService;
import ca.magex.crm.spring.security.jwt.JwtAuthenticatedPrincipal;
import ca.magex.crm.spring.security.jwt.JwtAuthenticationToken;
import ca.magex.crm.spring.security.jwt.JwtToken;

/**
 * An implementation of the JwtAuthDetailsService that uses a remote
 * authentication server to return the jwt authentication token
 * 
 * @author Jonny
 */
@Service
@Profile(MagexCrmProfiles.AUTH_REMOTE_JWT)
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
		ResponseEntity<JwtToken> authResponse = authClient.acquireJwtToken(authenticationUsername, authenticationPassword);
		Assert.isTrue(authResponse.getStatusCode().is2xxSuccessful(), authResponse.toString());
		this.authToken = authResponse.getBody().getToken();
	}

	@Override
	public JwtAuthenticationToken getJwtAuthenticationTokenForUsername(String token) {
		ResponseEntity<AuthDetails> authDetails = authClient.validateJwtToken(token, authToken);
		 // TODO generate a graceful failure here
		Assert.isTrue(authDetails.getStatusCode().is2xxSuccessful(), authDetails.toString());
		Assert.isTrue(authDetails.getBody().isSuccessful(), authDetails.getBody().getFailureReason());
		return new JwtAuthenticationToken(
				new JwtAuthenticatedPrincipal(authDetails.getBody().getUsername()),
				null,
				authDetails.getBody().getGrantedAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}
}