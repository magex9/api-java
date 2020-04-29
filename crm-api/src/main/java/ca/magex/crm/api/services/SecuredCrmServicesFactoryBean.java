package ca.magex.crm.api.services;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.services.SecuredCrmServices;

@Configuration
public class SecuredCrmServicesFactoryBean implements FactoryBean<SecuredCrmServices> {

	/* autowired services */
	@Autowired private CrmLookupService lookupService;	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	
	/* autowired policies */
	@Autowired private CrmOrganizationPolicy organizationPolicy;
	@Autowired private CrmLocationPolicy locationPolicy;
	@Autowired private CrmPersonPolicy personPolicy;
	@Autowired private CrmUserPolicy userPolicy;
	
	
	@Override
	public SecuredCrmServices getObject() throws Exception {
		return new SecuredCrmServices(
				lookupService, 
				organizationService, organizationPolicy,
				locationService, locationPolicy,
				personService, personPolicy,
				userService, userPolicy);
	}
	
	@Override
	public Class<?> getObjectType() {
		return SecuredCrmServices.class;
	}
}
