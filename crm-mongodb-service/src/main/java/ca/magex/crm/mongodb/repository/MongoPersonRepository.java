package ca.magex.crm.mongodb.repository;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmPersonRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * Implementation of the Crm Person Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoPersonRepository implements CrmPersonRepository {

	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	/**
	 * Creates our new MongoDB Backed Person Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoPersonRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	@Override
	public PersonDetails savePersonDetails(PersonDetails person) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonDetails findPersonDetails(PersonIdentifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PersonSummary findPersonSummary(PersonIdentifier personId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countPersons(PersonsFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

}
