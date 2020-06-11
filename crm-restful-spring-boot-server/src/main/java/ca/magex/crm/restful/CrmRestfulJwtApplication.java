package ca.magex.crm.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmInitializationService;

@SpringBootApplication
@ComponentScan(basePackages = { 
		"ca.magex.crm.api",
		"ca.magex.crm.resource",
		"ca.magex.crm.amnesia", 
		"ca.magex.crm.restful", 
		"ca.magex.crm.transform", 
		"ca.magex.crm.spring.security"		
})
public class CrmRestfulJwtApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmRestfulJwtApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
	
	@Bean
	public ApplicationRunner dataInitializer() {
		return new ApplicationRunner() {			
			
			@Autowired CrmInitializationService initializationService;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				Logger LOG = LoggerFactory.getLogger(CrmRestfulJwtApplication.class);

				if (initializationService.isInitialized()) {
					return;
				}
				
				LOG.info("Initializing CRM System");
				initializationService.initializeSystem("Magex", new PersonName(null, "Admin", "", "Admin"), "admin@magex.ca", "sysadmin", "sysadmin");
			}
		};
	}
	
}