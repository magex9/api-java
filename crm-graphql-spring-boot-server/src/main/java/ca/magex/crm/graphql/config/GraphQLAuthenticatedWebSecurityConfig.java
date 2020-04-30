package ca.magex.crm.graphql.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.spring.security.jwt.JwtRequestFilter;

@Configuration
@Order(3)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile(MagexCrmProfiles.CRM_AUTH)
public class GraphQLAuthenticatedWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired private JwtRequestFilter jwtRequestFilter;
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/* 
		 * graphql api endpoints 
		 * @formatter:off 
		 */
		httpSecurity.authorizeRequests()
				.antMatchers("/graphql", "/").permitAll()		
			.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf().disable();
//			.csrf().csrfTokenRepository(new CookieCsrfTokenRepository());	
	}
}