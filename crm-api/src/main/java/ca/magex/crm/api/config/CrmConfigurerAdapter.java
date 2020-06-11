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

	private CrmInitializationService initializationService;	
	private CrmLookupService lookupService;	
	private CrmOrganizationService organizationService;
	private CrmLocationService locationService;
	private CrmPersonService personService;
	private CrmUserService userService;
	private CrmPermissionService permissionService;
	
	@Autowired
	public void setInitializationService(CrmInitializationService initializationService) {
		this.initializationService = initializationService;
	}
	
	public CrmInitializationService getInitializationService() {
		return initializationService;
	}
	
	@Autowired
	public void setLookupService(CrmLookupService lookupService) {
		this.lookupService = lookupService;
	}
	
	public CrmLookupService getLookupService() {
		return lookupService;
	}
	
	@Autowired
	public void setOrganizationService(CrmOrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	public CrmOrganizationService getOrganizationService() {
		return organizationService;
	}
	
	@Autowired
	public void setLocationService(CrmLocationService locationService) {
		this.locationService = locationService;
	}
	
	public CrmLocationService getLocationService() {
		return locationService;
	}
	
	@Autowired
	public void setPersonService(CrmPersonService personService) {
		this.personService = personService;
	}
	
	public CrmPersonService getPersonService() {
		return personService;
	}
	
	@Autowired
	public void setUserService(CrmUserService userService) {
		this.userService = userService;
	}
	
	public CrmUserService getUserService() {
		return userService;
	}
	
	@Autowired
	public void setPermissionService(CrmPermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	public CrmPermissionService getPermissionService() {
		return permissionService;
	}
}