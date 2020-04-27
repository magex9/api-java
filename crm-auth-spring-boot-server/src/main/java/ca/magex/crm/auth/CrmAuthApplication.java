package ca.magex.crm.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

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
}
