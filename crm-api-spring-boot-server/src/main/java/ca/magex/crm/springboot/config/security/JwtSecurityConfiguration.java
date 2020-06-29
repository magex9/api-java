package ca.magex.crm.springboot.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@Order(3)
@Description("Defines the Roles required for accessing the authentication endpoints")
@Profile(CrmProfiles.AUTH_EMBEDDED_JWT)
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter {	

	@Autowired private JwtRequestFilter jwtRequestFilter;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity.authorizeRequests()
				.antMatchers("/authenticate").permitAll()
				.antMatchers("/validate").hasRole("AUTH_REQUEST")
			.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf().disable();
//			.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());
	}
}
