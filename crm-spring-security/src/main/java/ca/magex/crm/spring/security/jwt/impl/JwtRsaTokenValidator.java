package ca.magex.crm.spring.security.jwt.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.spring.security.auth.AuthProfiles;
import ca.magex.crm.spring.security.jwt.JwtTokenDetails;
import ca.magex.crm.spring.security.jwt.JwtTokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * Wraps the JWT libraries into a spring component to be used by the Authentication ServerThen 
 * 
 * @author Jonny
 */
@Component
@Profile({AuthProfiles.EMBEDDED_RSA, AuthProfiles.REMOTE_RSA})
public class JwtRsaTokenValidator implements JwtTokenValidator, Serializable {

	private static final long serialVersionUID = -3887579290326971481L;

	private PublicKey publicKey;

	public JwtRsaTokenValidator(
			@Value("${jwt.expiration.hours:5}") Long expirationDuration,
			@Value("${jwt.rsa.cert}") String certResource,
			@Value("${jwt.rsa.keypass}") String keypass) {
		InputStream certStream = null;
		try {
			if (certResource.startsWith("classpath:")) {
				certStream = getClass().getResourceAsStream("/" + certResource.substring(10));
			}
			else if (certResource.startsWith("file:")) {
				certStream = new FileInputStream(certResource.substring(5));
			}
			else {
				throw new RuntimeException("Unknown certResource type: " + certResource);
			}
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(certStream);
			this.publicKey = cert.getPublicKey();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (certStream != null) {
					certStream.close();
				}
			}
			catch(IOException ioe) {}
		}
	}

	@Override
	public JwtTokenDetails validateToken(String token) {
		Jws<Claims> jws = Jwts.parser()
				.setSigningKey(publicKey)
				.parseClaimsJws(token);
		return new JwtTokenDetails(
				token,
				jws.getBody().getSubject(),
				jws.getBody().getExpiration());
	}
}