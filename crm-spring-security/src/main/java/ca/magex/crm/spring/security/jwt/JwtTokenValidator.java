package ca.magex.crm.spring.security.jwt;

public interface JwtTokenValidator {

	/**
	 * Validates the token and returns the details from the token
	 * @param token
	 * @return
	 */
	public JwtTokenDetails validateToken(String token);
}
