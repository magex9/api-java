package ca.magex.crm.auth;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
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
			
			@Autowired private PasswordEncoder passwordEncoder;
			
			@PostConstruct
			public void init() {
				
				this.createUser(new User(
						"admin", 
						passwordEncoder.encode("admin"), 
						Set.of(new SimpleGrantedAuthority("CRM_ADMIN"))
				));
				
				
				this.createUser(new User(
						"sysadmin", 
						passwordEncoder.encode("sysadmin"), 
						Set.of(
								new SimpleGrantedAuthority("CRM_ADMIN"), 
								new SimpleGrantedAuthority("SYS_ADMIN"))
				));
			}
		};
	}
}
