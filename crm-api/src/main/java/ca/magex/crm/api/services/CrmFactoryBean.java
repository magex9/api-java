package ca.magex.crm.api.services;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.CrmOrganizationPolicy;
import ca.magex.crm.api.policies.CrmPermissionPolicy;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.policies.CrmUserPolicy;

@Component
public class CrmFactoryBean implements FactoryBean<Crm> {

	/* autowired services */
	@Autowired private CrmInitializationService initializationService;	
	@Autowired private CrmLookupService lookupService;	
	@Autowired private CrmOrganizationService organizationService;
	@Autowired private CrmLocationService locationService;
	@Autowired private CrmPersonService personService;
	@Autowired private CrmUserService userService;
	@Autowired private CrmPermissionService permissionService;
	
	/* autowired policies */
	@Autowired private CrmLookupPolicy lookupPolicy;
	@Autowired private CrmOrganizationPolicy organizationPolicy;
	@Autowired private CrmLocationPolicy locationPolicy;
	@Autowired private CrmPersonPolicy personPolicy;
	@Autowired private CrmUserPolicy userPolicy;
	@Autowired private CrmPermissionPolicy permissionPolicy;
	
	@Override
	public Crm getObject() throws Exception {
		return new Crm(
			initializationService, 
			lookupService, lookupPolicy,
			permissionService, permissionPolicy, 
			organizationService, organizationPolicy,
			locationService, locationPolicy,
			personService, personPolicy,
			userService, userPolicy);
	}
	
	@Override
	public Class<?> getObjectType() {
		return Crm.class;
	}
}
