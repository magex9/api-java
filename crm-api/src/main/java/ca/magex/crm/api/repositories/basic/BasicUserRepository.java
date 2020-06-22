package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public class BasicUserRepository implements CrmUserRepository {

	private CrmStore store;
	
	private CrmUpdateNotifier notifier;
	
	public BasicUserRepository(CrmStore store, CrmUpdateNotifier notifier) {
		this.store = store;
		this.notifier = notifier;
	}
	
	@Override
	public Identifier generateUserId() {
		return CrmStore.generateId(User.class);
	}
	
	private Stream<User> apply(UsersFilter filter) {
		return store.getUsers().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return apply(filter).count();
	}

	@Override
	public User findUser(Identifier userId) {
		return store.getUsers().get(userId);
	}

	@Override
	public User saveUser(User user) {
		notifier.userUpdated(System.nanoTime(), user.getUserId());
		store.getUsers().put(user.getUserId(), user);
		return user;
	}

}