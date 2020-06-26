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
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class BasicPersonService implements CrmPersonService {

	private CrmRepositories repos;
	
	public BasicPersonService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, List<Identifier> roleIds) {
		return repos.savePersonDetails(new PersonDetails(repos.generatePersonId(), organizationId, Status.ACTIVE, legalName.getDisplayName(), legalName, address, communication, roleIds));
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName legalName) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withLegalName(legalName).withDisplayName(legalName.getDisplayName()));
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withAddress(address));
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withCommunication(communication));
	}
	
	@Override
	public PersonDetails updatePersonRoles(Identifier personId, List<Identifier> roleIds) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withRoleIds(roleIds));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withStatus(Status.ACTIVE));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		PersonDetails person = repos.findPersonDetails(personId);
		if (person == null) {
			return null;
		}
		return repos.savePersonDetails(person.withStatus(Status.INACTIVE));
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return repos.findPersonSummary(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return repos.findPersonDetails(personId);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return repos.countPersons(filter);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return repos.findPersonSummary(filter, paging);
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return repos.findPersonDetails(filter, paging);
	}

}