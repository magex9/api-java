package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

public class BasicPolicies extends CrmPoliciesAdapter implements CrmPolicies {
	
	public BasicPolicies(
			CrmLookupService lookups,
			CrmPermissionService permissions,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new BasicLookupPolicy(lookups),
			new BasicPermissionPolicy(permissions),
			new BasicOrganizationPolicy(organizations),
			new BasicLocationPolicy(organizations, locations),
			new BasicPersonPolicy(organizations, persons),
			new BasicUserPolicy(persons, users)
		);
	}
	
}
