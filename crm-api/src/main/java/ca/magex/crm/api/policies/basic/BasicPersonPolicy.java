package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicPersonPolicy implements CrmPersonPolicy {

	private CrmOrganizationService organizationService;
	private CrmPersonService personService;
	
	/**
	 * Basic Person Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizationService
	 * @param personService
	 */
	public BasicPersonPolicy(CrmOrganizationService organizationService, CrmPersonService personService) {
		this.organizationService = organizationService;
		this.personService = personService;
	}
	
	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		/* can create a person for the organization, if the organization exists, and is active */
		return organizationService.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		/* can view a person if the person exists */
		personService.findPersonSummary(personId);
		return true;
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		/* can only update a person if the person exists, and is active */
		return personService.findPersonSummary(personId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		/* can enable a person if the person exists */
		personService.findPersonSummary(personId);
		return true;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		/* can disable a person if the person exists */
		personService.findPersonSummary(personId);
		return true;
	}
}