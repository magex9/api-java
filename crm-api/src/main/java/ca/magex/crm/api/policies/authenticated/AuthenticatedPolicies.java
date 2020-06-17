package ca.magex.crm.api.policies.authenticated;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;

public class AuthenticatedPolicies extends CrmPoliciesAdapter implements CrmPolicies {
	
	public AuthenticatedPolicies(			
			CrmAuthenticationService auth,
			CrmLookupService lookups,
			CrmPermissionService permissions,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new AuthenticatedLookupPolicy(auth, lookups),
			new AuthenticatedPermissionPolicy(auth, permissions),
			new AuthenticatedOrganizationPolicy(auth, organizations),
			new AuthenticatedLocationPolicy(auth, organizations, locations),
			new AuthenticatedPersonPolicy(auth, organizations, persons),
			new AuthenticatedUserPolicy(auth, persons, users)
		);
	}

}
