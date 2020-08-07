package ca.magex.crm.spring.security.jwt;

import org.springframework.security.core.Authentication;

public interface JwtTokenGenerator {

	/**
	 * generates a token representing the given authentication instance
	 * @param authentication
	 * @return
	 */
	public JwtToken generateToken(Authentication authentication);
}
