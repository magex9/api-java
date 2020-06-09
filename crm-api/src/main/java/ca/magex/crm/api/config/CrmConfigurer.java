package ca.magex.crm.api.config;

import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.Crm;

/**
 * Used to configure the Crm Subsystem
 * 
 * @author Jonny
 */
public interface CrmConfigurer {

	@Bean
	public Crm crm();
	
	@Bean
	public CrmPolicies crmPolicies();
}
