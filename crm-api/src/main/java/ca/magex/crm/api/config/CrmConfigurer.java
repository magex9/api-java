package ca.magex.crm.api.config;

import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordService;

/**
 * Used to configure the Crm Subsystem
 * 
 * @author Jonny
 */
public interface CrmConfigurer {

	@Bean
	public CrmPasswordService passwords();
	
	@Bean
	public Crm crm();

}
