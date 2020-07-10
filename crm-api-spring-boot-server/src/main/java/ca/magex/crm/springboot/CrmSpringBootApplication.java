package ca.magex.crm.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",		
		//"ca.magex.crm.caching",
		//"ca.magex.crm.resource",
		//"ca.magex.crm.amnesia",		
		"ca.magex.crm.graphql",
		//"ca.magex.crm.restful",		
		"ca.magex.crm.springboot",
		"ca.magex.crm.spring.security",
		//"ca.magex.crm.transform", 
})
public class CrmSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmSpringBootApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}	
}