package ca.magex.crm.spring.security.jwt;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.ExpiredJwtException;

public class JwtTokenServiceTest {
	
	@Test
	public void testJwtTokenGeneration() {
				
		JwtTokenService tokenService = new JwtTokenService(5L, "YouSmellLikeDirtySocks");		
		Authentication auth = new JwtAuthenticationToken(new JwtAuthenticatedPrincipal("Smith"));		
		String token = tokenService.generateToken(auth);
		Assert.assertNotNull(token);
		
		String username = tokenService.validateToken(token);
		Assert.assertEquals("Smith", username);
	
		Date expiration = tokenService.getExpiration(token);
		Assert.assertFalse(expiration.after(new Date(System.currentTimeMillis() + tokenService.getExpirationDuration())));
	}
	
	@Test
	public void testExpiredJwtToken() {
		JwtTokenService tokenService = new JwtTokenService(-5L, "ExpiredMilk");		
		Authentication auth = new JwtAuthenticationToken(new JwtAuthenticatedPrincipal("Smith"));		
		String token = tokenService.generateToken(auth);
		Assert.assertNotNull(token);
		
		try {
			tokenService.validateToken(token);
			Assert.fail("Token should be expired");
		}
		catch(ExpiredJwtException e) {
		}
	}
}
