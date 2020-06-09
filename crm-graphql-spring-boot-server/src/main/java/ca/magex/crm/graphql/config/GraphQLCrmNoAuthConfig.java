package ca.magex.crm.graphql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.config.CrmConfigurerAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.services.Crm;

@Configuration
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class GraphQLCrmNoAuthConfig extends CrmConfigurerAdapter {	
		
	@Bean
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
		return new BasicPolicies(
				lookupService, 
				permissionService, 
				organizationService, 
				locationService, 
				personService, 
				userService);
	}
}