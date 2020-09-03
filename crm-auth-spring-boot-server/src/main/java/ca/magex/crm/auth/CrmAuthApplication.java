package ca.magex.crm.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.auth",				// auth server configuration
		"ca.magex.crm.spring.security"		// security implementation
})
public class CrmAuthApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmAuthApplication.class);
		//app.setAdditionalProfiles("ut");
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
