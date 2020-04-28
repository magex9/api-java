package ca.magex.crm.graphql.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile("NOAUTH")
public class UnauthenticatedWebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable()
				/* get the list of public resources */
				.authorizeRequests().antMatchers("/,/graphql").permitAll()
				/* actuator needs to be protected */
//				.and().authorizeRequests()	
					.antMatchers("/actuator/shutdown").hasRole("SYS_ADMIN")
					.antMatchers("/actuator/*").hasAnyRole("SYS_ADMIN", "APP_ADMIN")
				/* any other requests require authentication */
//				.and().authorizeRequests().anyRequest().authenticated()
				.and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}