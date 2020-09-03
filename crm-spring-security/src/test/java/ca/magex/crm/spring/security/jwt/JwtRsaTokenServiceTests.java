package ca.magex.crm.spring.security.jwt;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ca.magex.crm.spring.security.jwt.impl.JwtRsaTokenGenerator;
import ca.magex.crm.spring.security.jwt.impl.JwtRsaTokenValidator;

public class JwtRsaTokenServiceTests {
		

	@Test
	public void testGenerateValidateToken() {
		
		JwtRsaTokenGenerator tokenGenerator = new JwtRsaTokenGenerator(TimeUnit.DAYS.toMillis(1), "classpath:crm-dev.jks", "crm", "Crm2020!");
		JwtRsaTokenValidator tokenValidator = new JwtRsaTokenValidator(TimeUnit.DAYS.toMillis(1), "classpath:crm-dev.cer");

		Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
		JwtToken token = tokenGenerator.generateToken(auth);
	
		JwtTokenDetails details = tokenValidator.validateToken(token.getToken());
		System.out.println(details);
		
	}
}
