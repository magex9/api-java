package ca.magex.crm.spring.security.jwt.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import ca.magex.crm.spring.security.MagexSecurityProfile;
import ca.magex.crm.spring.security.jwt.JwtAuthDetailsService;
import ca.magex.crm.spring.security.jwt.internal.JwtAuthenticatedPrincipal;
import ca.magex.crm.spring.security.jwt.internal.JwtAuthenticationToken;
import ca.magex.crm.spring.security.jwt.internal.JwtTokenService;
import io.jsonwebtoken.JwtException;

/**
 * An implementation of the Jwt Auth Details Service that uses the local Users Service provided
 * by Spring to lookup the User Details and return an AUthentication Token for the user
 * 
 * @author Jonny
 */
@Service
@Profile(MagexSecurityProfile.EMBEDDED)
public class JwtAuthDetailsUserDetailsService implements JwtAuthDetailsService {

	@Autowired private UserDetailsService userDetailsService;
	@Autowired private JwtTokenService jwtTokenService;
	
	@Override
	public JwtAuthenticationToken getJwtAuthenticationTokenForUsername(String token) {
		String username = jwtTokenService.validateToken(token);
		
		if (username != null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			
			return new JwtAuthenticationToken(
					new JwtAuthenticatedPrincipal(userDetails.getUsername()),
					null,
					userDetails.getAuthorities());
		}
		else {
			throw new JwtException("Unable to retrieve username from token");
		}
	};
}