package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.repositories.CrmUserRepository;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.id.UserIdentifier;

public class BasicUserRepository implements CrmUserRepository {

	private CrmStore store;
	
	public BasicUserRepository(CrmStore store) {
		this.store = store;
	}
	
	private Stream<UserDetails> apply(UsersFilter filter) {
		return store.getUsers().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<UserDetails> findUserDetails(UsersFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public FilteredPage<UserSummary> findUserSummaries(UsersFilter filter, Paging paging) {
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
	public UserDetails findUserDetails(UserIdentifier userId) {
		return store.getUsers().get(userId);
	}

	@Override
	public UserSummary findUserSummary(UserIdentifier userId) {
		return store.getUsers().get(userId).asSummary();
	}

	@Override
	public UserDetails saveUserDetails(UserDetails user) {
		store.getNotifier().userUpdated(System.nanoTime(), user.getUserId());
		store.getUsers().put(user.getUserId(), user);
		return user;
	}

}
