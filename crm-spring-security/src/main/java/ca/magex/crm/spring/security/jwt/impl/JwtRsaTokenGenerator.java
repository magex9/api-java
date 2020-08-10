package ca.magex.crm.spring.security.jwt.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.spring.security.jwt.JwtToken;
import ca.magex.crm.spring.security.jwt.JwtTokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Wraps the JWT libraries into a spring component to be used by the Authentication ServerThen 
 * 
 * @author Jonny
 */
@Component
@Profile(AuthProfiles.EMBEDDED_RSA)
public class JwtRsaTokenGenerator implements JwtTokenGenerator, Serializable {

	private static final long serialVersionUID = -3887579290326971481L;

	private Long expirationDuration;
	private Key key;

	public JwtRsaTokenGenerator(
			@Value("${jwt.expiration.hours:5}") Long expirationDuration,
			@Value("${jwt.rsa.jks}") String jksResource,
			@Value("${jwt.rsa.keypass}") String keypass) {
		this.expirationDuration = expirationDuration;
		InputStream jksStream = null;
		try {			
			if (jksResource.startsWith("classpath:")) {
				jksStream = getClass().getResourceAsStream("/" + jksResource.substring(10));
			}
			else if (jksResource.startsWith("file:")) {
				jksStream = new FileInputStream(jksResource.substring(5));
			}
			else {
				throw new RuntimeException("Unknown jksResource type: " + jksResource);
			}
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(jksStream, keypass.toCharArray());
			this.key = ks.getKey("crm", keypass.toCharArray());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (jksStream != null) {
					jksStream.close();
				}
			}
			catch(IOException ioe) {}
		}
	}

	public Long getExpirationDuration() {
		return TimeUnit.HOURS.toMillis(expirationDuration);
	}

	@Override
	public JwtToken generateToken(Authentication authentication) {
		String token = Jwts.builder()
				.setClaims(new HashMap<>())
				.setSubject(authentication.getName())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + getExpirationDuration()))
				.signWith(SignatureAlgorithm.RS512, key)
				.compact();
		return new JwtToken(token);
	}
}