package ca.magex.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.services.AmnesiaAnonymousPolicies;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.services.SecuredCrmServices;

@SpringBootApplication
@ComponentScan(basePackages = {"ca.magex.crm.amnesia", "ca.magex.crm.rest", "ca.magex.crm.resource"})
public class NoAuthRestfulApiServer {

	public static void main(String[] args) {
		SpringApplication.run(NoAuthRestfulApiServer.class, args);
	}

	@Autowired private AmnesiaDB db;
	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	
	@Bean
	public SecuredCrmServices organizations() {
		/* use anonymous policies */
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies(db);
		return new SecuredCrmServices(
				lookupService, 
				organizationService, policies, 
				locationService, policies,
				personService, policies,
				userService, policies);
	}

}
