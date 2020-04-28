package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.amnesia.services.AmnesiaAnonymousPolicies;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.services.SecuredCrmServices;

@Configuration
public class SecuredCrmServicesFactoryBean {

	@Autowired private CrmLookupService lookupService;	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	
	@Bean
	public SecuredCrmServices securedCrmServices() throws Exception {
		AmnesiaAnonymousPolicies policies = new AmnesiaAnonymousPolicies();
		
		return new SecuredCrmServices(
				lookupService, 
				organizationService, policies,
				locationService, policies,
				personService, policies,
				userService, policies);
	}
}
