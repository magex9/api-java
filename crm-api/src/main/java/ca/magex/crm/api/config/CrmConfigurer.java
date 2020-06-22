package ca.magex.crm.api.config;

import org.springframework.context.annotation.Bean;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordRepository;

/**
 * Used to configure the Crm Subsystem
 * 
 * @author Jonny
 */
public interface CrmConfigurer {

	@Bean
	public CrmPasswordRepository passwords();
	
	@Bean
	public Crm crm();

}
