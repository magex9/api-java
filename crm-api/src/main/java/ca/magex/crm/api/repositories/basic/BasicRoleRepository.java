package ca.magex.crm.api.repositories.basic;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;

import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmRoleRepository;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;

public class BasicRoleRepository implements CrmRoleRepository {

	private CrmStore store;
	
	private CrmUpdateNotifier notifier;

	public BasicRoleRepository(CrmStore store, CrmUpdateNotifier notifier) {
		this.store = store;
		this.notifier = notifier;
	}
	
	@Override
	public Identifier generateRoleId() {
		return CrmStore.generateId(Role.class);
	}
	
	private Stream<Role> apply(RolesFilter filter) {
		return store.getRoles().values().stream().filter(p -> filter.apply(p));
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}

	@Override
	public long countRoles(RolesFilter filter) {
		return apply(filter).count();
	}

	@Override
	public Role findRole(Identifier roleId) {
		return store.getRoles().get(roleId);
	}

	@Override
	public Role saveRole(Role role) {
		notifier.roleUpdated(System.nanoTime(), role.getRoleId());
		store.getRoles().put(role.getRoleId(), role);
		return role;
	}

}