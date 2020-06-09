package ca.magex.crm.api.config;

import org.springframework.beans.factory.annotation.Autowired;

import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

/**
 * Adapter to hide the common components of the CrmConfigurer
 * 
 * @author Jonny
 */
public abstract class CrmConfigurerAdapter implements CrmConfigurer {

	protected CrmInitializationService initializationService;	
	protected CrmLookupService lookupService;	
	protected CrmOrganizationService organizationService;
	protected CrmLocationService locationService;
	protected CrmPersonService personService;
	protected CrmUserService userService;
	protected CrmPermissionService permissionService;
	
	@Autowired
	public void setInitializationService(CrmInitializationService initializationService) {
		this.initializationService = initializationService;
	}
	
	@Autowired
	public void setLookupService(CrmLookupService lookupService) {
		this.lookupService = lookupService;
	}
	
	@Autowired
	public void setOrganizationService(CrmOrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	@Autowired
	public void setLocationService(CrmLocationService locationService) {
		this.locationService = locationService;
	}
	
	@Autowired
	public void setPersonService(CrmPersonService personService) {
		this.personService = personService;
	}
	
	@Autowired
	public void setUserService(CrmUserService userService) {
		this.userService = userService;
	}
	
	@Autowired
	public void setPermissionService(CrmPermissionService permissionService) {
		this.permissionService = permissionService;
	}
}