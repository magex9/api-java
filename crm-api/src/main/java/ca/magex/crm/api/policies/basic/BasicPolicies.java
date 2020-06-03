package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.policies.CrmPolicyDelegate;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

public class BasicPolicies extends CrmPolicyDelegate {
	
	public BasicPolicies(
			CrmAuthenticationService auth,
			CrmPermissionService permissions,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new BasicPermissionPolicy(permissions),
			new BasicOrganizationPolicy(organizations),
			new BasicLocationPolicy(organizations, locations),
			new BasicPersonPolicy(organizations, persons),
			new BasicUserPolicy(persons, users)
		);
	}
	
}
