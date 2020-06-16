package ca.magex.crm.springboot.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(1)
@Description("Defines the layers for the WebUI")
public class CrmUiWebSecurityConfig extends WebSecurityConfigurerAdapter {	

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity
			.authorizeRequests()
				.antMatchers("/", "/initialize").permitAll()
				.antMatchers("/home").hasAnyRole("CRM_USER", "CRM_ADMIN")
				.and()
			.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
			.logout()
				.permitAll();
	}
}
