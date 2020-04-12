package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import ca.magex.crm.api.services.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonPolicy;
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
	@Autowired private CrmOrganizationPolicy organizationPolicy;
	@Autowired private CrmLocationPolicy locationPolicy;
	@Autowired private CrmPersonPolicy personPolicy;
	
	@Bean
	public SecuredCrmServices organizations() {
		/* use anonymous policies */
		return new SecuredCrmServices(
				lookupService, validationService, 
				organizationService, organizationPolicy, 
				locationService, locationPolicy,
				personService, personPolicy);
	}
}