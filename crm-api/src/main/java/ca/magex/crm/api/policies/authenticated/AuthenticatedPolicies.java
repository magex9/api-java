package ca.magex.crm.api.policies.authenticated;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;

public class AuthenticatedPolicies extends CrmPoliciesAdapter implements CrmPolicies {
	
	public AuthenticatedPolicies(CrmAuthenticationService auth, CrmServices services) {
		super(
			new AuthenticatedConfigurationPolicy(auth, services),
			new AuthenticatedOptionPolicy(auth, services),
			new AuthenticatedOrganizationPolicy(auth, services),
			new AuthenticatedLocationPolicy(auth, services, services),
			new AuthenticatedPersonPolicy(auth, services, services),
			new AuthenticatedUserPolicy(auth, services, services)
		);
	}
	
	public AuthenticatedPolicies(			
			CrmAuthenticationService auth,
			CrmConfigurationService config,
			CrmOptionService options,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new AuthenticatedConfigurationPolicy(auth, config),
			new AuthenticatedOptionPolicy(auth, options),
			new AuthenticatedOrganizationPolicy(auth, organizations),
			new AuthenticatedLocationPolicy(auth, organizations, locations),
			new AuthenticatedPersonPolicy(auth, organizations, persons),
			new AuthenticatedUserPolicy(auth, persons, users)
		);
	}

}
