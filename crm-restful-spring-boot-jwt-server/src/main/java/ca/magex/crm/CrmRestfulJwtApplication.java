package ca.magex.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { 
		"ca.magex.crm.api", 
		"ca.magex.crm.amnesia", 
		"ca.magex.crm.rest", 
		"ca.magex.crm.spring.jwt",
		"ca.magex.crm.resource" 
})
public class CrmRestfulJwtApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmRestfulJwtApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
	
}