package ca.magex.crm.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",
		"ca.magex.crm.resource",		
		"ca.magex.crm.amnesia",
		"ca.magex.crm.hazelcast",
		"ca.magex.crm.graphql",
		"ca.magex.crm.spring.security",
		"ca.magex.crm.policy"
})
public class CrmGraphqlJwtApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmGraphqlJwtApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}	
}