package ca.magex.crm.graphql.configuration;

import org.checkerframework.checker.units.qual.C;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class Oauth2WebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests(a -> a
						.antMatchers("/", "/error", "/webjars/**").permitAll()
						.anyRequest().authenticated())
				.exceptionHandling(e -> e
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.logout(l -> l
						.logoutSuccessUrl("/").permitAll())
				.csrf(c -> c
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.oauth2Login();
	}
}
