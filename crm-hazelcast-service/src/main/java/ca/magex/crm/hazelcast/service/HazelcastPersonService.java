package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

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
@Validated
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPersonService implements CrmPersonService {

	public static String HZ_PERSON_KEY = "persons";

	@Autowired private HazelcastInstance hzInstance;

	// these need to be marked as lazy because spring proxies this class due to the @Validated annotation
	// if these are not lazy then they are autowired before the proxy is created and we get a cyclic dependency
	// so making them lazy allows the proxy to be created before autowiring
	@Autowired @Lazy private CrmOrganizationService organizationService;

	@Override
	public PersonDetails createPerson(
			@NotNull Identifier organizationId,
			@NotNull PersonName legalName,
			@NotNull MailingAddress address,
			@NotNull Communication communication,
			@NotNull BusinessPosition position) {
		/* run a find on the organizationId to ensure it exists */
		organizationService.findOrganizationDetails(organizationId);
		/* create our new person for this organizationId */
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
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
	public PersonDetails updatePersonName(@NotNull Identifier personId, @NotNull PersonName name) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getLegalName().equals(name)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withLegalName(name).withDisplayName(name.getDisplayName());
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonAddress(@NotNull Identifier personId, @NotNull MailingAddress address) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getAddress().equals(address)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withAddress(address);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonCommunication(@NotNull Identifier personId, @NotNull Communication communication) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getCommunication().equals(communication)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withCommunication(communication);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonDetails updatePersonBusinessPosition(@NotNull Identifier personId, @NotNull BusinessPosition position) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getPosition().equals(position)) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withPosition(position);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary enablePerson(@NotNull Identifier personId) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.ACTIVE);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary disablePerson(@NotNull Identifier personId) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		if (personDetails.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(personDetails);
		}
		personDetails = personDetails.withStatus(Status.INACTIVE);
		persons.put(personId, personDetails);
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary findPersonSummary(@NotNull Identifier personId) { return findPersonDetails(personId); }

	@Override
	public PersonDetails findPersonDetails(@NotNull Identifier personId) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}
		return personDetails;
	}

	@Override
	public long countPersons(@NotNull PersonsFilter filter) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
		return persons.values()
				.stream()
				.filter(p -> StringUtils.isNotBlank(filter.getDisplayName()) ? p.getDisplayName().contains(filter.getDisplayName()) : true)
				.filter(i -> filter.getStatus() != null ? i.getStatus().equals(filter.getStatus()) : true)
				.filter(j -> filter.getOrganizationId() != null ? j.getOrganizationId().equals(filter.getOrganizationId()) : true)
				.count();
	}

	@Override
	public Page<PersonDetails> findPersonDetails(@NotNull PersonsFilter filter, @NotNull Paging paging) {
		Map<Identifier, PersonDetails> persons = hzInstance.getMap(HZ_PERSON_KEY);
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
	public Page<PersonSummary> findPersonSummaries(@NotNull PersonsFilter filter, @NotNull Paging paging) {
		Map<Identifier, PersonSummary> persons = hzInstance.getMap(HZ_PERSON_KEY);
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