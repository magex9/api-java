package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.services.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmValidation;
import ca.magex.crm.api.services.SecuredCrmServices;

@SpringBootApplication(scanBasePackages = { 
		"ca.magex.crm.amnesia", 
		"ca.magex.crm.hazelcast", 
		"ca.magex.crm.graphql", 
		"ca.magex.crm.spring.jwt" 
})
public class CrmGraphqlJwtApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmGraphqlJwtApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmValidation validationService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmOrganizationPolicy organizationPolicy;
	@Autowired private CrmLocationPolicy locationPolicy;
	@Autowired private CrmPersonPolicy personPolicy;

	@Bean
	public SecuredCrmServices crm() {
		/* use anonymous policies */
		return new SecuredCrmServices(
				lookupService, validationService,
				organizationService, organizationPolicy,
				locationService, locationPolicy,
				personService, personPolicy);
	}
}