package ca.magex.crm.spring.security.jwt.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import ca.magex.crm.spring.security.AuthClient;
import ca.magex.crm.spring.security.jwt.JwtAuthDetailsService;
import ca.magex.crm.spring.security.jwt.JwtAuthenticatedPrincipal;
import ca.magex.crm.spring.security.jwt.JwtAuthenticationToken;
import io.jsonwebtoken.JwtException;

/**
 * An implementation of the JwtAuthDetailsService that uses a remote authentication server
 * to return the jwt authentication token
 * 
 * @author Jonny
 */
public class JwtAuthDetailsRemoteService implements JwtAuthDetailsService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${jwt.auth.host:localhost}") String authenticationServerHost;
	@Value("${jwt.auth.port}") Integer authenticationServerPort;
	@Value("${jwt.auth.username}") String authenticationUsername;
	@Value("${jwt.auth.password}") String authenticationPassword;
	
	private AuthClient authClient = null;
	private String authToken;
	
	@PostConstruct
	public void initialize() throws JSONException {
		logger.info("Acquiring Authentication Token for Authentication Server Access");
		this.authClient = new AuthClient("http://" + authenticationServerHost + ":" + authenticationServerPort);
		JSONObject authResponse = authClient.acquireJwtToken(authenticationUsername, authenticationPassword);
		Assert.isTrue(authResponse.getBoolean("valid"), authResponse.getString("status"));
		this.authToken = authResponse.getString("token");
	}

	@Override
	public JwtAuthenticationToken getJwtAuthenticationTokenForUsername(String token) {
		try {
			JSONObject tokenDetailsResponse = authClient.validateToken(token, authToken);
			Assert.isTrue(tokenDetailsResponse.getBoolean("valid"), tokenDetailsResponse.getString("status"));
			String username = tokenDetailsResponse.getString("username");
			
			JSONObject authDetailsResponse = authClient.getAuthDetails(username, authToken);
			Assert.isTrue(authDetailsResponse.getBoolean("valid"), authDetailsResponse.getString("status"));
			Assert.isTrue(StringUtils.equals(username, authDetailsResponse.getString("username")), "authorized username does not match requested username");
			
			List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
			JSONArray grantedAuthoritiesArray = authDetailsResponse.getJSONArray("grantedAuthorities");
			for (int i=0; i<grantedAuthoritiesArray.length(); i++) {
				grantedAuthorities.add(new SimpleGrantedAuthority(grantedAuthoritiesArray.getJSONObject(i).getString("authority")));
			}
						
			return new JwtAuthenticationToken(
					new JwtAuthenticatedPrincipal(username),
					null,
					grantedAuthorities);
		}
		catch(Exception e) {
			throw new JwtException("Error retrieving auth details from remove auth server", e);
		}
	}
}
