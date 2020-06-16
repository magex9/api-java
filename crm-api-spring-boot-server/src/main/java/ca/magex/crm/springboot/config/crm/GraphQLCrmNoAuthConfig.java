package ca.magex.crm.springboot.config.crm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.caching.config.CrmCachingConfigurerAdapter;

@Configuration
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
@Description("Configures the CRM by adding caching support, and using the Basic Policies for CRM Processing")
public class GraphQLCrmNoAuthConfig extends CrmCachingConfigurerAdapter {	
		
	@Bean
	@Primary
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
	@Primary
	@Override
	public CrmPolicies crmPolicies() {
		return new BasicPolicies(
				getLookupService(), 
				getPermissionService(), 
				getOrganizationService(), 
				getLocationService(), 
				getPersonService(), 
				getUserService());
	}
}