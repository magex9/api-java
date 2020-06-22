package ca.magex.crm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmInitializationService;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",					// Generic CRM beans 
		"ca.magex.crm.auth",				// auth server configuration
		"ca.magex.crm.resource", 			// lookup data
		"ca.magex.crm.amnesia",				// crm implementation
		"ca.magex.crm.hazelcast",			// crm implementation
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
	
	@Bean
	public ApplicationRunner dataInitializer() {
		return new ApplicationRunner() {			
			
			@Autowired CrmInitializationService initializationService;
			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				Logger LOG = LoggerFactory.getLogger(CrmAuthApplication.class);

				if (initializationService.isInitialized()) {
					return;
				}
				
				LOG.info("Initializing CRM System");
				initializationService.initializeSystem("Magex", new PersonName(null, "Admin", "", "Admin"), "admin@magex.ca", "sysadmin", "sysadmin");
			}
		};
	}
}
