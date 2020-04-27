package ca.magex.crm.auth;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication(scanBasePackages = { 
		"ca.magex.crm.auth",
		"ca.magex.crm.spring.security.jwt" 
})
public class CrmAuthApplication {
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmAuthApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		return new InMemoryUserDetailsManager() {

			@Autowired
			private PasswordEncoder passwordEncoder;

			@PostConstruct
			public void init() {
				LoggerFactory.getLogger(CrmAuthApplication.class).info("Creating Default Users");
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
