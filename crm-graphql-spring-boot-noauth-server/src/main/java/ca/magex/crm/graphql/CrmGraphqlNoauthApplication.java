package ca.magex.crm.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ca.magex.crm.amnesia", "ca.magex.crm.graphql"})
public class CrmGraphqlNoauthApplication {
	
	public static void main(String[] args) {
		
		SpringApplication app = new SpringApplication(CrmGraphqlNoauthApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
