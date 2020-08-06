package ca.magex.crm.springboot.config.crm;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.common.PersonName;

@Configuration
@Profile(CrmProfiles.DEV)
@Description("Initializes the server with some dummy dev data")
public class DevInitializerConfig {

	@Autowired
	private Crm crm;

	@PostConstruct
	public void initialize() {
		LoggerFactory.getLogger(DevInitializerConfig.class).info("Initializing CRM System for Dev");
		crm.initializeSystem("System", new PersonName(null, "System", null, "Admin"), "root@localhost", "admin", "admin");
	}	
}