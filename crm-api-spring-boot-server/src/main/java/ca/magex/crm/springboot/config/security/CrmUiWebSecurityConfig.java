package ca.magex.crm.springboot.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@Order(1)
@Description("Defines the layers for the WebUI and the user details service")
public class CrmUiWebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		/*
		 * Only match the URL's associated with the Web UI 
		 * @formatter:off 
		 */
		httpSecurity
			.requestMatchers()
				.antMatchers( "/", "/initialize", "/home", "/login", "/logout", "/graphql/query")
				.and()
			.authorizeRequests()
				.antMatchers("/home", "/graphql/query").hasAnyRole("CRM_USER", "CRM_ADMIN")
				.antMatchers("/", "/initialize", "/login", "/logout").permitAll()
				.and()
			.formLogin()
				.loginPage("/login").permitAll()
				.and()
			.logout()
				.deleteCookies("JSESSIONID").invalidateHttpSession(true).logoutSuccessUrl("/").permitAll()
				.and()
			.sessionManagement()
				.maximumSessions(1).and().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
				.and()
			.csrf()
				.csrfTokenRepository(new CookieCsrfTokenRepository());
		// @formatter:on
	}
}