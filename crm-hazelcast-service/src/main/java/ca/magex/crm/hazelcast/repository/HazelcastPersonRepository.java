package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.repositories.CrmPersonRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Person Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastPersonRepository implements CrmPersonRepository {

	private XATransactionAwareHazelcastInstance hzInstance;	
	
	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastPersonRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}

	@Override
	public PersonDetails savePersonDetails(PersonDetails person) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		/* persist a clone of this location, and return the original */
		persons.put(person.getPersonId(), SerializationUtils.clone(person));
		return person;
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		return SerializationUtils.clone(personDetails);
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		PersonDetails personDetails = persons.get(personId);
		if (personDetails == null) {
			return null;
		}
		return SerializationUtils.clone(personDetails).asSummary();
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonDetails> allMatchingPersons = persons.values(new CrmFilterPredicate<PersonSummary>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> SerializationUtils.clone(i))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		List<PersonSummary> allMatchingPersons = persons.values(new CrmFilterPredicate<PersonSummary>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(i -> i.asSummary())
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingPersons, paging);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		TransactionalMap<PersonIdentifier, PersonDetails> persons = hzInstance.getPersonsMap();
		return persons.values(new CrmFilterPredicate<PersonSummary>(filter)).size();
	}
}