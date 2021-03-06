package ca.magex.crm.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

@Configuration
public class MockTestConfig {

	@Bean
	public CrmOptionService mockOptionService() {
		return Mockito.mock(CrmOptionService.class);
	}

	@Bean
	public CrmOrganizationService mockOrganizationService() {
		return Mockito.mock(CrmOrganizationService.class);
	}

	@Bean
	public CrmLocationService mockLocationService() {
		return Mockito.mock(CrmLocationService.class);
	}

	@Bean
	public CrmPersonService mockPersonService() {
		return Mockito.mock(CrmPersonService.class);
	}

	@Bean
	public CrmUserService mockUserService() {
		return Mockito.mock(CrmUserService.class);
	}
	
	@Bean
	public CrmPasswordService mockPasswordService() {
		return Mockito.mock(CrmPasswordService.class);
	}
}
