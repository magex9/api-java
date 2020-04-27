package ca.magex.crm.spring.security.jwt;

import ca.magex.crm.spring.security.jwt.internal.JwtAuthenticationToken;

/**
 * Responsible for retrieving the JwtAuthenticationToken for the given username
 * 
 * @author Jonny
 */
public interface JwtAuthDetailsService {

	public JwtAuthenticationToken getJwtAuthenticationTokenForUsername(String token);
}
