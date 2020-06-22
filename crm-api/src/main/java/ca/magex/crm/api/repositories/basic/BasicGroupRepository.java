package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmGroupRepository;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public class BasicGroupRepository implements CrmGroupRepository {

	private CrmStore store;
	
	private CrmUpdateNotifier notifier;

	public BasicGroupRepository(CrmStore store, CrmUpdateNotifier notifier) {
		this.store = store;
		this.notifier = notifier;
	}
	
	@Override
	public Identifier generateGroupId() {
		return CrmStore.generateId(Group.class);
	}

	private synchronized Stream<Group> apply(GroupsFilter filter) {
		return store.getGroups().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public synchronized FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public synchronized long countGroups(GroupsFilter filter) {
		return apply(filter).count();
	}

	@Override
	public synchronized Group findGroup(Identifier groupId) {
		return store.getGroups().get(groupId);
	}

	@Override
	public synchronized Group saveGroup(Group group) {
		notifier.groupUpdated(System.nanoTime(), group.getGroupId());
		store.getGroups().put(group.getGroupId(), group);
		return group;
	}
	
}