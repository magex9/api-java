package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmPersonPolicy;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

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
	public boolean canCreatePersonForOrganization(OrganizationIdentifier organizationId) {
		/* can only create a location for the organization, if the organization exists, and is active */
		OrganizationSummary summary = organizations.findOrganizationSummary(organizationId);
		if (summary == null) {
			throw new ItemNotFoundException("Organization ID '" + organizationId + "'");
		}
		return organizations.findOrganizationSummary(organizationId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canViewPerson(PersonIdentifier personId) {
		/* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdatePerson(PersonIdentifier personId) {
		/* can only update a location if it exists, and is active */
		PersonSummary summary = persons.findPersonSummary(personId);
		if (summary == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		/* can only update a person if the person exists, and is active */
		return persons.findPersonSummary(personId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnablePerson(PersonIdentifier personId) {
		//* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisablePerson(PersonIdentifier personId) {
		/* can view a person if the person exists */
		if (persons.findPersonSummary(personId) == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return true;
	}
}