package ca.magex.crm.hazelcast.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
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
	public Identifier generateUserId() {
		return CrmStore.generateId(User.class);
	}

	@Override
	public User saveUser(User user) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		/* persist a clone of this location, and return the original */
		users.put(user.getUserId(), SerializationUtils.clone(user));
		return user;
	}

	@Override
	public User findUser(Identifier userId) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			return null;
		}
		return SerializationUtils.clone(user);
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		List<User> allMatchingUsers = users.values(new CrmFilterPredicate<User>(filter))
				.stream()
				.map(u -> SerializationUtils.clone(u))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingUsers, paging);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		return users.values(new CrmFilterPredicate<User>(filter)).size();
	}
}