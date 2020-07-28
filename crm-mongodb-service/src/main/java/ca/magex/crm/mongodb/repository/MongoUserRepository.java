package ca.magex.crm.mongodb.repository;

import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Implementation of the Crm User Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoUserRepository implements CrmUserRepository {

	private MongoDatabase mongoCrm;
	private CrmUpdateNotifier notifier;
	
	/**
	 * Creates our new MongoDB Backed User Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoUserRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		this.mongoCrm = mongoCrm;
		this.notifier = notifier;
	}
	
	@Override
	public UserDetails saveUserDetails(UserDetails user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countUsers(UsersFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}

}
