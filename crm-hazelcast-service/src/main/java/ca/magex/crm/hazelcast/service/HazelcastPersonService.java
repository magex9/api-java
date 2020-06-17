package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastPersonService implements CrmPersonService {

	public static String HZ_PERSON_KEY = "persons";

	private XATransactionAwareHazelcastInstance hzInstance;

	public HazelcastPersonService(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition position) {
		/* create our new person for this organizationId */
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_PERSON_KEY);
		PersonDetails personDetails = new PersonDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				organizationId,
				Status.ACTIVE,
				legalName.getDisplayName(),
				legalName,
				address,
				communication,
				position);
		persons.put(personDetails.getPersonId(), personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getLegalName().equals(name)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withLegalName(name).withDisplayName(name.getDisplayName());
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getAddress().equals(address)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withAddress(address);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getCommunication().equals(communication)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withCommunication(communication);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getPosition().equals(position)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withPosition(position);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.ACTIVE);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary disablePerson(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		if (personDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.INACTIVE);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return findPersonDetails(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		return persons.values(new CrmFilterPredicate<PersonSummary>(filter)).size();				
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonDetails> allMatchingPersons = persons.values(new CrmFilterPredicate<PersonSummary>(filter))
				.stream()
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		TransactionalMap<Identifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonSummary> allMatchingPersons = persons.values(new CrmFilterPredicate<PersonSummary>(filter))
				.stream()
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}

	@Override
	public FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		return CrmPersonService.super.findActivePersonSummariesForOrg(organizationId);
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		return CrmPersonService.super.findPersonDetails(filter);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		return CrmPersonService.super.findPersonSummaries(filter);
	}

	@Override
	public PersonDetails createPerson(PersonDetails prototype) {
		return CrmPersonService.super.createPerson(prototype);
	}

	@Override
	public PersonDetails prototypePerson(@NotNull Identifier organizationId, @NotNull PersonName name, @NotNull MailingAddress address, @NotNull Communication communication, @NotNull BusinessPosition position) {
		return CrmPersonService.super.prototypePerson(organizationId, name, address, communication, position);
	}
}