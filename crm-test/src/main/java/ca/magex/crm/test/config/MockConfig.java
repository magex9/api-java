package ca.magex.crm.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

@Configuration
public class MockConfig {

	@Bean
	@Primary	
	public CrmLookupService mockLookupService() {
		return Mockito.mock(CrmLookupService.class);
	}
	
	@Bean
	@Primary
	public CrmPermissionService mockPermissionService() {
		return Mockito.mock(CrmPermissionService.class);
	}
	
	@Bean
	@Primary
	public CrmOrganizationService mockOrganizationService() {
		return Mockito.mock(CrmOrganizationService.class);
	}
	
	@Bean
	@Primary
	public CrmLocationService mockLocationService() {
		return Mockito.mock(CrmLocationService.class);
	}
	
	@Bean
	@Primary
	public CrmPersonService mockPersonService() {
		return Mockito.mock(CrmPersonService.class);
	}
	
	@Bean
	@Primary
	public CrmUserService mockUserService() {
		return Mockito.mock(CrmUserService.class);
	}
}
