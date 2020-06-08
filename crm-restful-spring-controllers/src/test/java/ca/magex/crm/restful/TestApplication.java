package ca.magex.crm.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",
		"ca.magex.crm.resource",
		"ca.magex.crm.amnesia",
		"ca.magex.crm.restful",
		"ca.magex.crm.transform",
})
public class TestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);		
	}
	
	@Bean 
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	};
}