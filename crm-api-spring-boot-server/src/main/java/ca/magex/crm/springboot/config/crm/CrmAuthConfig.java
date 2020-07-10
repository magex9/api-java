package ca.magex.crm.springboot.config.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.config.CrmConfigurer;

@Configuration
@Profile(CrmProfiles.CRM_AUTH)
@Description("Configures the CRM by adding caching support, and using the Authenticated Policies for CRM Processing")
public class CrmAuthConfig implements CrmConfigurer {	
	
	private CrmAuthenticationService authenticationService;
	
	@Autowired
	public void setAuthenticationService(CrmAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	@Bean
	@Override
	public Crm crm() {
		return null;
//		return new Crm(
//				getInitializationService(), 
//				getLookupService(), 
//				getPermissionService(), 
//				getOrganizationService(), 
//				getLocationService(), 
//				getPersonService(),
//				getUserService(), 
//				crmPolicies());
	}
}