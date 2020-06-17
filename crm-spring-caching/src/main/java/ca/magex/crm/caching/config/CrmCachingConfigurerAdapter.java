package ca.magex.crm.caching.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ca.magex.crm.api.config.CrmConfigurerAdapter;
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
public abstract class CrmCachingConfigurerAdapter extends CrmConfigurerAdapter {

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
	
	@Override
	public CrmInitializationService getInitializationService() {
		return initializationService;
	}
	
	@Autowired
	public void setLookupService(CrmLookupService lookupService) {
		this.lookupService = lookupService;
	}
	
	@Override
	public CrmLookupService getLookupService() {
		return lookupService;
	}
	
	@Autowired
	@Qualifier("CrmOrganizationServiceCachingDelegate")
	public void setOrganizationService(CrmOrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	public CrmOrganizationService getOrganizationService() {
		return organizationService;
	}
	
	@Autowired
	@Qualifier("CrmLocationServiceCachingDelegate")
	public void setLocationService(CrmLocationService locationService) {
		this.locationService = locationService;
	}
	
	@Override
	public CrmLocationService getLocationService() {
		return locationService;
	}
	
	@Autowired
	@Qualifier("CrmPersonServiceCachingDelegate")
	public void setPersonService(CrmPersonService personService) {
		this.personService = personService;
	}
	
	@Override
	public CrmPersonService getPersonService() {
		return personService;
	}
	
	@Autowired
	@Qualifier("CrmUserServiceCachingDelegate")
	public void setUserService(CrmUserService userService) {
		this.userService = userService;
	}
	
	@Override
	public CrmUserService getUserService() {
		return userService;
	}
	
	@Autowired
	@Qualifier("CrmPermissionServiceCachingDelegate")
	public void setPermissionService(CrmPermissionService permissionService) {
		this.permissionService = permissionService;
	}
	
	@Override
	public CrmPermissionService getPermissionService() {
		return permissionService;
	}
}