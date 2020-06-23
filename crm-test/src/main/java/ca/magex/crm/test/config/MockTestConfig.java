package ca.magex.crm.test.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

@Configuration
public class MockTestConfig {

	@Bean("PrincipalLookupService")
	public CrmLookupService mockLookupService() {
		return Mockito.mock(CrmLookupService.class);
	}

	@Bean("PrincipalGroupService")
	public CrmGroupService mockGroupService() {
		return Mockito.mock(CrmGroupService.class);
	}

	@Bean("PrincipalOrganizationService")
	public CrmOrganizationService mockOrganizationService() {
		return Mockito.mock(CrmOrganizationService.class);
	}

	@Bean("PrincipalLocationService")
	public CrmLocationService mockLocationService() {
		return Mockito.mock(CrmLocationService.class);
	}

	@Bean("PrincipalPersonService")
	public CrmPersonService mockPersonService() {
		return Mockito.mock(CrmPersonService.class);
	}

	@Bean("PrincipalUserService")
	public CrmUserService mockUserService() {
		return Mockito.mock(CrmUserService.class);
	}
}
