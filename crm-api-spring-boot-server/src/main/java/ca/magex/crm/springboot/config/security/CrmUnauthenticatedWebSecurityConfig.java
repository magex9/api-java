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
@Order(2)
@Description("Defines the access for an unauthenticated CRM Server")
@Profile(CrmProfiles.BASIC_NO_AUTH)
public class CrmUnauthenticatedWebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] URLS = {
			"/graphql",
			"/graphql/schema",
			"/rest",
			"/rest/**",
			"/crm.yaml",
			"/swagger-ui-bundle.js",
			"/swagger-ui.css",
			"/favicon.ico"
	};

	@Autowired private JwtRequestFilter jwtRequestFilter;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * Only match the URL's associated with the end points and the swagger pages
		 * Setup the authentication entry point to simply return an UnAuthorized http code
		 * @formatter:off 
		 */
		httpSecurity
			.requestMatchers()
				.antMatchers(URLS)
				.and()			
			.authorizeRequests()
				.antMatchers(URLS).permitAll()		
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.csrf()
				.disable()
			.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		// @formatter:on
	}
}