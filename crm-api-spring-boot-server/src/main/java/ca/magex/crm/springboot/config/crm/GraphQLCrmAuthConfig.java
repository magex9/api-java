package ca.magex.crm.springboot.config.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.authenticated.AuthenticatedPolicies;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.caching.config.CrmCachingConfigurerAdapter;

@Configuration
@Profile(MagexCrmProfiles.CRM_AUTH)
@Description("Configures the CRM by adding caching support, and using the Authenticated Policies for CRM Processing")
public class GraphQLCrmAuthConfig extends CrmCachingConfigurerAdapter {	
	
	private CrmAuthenticationService authenticationService;
	
	@Autowired
	public void setAuthenticationService(CrmAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	@Bean
	@Override
	public Crm crm() {
		return new Crm(
				getInitializationService(), 
				getLookupService(), 
				getPermissionService(), 
				getOrganizationService(), 
				getLocationService(), 
				getPersonService(),
				getUserService(), 
				crmPolicies());
	}
	
	@Bean
	@Override
	public CrmPolicies crmPolicies() {		
		return new AuthenticatedPolicies(
				authenticationService,
				getLookupService(), 
				getPermissionService(), 
				getOrganizationService(), 
				getLocationService(), 
				getPersonService(), 
				getUserService());
	}
}