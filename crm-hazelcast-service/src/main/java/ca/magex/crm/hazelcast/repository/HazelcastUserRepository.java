package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the User Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastUserRepository implements CrmUserRepository {

	private XATransactionAwareHazelcastInstance hzInstance;

	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastUserRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}	

	@Override
	public UserDetails saveUserDetails(UserDetails user) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		/* persist a clone of this location, and return the original */
		users.put(user.getUserId(), SerializationUtils.clone(user));
		return user;
	}

	@Override
	public UserDetails findUserDetails(UserIdentifier userId) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		UserDetails user = users.get(userId);
		if (user == null) {
			return null;
		}
		return SerializationUtils.clone(user);
	}
	
	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		UserDetails user = users.get(userId);
		if (user == null) {
			return null;
		}
		return SerializationUtils.clone(user.asSummary());
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		List<UserDetails> allMatchingUsers = users.values(new CrmFilterPredicate<UserDetails>(filter))
				.stream()				
				.sorted(filter.getComparator(paging))
				.map(u -> SerializationUtils.clone(u))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingUsers, paging);
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		List<UserSummary> allMatchingUsers = users.values(new CrmFilterPredicate<UserDetails>(filter))
				.stream()
				.sorted(filter.getComparator(paging))
				.map(i -> i.asSummary())
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingUsers, paging);
	}
	
	@Override
	public long countUsers(UsersFilter filter) {
		TransactionalMap<UserIdentifier, UserDetails> users = hzInstance.getUsersMap();
		return users.values(new CrmFilterPredicate<UserDetails>(filter)).size();
	}	
}