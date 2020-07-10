package ca.magex.crm.spring.security.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@Order(20)
@Description("Defines the Roles required for accessing the actuator endpoints")
public class ActuatorSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired private JwtRequestFilter jwtRequestFilter;

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * Only match the URL's associated with the actuator
		 * Setup the authentication entry point to simply return an UnAuthorized http code 
		 * @formatter:off 
		 */
		httpSecurity
			.requestMatchers()
				.antMatchers("/actuator/**")
				.and()
			.authorizeRequests()
				.antMatchers("/actuator/shutdown").hasRole("SYS_ADMIN")
				.antMatchers("/actuator/*").hasAnyRole("SYS_ADMIN", "APP_ADMIN")
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