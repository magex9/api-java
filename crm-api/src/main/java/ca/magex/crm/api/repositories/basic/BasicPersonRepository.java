package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.repositories.CrmPersonRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.PersonIdentifier;

public class BasicPersonRepository implements CrmPersonRepository {

	private CrmStore store;

	public BasicPersonRepository(CrmStore store) {
		this.store = store;
	}

	private Stream<PersonDetails> apply(PersonsFilter filter) {
		return store.getPersons().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
				.map(i -> SerializationUtils.clone(i))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList()), paging);
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		return store.getPersons().get(personId);
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		PersonDetails personDetails = findPersonDetails(personId);
		return personDetails == null ? null : personDetails.asSummary();
	}

	@Override
	public PersonDetails savePersonDetails(PersonDetails person) {
		store.getNotifier().personUpdated(System.nanoTime(), person.getPersonId());
		store.getPersons().put(person.getPersonId(), person);
		return person;
	}

}