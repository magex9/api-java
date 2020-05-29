package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
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
	public BasicPersonPolicy(
			CrmOrganizationService organizationService,
			CrmPersonService personService) {
		this.organizationService = organizationService;
		this.personService = personService;
	}
	
	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		try {
			/* can create a person for the organization, if the organization exists, and is active */
			return organizationService.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		try {
			/* can view a person if the person exists */
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		try {
			/* can only update a person if the person exists, and is active */
			return personService.findPersonSummary(personId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		try {
			/* can enable a person if the person exists */
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		try {
			/* can disable a person if the person exists */
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}