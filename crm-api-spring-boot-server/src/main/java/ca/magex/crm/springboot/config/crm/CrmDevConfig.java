package ca.magex.crm.springboot.config.crm;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.PersonName;

@Configuration
@Profile("Dev")
@Description("Initializes the server with some dummy dev data")
public class CrmDevConfig {

	@Autowired
	private Crm crm;

	@PostConstruct
	public void initialize() {
		crm.initializeSystem("System", new PersonName(null, "System", null, "Admin"), "root@localhost", "admin", "admin");
	}
	
}
