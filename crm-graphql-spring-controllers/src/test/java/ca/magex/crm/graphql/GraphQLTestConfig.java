package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

@Configuration
@ComponentScan(basePackages = {
		"ca.magex.crm.graphql"
		})
public class GraphQLTestConfig implements CrmConfigurer {

	/* autowired services */
	@Autowired private CrmInitializationService initializationService;	
	@Autowired private CrmLookupService lookupService;	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	@Autowired private CrmPermissionService permissionService;
		
	@Bean
	@Primary
	@Override
	public Crm crm() {		
		return new Crm(
				initializationService, 
				lookupService, 
				permissionService, 
				organizationService, 
				locationService, 
				personService,
				userService, 
				crmPolicies());
	}
	
	@Bean
	@Override
	public CrmPolicies crmPolicies() {
		return new BasicPolicies(lookupService, permissionService, organizationService, locationService, personService, userService);
	}
}
