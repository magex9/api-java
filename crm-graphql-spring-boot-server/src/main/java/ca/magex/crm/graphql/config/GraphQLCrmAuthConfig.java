package ca.magex.crm.graphql.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.config.CrmConfigurerAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmAuthenticationService;

@Configuration
@Profile(MagexCrmProfiles.CRM_AUTH)
public class GraphQLCrmAuthConfig extends CrmConfigurerAdapter {	
	
	private CrmAuthenticationService authenticationService;
	
	@Autowired
	public void setAuthenticationService(CrmAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
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
		return new AuthenticatedPolicies(
				authenticationService,
				lookupService, 
				permissionService, 
				organizationService, 
				locationService, 
				personService, 
				userService);
	}
}