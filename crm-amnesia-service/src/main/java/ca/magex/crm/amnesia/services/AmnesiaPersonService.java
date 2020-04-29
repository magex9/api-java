package ca.magex.crm.amnesia.services;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
public class AmnesiaPersonService implements CrmPersonService {

	@Autowired private AmnesiaDB db;

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition unit) {
		return db.savePerson(new PersonDetails(db.generateId(), organizationId, Status.ACTIVE, legalName.getDisplayName(), legalName, address, communication, unit));
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName legalName) {
		return db.savePerson(findPersonDetails(personId).withLegalName(legalName).withDisplayName(legalName.getDisplayName()));
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		return db.savePerson(findPersonDetails(personId).withAddress(address));
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		return db.savePerson(findPersonDetails(personId).withCommunication(communication));
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		return db.savePerson(findPersonDetails(personId).withPosition(position));
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		return db.savePerson(findPersonDetails(personId).withStatus(Status.ACTIVE));
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		return db.savePerson(findPersonDetails(personId).withStatus(Status.INACTIVE));
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return db.findPerson(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		return db.findPerson(personId);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return applyFilter(filter).count();
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(applyFilter(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(applyFilter(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	private Stream<PersonDetails> applyFilter(PersonsFilter filter) {
		return db.findByType(PersonDetails.class)
				.filter(person -> StringUtils.isNotBlank(filter.getDisplayName()) ? person.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(person -> filter.getStatus() != null ? filter.getStatus().equals(person.getStatus()) : true)
				.filter(person -> filter.getOrganizationId() != null ? filter.getOrganizationId().equals(person.getOrganizationId()) : true);
	}
}