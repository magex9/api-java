package ca.magex.crm.spring.security.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@Order(15)
@Description("Defines the Roles required for accessing the authentication endpoints")
@Profile(CrmProfiles.AUTH_EMBEDDED_JWT)
public class JwtSecurityConfiguration extends WebSecurityConfigurerAdapter {	

	@Autowired private JwtRequestFilter jwtRequestFilter;
	@Autowired private UserDetailsService jwtUserDetailsService;
	@Autowired private UserDetailsPasswordService jwtUserDetailsPasswordService;
	@Autowired private PasswordEncoder passwordEncoder;
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService)
				.passwordEncoder(passwordEncoder)
				.userDetailsPasswordManager(jwtUserDetailsPasswordService);
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * Only match the URL's associated with the authentication API 
		 * Setup the authentication entry point to simply return an UnAuthorized http code
		 * @formatter:off 
		 */
		httpSecurity
			.requestMatchers()
				.antMatchers("/authenticate", "/validate", "/auth")
				.and()
			.authorizeRequests()
				.antMatchers("/authenticate", "/auth").permitAll()
				.antMatchers("/validate").hasAnyRole("APP_AUTH_REQUEST", "SYS_ADMIN")
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
