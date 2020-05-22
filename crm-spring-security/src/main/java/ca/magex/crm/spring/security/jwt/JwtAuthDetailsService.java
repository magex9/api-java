package ca.magex.crm.spring.security.jwt;

/**
 * Responsible for retrieving the JwtAuthenticationToken for the given username
 * 
 * @author Jonny
 */
public interface JwtAuthDetailsService {

	public JwtAuthenticationToken getJwtAuthenticationTokenForUsername(String token);
}
