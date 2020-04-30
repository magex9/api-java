package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPersonService implements CrmPersonService {

	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmOrganizationService organizationService;

	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName legalName, MailingAddress address, Communication communication, BusinessPosition position) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationDetails(organizationId);
		/* create our new person for this organizationId */
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator("persons");
		PersonDetails personDetails = new PersonDetails(
				new Identifier(Long.toHexString(idGenerator.newId())),
				organizationId,
				Status.ACTIVE,
				legalName != null ? legalName.getDisplayName() : null,
				legalName,
				address,
				communication,
				position);
		persons.put(personDetails.getPersonId(), personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary enablePerson(Identifier personId) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
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
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		if (personDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.INACTIVE);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		if (personDetails.getLegalName() != null && personDetails.getLegalName().equals(name)) {
			return SerializationUtils.clone(personDetails);
		}
		if (personDetails.getLegalName() == null && name == null) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withLegalName(name).withDisplayName(name.getDisplayName());
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		if (personDetails.getAddress() != null && personDetails.getAddress().equals(address)) {
			return SerializationUtils.clone(personDetails);
		}
		if (personDetails.getAddress() == null && address == null) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withAddress(address);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		if (personDetails.getCommunication() != null && personDetails.getCommunication().equals(communication)) {
			return SerializationUtils.clone(personDetails);
		}
		if (personDetails.getCommunication() == null && communication == null) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withCommunication(communication);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		if (personDetails.getPosition() != null && personDetails.getPosition().equals(position)) {
			return SerializationUtils.clone(personDetails);
		}
		if (personDetails.getPosition() == null && position == null) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withPosition(position);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	

	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		return findPersonDetails(personId);
	}

	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Unable to find person " + personId);
		}
		return personDetails;
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		return persons.size();
	}

	@Override
	public Page<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap("persons");
		List<PersonDetails> allMatchingPersons = persons.values()
			.stream()
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, paging);
	}

	@Override
	public Page<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		Map<Identifier, PersonSummary> persons = hzInstance.getMap("persons");
		List<PersonSummary> allMatchingPersons = persons.values()
			.stream()
			.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
			.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
			.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(allMatchingPersons, paging);
	}
}
