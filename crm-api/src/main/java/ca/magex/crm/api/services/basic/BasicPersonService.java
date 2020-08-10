package ca.magex.crm.api.services.basic;

import java.util.List;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

public class BasicPersonService implements CrmPersonService {

	private CrmRepositories repos;
	
	public BasicPersonService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public PersonDetails createPerson(OrganizationIdentifier organizationId, String displayName, PersonName legalName, MailingAddress address, Communication communication, List<BusinessRoleIdentifier> roleIds) {
		return repos.savePersonDetails(new PersonDetails(repos.generatePersonId(), organizationId, Status.ACTIVE, displayName, legalName, address, communication, roleIds));
	}

	@Override
	public PersonDetails updatePersonDisplayName(PersonIdentifier personId, String displayName) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withDisplayName(displayName));
	}

	@Override
	public PersonDetails updatePersonLegalName(PersonIdentifier personId, PersonName legalName) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withLegalName(legalName));
	}

	@Override
	public PersonDetails updatePersonAddress(PersonIdentifier personId, MailingAddress address) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withAddress(address));
	}

	@Override
	public PersonDetails updatePersonCommunication(PersonIdentifier personId, Communication communication) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withCommunication(communication));
	}
	
	@Override
	public PersonDetails updatePersonBusinessRoles(PersonIdentifier personId, List<BusinessRoleIdentifier> businessRoleIds) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withBusinessRoleIds(businessRoleIds));
	}

	@Override
	public PersonSummary enablePerson(PersonIdentifier personId) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withStatus(Status.ACTIVE)).asSummary();
	}

	@Override
	public PersonSummary disablePerson(PersonIdentifier personId) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withStatus(Status.INACTIVE)).asSummary();
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		return repos.findPersonSummary(personId);
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		return repos.findPersonDetails(personId);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return repos.countPersons(filter);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return repos.findPersonSummaries(filter, paging);
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return repos.findPersonDetails(filter, paging);
	}

}