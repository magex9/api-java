package ca.magex.crm.spring.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Wraps the JWT libraries into a spring component to be used by the Authentication ServerThen 
 * 
 * @author Jonny
 */
@Component
@Profile(MagexCrmProfiles.AUTH_EMBEDDED_JWT)
public class JwtTokenService implements Serializable {

	private static final long serialVersionUID = -3887579290326971481L;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	@Value("${jwt.expiration.hours:5}") private Long expiration;
	@Value("${jwt.secret}") private String secret;

	/**
	 * Generates a new Token for the given authentication
	 * @param authentication
	 * @return
	 */
	public String generateToken(Authentication authentication) {
		return Jwts.builder()
				.setClaims(new HashMap<>())
				.setSubject(authentication.getName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expiration)))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	/**
	 * validates the token and returns the user
	 * 
	 * @param token
	 * @return
	 */
	public String validateToken(String token) {
		Jws<Claims> jws = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token);

		/* if we have an expiration which is before now then return null */
		if (jws.getBody().getExpiration() != null && jws.getBody().getExpiration().before(new Date(System.currentTimeMillis()))) {
			throw new ExpiredJwtException(jws.getHeader(), jws.getBody(), "Token expired");
		} else {
			return jws.getBody().getSubject();
		}
	}
	
	/**
	 * validates the token and returns the user
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpiration(String token) {
		Jws<Claims> jws = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token);
		/* if we have an expiration which is before now then return null */
		return jws.getBody().getExpiration();
	}
}