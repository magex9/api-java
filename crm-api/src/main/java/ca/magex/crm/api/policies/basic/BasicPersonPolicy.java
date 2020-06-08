package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonSummary;
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

	private CrmOrganizationService organizations;

	private CrmPersonService persons;

	/**
	 * Basic Person Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizations
	 * @param persons
	 */
	public BasicPersonPolicy(CrmOrganizationService organizations, CrmPersonService persons) {
		this.organizations = organizations;
		this.persons = persons;
	}

	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		/* can only create a location for the organization, if the organization exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return organizations.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canViewPerson(Identifier personId) {
		/* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdatePerson(Identifier personId) {
		/* can only update a location if it exists, and is active */
		PersonSummary summary = persons.findPersonSummary(personId);
		if (summary == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		/* can only update a person if the person exists, and is active */
		return persons.findPersonSummary(personId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnablePerson(Identifier personId) {
		//* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisablePerson(Identifier personId) {
		/* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}
}