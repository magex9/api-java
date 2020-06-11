package ca.magex.crm.api.policies.authenticated;

import static ca.magex.crm.api.services.CrmAuthenticationService.CRM_ADMIN;
import static ca.magex.crm.api.services.CrmAuthenticationService.ORG_ADMIN;

import ca.magex.crm.api.policies.CrmLocationPolicy;
import ca.magex.crm.api.policies.basic.BasicLocationPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;

public class AuthenticatedLocationPolicy implements CrmLocationPolicy {
	
	private CrmAuthenticationService auth;

	private CrmLocationPolicy delegate;
	
	private CrmLocationService locations;

	/**
	 * Authenticated Location Policy handles roles and association checks required for policy approval
	 * 
	 * @param auth
	 * @param organizations
	 * @param locations
	 * @param userService
	 */
	public AuthenticatedLocationPolicy(
			CrmAuthenticationService auth,
			CrmOrganizationService organizations,
			CrmLocationService locations) {
		this.auth = auth;
		this.locations = locations;
		this.delegate = new BasicLocationPolicy(organizations, locations);
		
	}

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		if (!delegate.canCreateLocationForOrganization(organizationId)) {
			return false;
		}
		/* if the user is a CRM Admin then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* if the current user is associated with the organization, then return true if they are an RE Admin */
		if (auth.getOrganizationId().equals(organizationId)) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		if (!delegate.canViewLocation(locationId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with */
		return auth.getOrganizationId().equals(locations.findLocationSummary(locationId).getOrganizationId());
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		if (!delegate.canUpdateLocation(locationId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (auth.getOrganizationId().equals(locations.findLocationSummary(locationId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		if (!delegate.canEnableLocation(locationId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (auth.getOrganizationId().equals(locations.findLocationSummary(locationId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		if (!delegate.canDisableLocation(locationId)) {
			return false;
		}
		/* if the user is a CRM_ADMIN then return true */
		if (auth.isUserInRole(CRM_ADMIN)) {
			return true;
		}
		/* ensure this location is associated with the organization the current user is associated with, then return true if they are an RE Admin */
		if (auth.getOrganizationId().equals(locations.findLocationSummary(locationId).getOrganizationId())) {
			return auth.isUserInRole(ORG_ADMIN);
		}
		/* the current user doesn't belong to the organization that this location is associated with */
		return false;
	}
}
