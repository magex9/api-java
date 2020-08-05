package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;

public class BasicPolicies extends CrmPoliciesAdapter implements CrmPolicies {
	
	public BasicPolicies(CrmServices services) {
		super(
			new BasicOptionPolicy(services),
			new BasicOrganizationPolicy(services),
			new BasicLocationPolicy(services, services),
			new BasicPersonPolicy(services, services),
			new BasicUserPolicy(services, services)
		);
	}
	
	public BasicPolicies(
			CrmOptionService options,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new BasicOptionPolicy(options),
			new BasicOrganizationPolicy(organizations),
			new BasicLocationPolicy(organizations, locations),
			new BasicPersonPolicy(organizations, persons),
			new BasicUserPolicy(persons, users)
		);
	}
	
}
