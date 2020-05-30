package ca.magex.crm.api.policies;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
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
	
	public BasicPersonPolicy(CrmOrganizationService organizationService, CrmPersonService personService) {
		this.organizationService = organizationService;
		this.personService = personService;
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		try {
			organizationService.findOrganizationDetails(organizationId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		try {
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		try {
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		try {
			return personService.findPersonSummary(personId).getStatus() != Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		try {
			return personService.findPersonSummary(personId).getStatus() != Status.INACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}