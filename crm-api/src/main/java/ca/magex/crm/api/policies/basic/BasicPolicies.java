package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.adapters.CrmPoliciesAdapter;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.services.CrmGroupService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmRoleService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.CrmUserService;

public class BasicPolicies extends CrmPoliciesAdapter implements CrmPolicies {
	
	public BasicPolicies(CrmServices services) {
		super(
			new BasicConfigurationPolicy(services),
			new BasicLookupPolicy(services),
			new BasicOptionPolicy(services, services),
			new BasicGroupPolicy(services),
			new BasicRolePolicy(services, services),
			new BasicOrganizationPolicy(services),
			new BasicLocationPolicy(services, services),
			new BasicPersonPolicy(services, services),
			new BasicUserPolicy(services, services)
		);
	}
	
	public BasicPolicies(
			CrmConfigurationService init,
			CrmLookupService lookups,
			CrmOptionService options,
			CrmGroupService groups,
			CrmRoleService roles,
			CrmOrganizationService organizations,
			CrmLocationService locations,
			CrmPersonService persons,
			CrmUserService users) {
		super(
			new BasicConfigurationPolicy(init),
			new BasicLookupPolicy(lookups),
			new BasicOptionPolicy(lookups, options),
			new BasicGroupPolicy(groups),
			new BasicRolePolicy(groups, roles),
			new BasicOrganizationPolicy(organizations),
			new BasicLocationPolicy(organizations, locations),
			new BasicPersonPolicy(organizations, persons),
			new BasicUserPolicy(persons, users)
		);
	}
	
}
