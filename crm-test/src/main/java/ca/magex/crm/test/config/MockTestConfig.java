package ca.magex.crm.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.services.CrmUserService;

@Configuration
public class MockTestConfig {

	@Bean
	public CrmLookupService mockLookupService() {
		return Mockito.mock(CrmLookupService.class);
	}
	
	@Bean
	public CrmOptionService mockOptionService() {
		return Mockito.mock(CrmOptionService.class);
	}

	@Bean
	public CrmGroupService mockGroupService() {
		return Mockito.mock(CrmGroupService.class);
	}
	
	@Bean
	public CrmRoleService mockRoleService() {
		return Mockito.mock(CrmRoleService.class);
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
}
