package ca.magex.crm.spring.security.jwt.config;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import ca.magex.crm.spring.security.MagexSecurityProfile;

@Configuration
@Profile(MagexSecurityProfile.EMBEDDED_JWT)
public class JwtConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		return new InMemoryUserDetailsManager() {

			@Autowired private PasswordEncoder passwordEncoder;

			@PostConstruct
			public void init() {
				LoggerFactory.getLogger(InMemoryUserDetailsManager.class).info("Creating Default Users");
				this.createUser(new User(
						"admin",
						passwordEncoder.encode("admin"),
						Set.of(new SimpleGrantedAuthority("ROLE_CRM_ADMIN"))));

				this.createUser(new User(
						"sysadmin",
						passwordEncoder.encode("sysadmin"),
						Set.of(
								new SimpleGrantedAuthority("ROLE_CRM_ADMIN"),
								new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))));

				this.createUser(new User(
						"app_crm",
						passwordEncoder.encode("NutritionFactsPer1Can"),
						Set.of(
								new SimpleGrantedAuthority("ROLE_AUTH_REQUEST"))));
			}
		};
	}
}
