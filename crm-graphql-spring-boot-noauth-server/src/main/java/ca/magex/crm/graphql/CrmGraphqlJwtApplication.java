package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ca.magex.crm.amnesia.services.AmnesiaAnonymousPolicies;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmValidation;
import ca.magex.crm.api.services.SecuredCrmServices;

@SpringBootApplication
@ComponentScan(basePackages = {"ca.magex.crm.amnesia", "ca.magex.crm.graphql", "ca.magex.crm.spring.jwt"})
public class CrmGraphqlJwtApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CrmGraphqlJwtApplication.class, args);
	}
		
	@Autowired private CrmLookupService lookupService;
	@Autowired private CrmValidation validationService;
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	
	@Bean
	public SecuredCrmServices organizations() {
		/* use anonymous policies */
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies();
		return new SecuredCrmServices(
				lookupService, validationService, 
				organizationService, policies, 
				locationService, policies,
				personService, policies);
	}
}
