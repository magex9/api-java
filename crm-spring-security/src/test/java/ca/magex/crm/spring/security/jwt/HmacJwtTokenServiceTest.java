package ca.magex.crm.spring.security.jwt;

import java.util.Base64;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import ca.magex.crm.spring.security.jwt.impl.JwtHmacTokenService;
import io.jsonwebtoken.ExpiredJwtException;

public class HmacJwtTokenServiceTest {
	
	@Test
	public void testJwtTokenGeneration() {
				
		JwtHmacTokenService tokenService = new JwtHmacTokenService(5L, "YouSmellLikeDirtySocks");		
		Authentication auth = new JwtAuthenticationToken(new JwtAuthenticatedPrincipal("Smith"));		
		JwtToken token = tokenService.generateToken(auth);
		Assert.assertNotNull(token);
		
		JwtTokenDetails details = tokenService.validateToken(token.getToken());
		Assert.assertEquals("Smith", details.getUsername());
	
		Assert.assertFalse(details.getExpiration().after(new Date(System.currentTimeMillis() + tokenService.getExpirationDuration())));
	}
	
	@Test
	public void testExpiredJwtToken() {
		JwtHmacTokenService tokenService = new JwtHmacTokenService(-5L, "ExpiredMilk");		
		Authentication auth = new JwtAuthenticationToken(new JwtAuthenticatedPrincipal("Smith"));		
		JwtToken token = tokenService.generateToken(auth);
		Assert.assertNotNull(token);
		
		try {
			tokenService.validateToken(token.getToken());
			Assert.fail("Token should be expired");
		}
		catch(ExpiredJwtException e) {
		}
		
		String[] components = token.getToken().split("\\.");
		String part1 = new String(Base64.getDecoder().decode(components[0]));
		System.out.println(part1);
		String part2 = new String(Base64.getDecoder().decode(components[1]));
		System.out.println(part2);
		

	}
}
