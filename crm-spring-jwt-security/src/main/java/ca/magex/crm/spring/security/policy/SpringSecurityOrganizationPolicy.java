package ca.magex.crm.spring.security.policy;

import org.springframework.stereotype.Component;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.CrmOrganizationPolicy;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityOrganizationPolicy extends AbstractSpringSecurityPolicy implements CrmOrganizationPolicy {	

	@Override
	public boolean canCreateOrganization() {
		return getCurrentUser().getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent();
	}

	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/* return true if the person belongs to the org */
		return personDetails.getOrganizationId().equals(organizationId);
	}

	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		PersonDetails personDetails = getCurrentUser();
		/* if the user is a CRM_ADMIN then return true */
		if (personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent()) {
			return true;
		}
		/* if the person belongs to the org, then return true if they are an RE_ADMIN, false otherwise */
		if (personDetails.getOrganizationId().equals(organizationId)) {
			return personDetails.getUser().getRoles().stream().filter((r) -> r.contentEquals("RE_ADMIN")).findAny().isPresent();
		}				
		/* if the person doesn't belong to the org, then return false */
		return false;
	}

	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		/* only CRM_ADMIN can enable an org */ 
		return getCurrentUser().getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent();
	}

	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		/* only CRM_ADMIN can disable an org */ 
		return getCurrentUser().getUser().getRoles().stream().filter((r) -> r.contentEquals("CRM_ADMIN")).findAny().isPresent();
	}
}