package ca.magex.crm.spring.jwt.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityLocationPolicy extends AbstractSpringSecurityPolicy implements CrmLocationPolicy {

	@Autowired private CrmLocationService locationService;

	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		if (personDetails.getOrganizationId().equals(organizationId)) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canViewLocation(Identifier locationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		LocationDetails location = locationService.findLocationDetails(locationId);		
		/* return true if the person belongs to the org */
		return personDetails.getOrganizationId().equals(location.getOrganizationId());
	}

	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (personDetails.getOrganizationId().equals(location.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canEnableLocation(Identifier locationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (personDetails.getOrganizationId().equals(location.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canDisableLocation(Identifier locationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/*
		 * if the person belongs to the org, then return true if they are an RE_ADMIN,
		 * false otherwise
		 */
		LocationDetails location = locationService.findLocationDetails(locationId);
		if (personDetails.getOrganizationId().equals(location.getOrganizationId())) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("RE_ADMIN")).findAny().isPresent();
		}
		/* if the person doesn't belong to the org, then return false */
		return false;
	}
}
