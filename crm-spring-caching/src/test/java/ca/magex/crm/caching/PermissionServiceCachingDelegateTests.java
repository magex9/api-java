package ca.magex.crm.caching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.test.config.MockConfig;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, TestConfig.class, MockConfig.class })
@ActiveProfiles(profiles = { MagexCrmProfiles.CRM_NO_AUTH })
public class PermissionServiceCachingDelegateTests {

	@Autowired private CrmPermissionService delegate;
	@Autowired private CacheManager cacheManager;
	@Autowired @Qualifier("PermissionServiceCachingDelegate") private CrmPermissionService permissionService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
	}

	@Test
	public void testCacheNewGroup() {
		final AtomicInteger groupIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new Group(new Identifier(Integer.toString(groupIndex.getAndIncrement())), Status.ACTIVE, invocation.getArgument(0));
		}).given(delegate).createGroup(Mockito.any(Localized.class));

		Group group = permissionService.createGroup(new Localized("ABC", "abc", "äbç"));

		/* find and ensure caching worked */
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		Mockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any());

		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		Mockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.any());
	}

	@Test
	public void testCacheNewGroupFromPrototype() {
		final AtomicInteger groupIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			Group arg = invocation.getArgument(0);
			return new Group(new Identifier(Integer.toString(groupIndex.getAndIncrement())), arg.getStatus(), arg.getName());
		}).given(delegate).createGroup(Mockito.any(Group.class));

		Group group = permissionService.createGroup(new Group(null, Status.ACTIVE, new Localized("ABC", "abc", "äbç")));

		/* find and ensure caching worked */
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		Mockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any());

		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		Mockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.any());
	}

	@Test
	public void testCacheExistingGroupById() {
		Group group = new Group(new Identifier("A"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));

		BDDMockito.willAnswer((invocation) -> {
			return group;
		}).given(delegate).findGroup(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(group, permissionService.findGroup(new Identifier("A")));

		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findGroup(Mockito.any());

		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.any());
	}

	@Test
	public void testCacheExistingGroupByCode() {
		Group group = new Group(new Identifier("A"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));

		BDDMockito.willAnswer((invocation) -> {
			return group;
		}).given(delegate).findGroupByCode(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(group, permissionService.findGroupByCode("ABC"));

		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any());

		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(1)).findGroupByCode(Mockito.any());
	}

	@Test
	public void testCacheNonExistantGroupById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findGroup(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(permissionService.findGroup(new Identifier("1")));
		Assert.assertNull(permissionService.findGroup(new Identifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findGroup(Mockito.any());
	}

	@Test
	public void testCacheNonExistantGroupByCode() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findGroupByCode(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(permissionService.findGroupByCode("ABC"));
		Assert.assertNull(permissionService.findGroupByCode("ABC"));
		BDDMockito.verify(delegate, Mockito.times(1)).findGroupByCode(Mockito.any());
	}

	@Test
	public void testUpdateExistingGroup() {
		final AtomicInteger groupIndex = new AtomicInteger();
		final AtomicReference<Group> reference = new AtomicReference<Group>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new Group(new Identifier(Integer.toString(groupIndex.getAndIncrement())), Status.ACTIVE, invocation.getArgument(0)));
			return reference.get();
		}).given(delegate).createGroup(Mockito.any(Localized.class));

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateGroupName(Mockito.any(Identifier.class), Mockito.any());

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableGroup(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableGroup(Mockito.any(Identifier.class));

		/* create and ensure cached */
		Group group = permissionService.createGroup(new Localized("ABC", "abc", "äbç"));
		Assert.assertEquals(reference.get(), group);
		/* ensure the values are cached */
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());

		/* clear cache, update roles, and ensure cached */
		cacheManager.getCache("groups").clear();
		group = permissionService.updateGroupName(group.getGroupId(), new Localized("ABC", "def", "déf"));
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());

		/* clear cache, disable and ensure cached */
		cacheManager.getCache("groups").clear();
		group = permissionService.disableGroup(group.getGroupId());
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());

		/* clear cache, disable and ensure cached */
		cacheManager.getCache("groups").clear();
		group = permissionService.enableGroup(group.getGroupId());
		Assert.assertEquals(group, permissionService.findGroup(group.getGroupId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		Assert.assertEquals(group, permissionService.findGroupByCode(group.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());
	}

	@Test
	public void testCachingFindGroupResults() {
		Group group1 = new Group(new Identifier("A"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));
		Group group2 = new Group(new Identifier("B"), Status.ACTIVE, new Localized("ABCD", "abcd", "äbçd"));
		Group group3 = new Group(new Identifier("C"), Status.ACTIVE, new Localized("ABCDE", "abcde", "äbçdé"));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(group1, group2, group3), 3);
		}).given(delegate).findGroups(Mockito.any(GroupsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), GroupsFilter.getDefaultPaging(), List.of(group1, group2, group3), 3);
		}).given(delegate).findGroups(Mockito.any(GroupsFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return List.of(group1.getCode(), group2.getCode(), group3.getCode());
		}).given(delegate).findActiveGroupCodes();

		Assert.assertEquals(3, permissionService.findActiveGroupCodes().size());
		BDDMockito.verify(delegate, Mockito.times(1)).findActiveGroupCodes();
		
		/* find users with paging and ensure cached results */
		cacheManager.getCache("groups").clear();
		Assert.assertEquals(3, permissionService.findGroups(new GroupsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(group1, permissionService.findGroup(group1.getGroupId()));
		Assert.assertEquals(group1, permissionService.findGroupByCode(group1.getCode()));

		Assert.assertEquals(group2, permissionService.findGroup(group2.getGroupId()));
		Assert.assertEquals(group2, permissionService.findGroupByCode(group2.getCode()));

		Assert.assertEquals(group3, permissionService.findGroup(group3.getGroupId()));
		Assert.assertEquals(group3, permissionService.findGroupByCode(group3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());

		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, permissionService.findGroups(new GroupsFilter()).getNumberOfElements());

		Assert.assertEquals(group1, permissionService.findGroup(group1.getGroupId()));
		Assert.assertEquals(group1, permissionService.findGroupByCode(group1.getCode()));

		Assert.assertEquals(group2, permissionService.findGroup(group2.getGroupId()));
		Assert.assertEquals(group2, permissionService.findGroupByCode(group2.getCode()));

		Assert.assertEquals(group3, permissionService.findGroup(group3.getGroupId()));
		Assert.assertEquals(group3, permissionService.findGroupByCode(group3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findGroup(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findGroupByCode(Mockito.anyString());
	}
	
	@Test
	public void testCacheNewRole() {
		final AtomicInteger roleIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new Role(new Identifier(Integer.toString(roleIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, invocation.getArgument(1));
		}).given(delegate).createRole(Mockito.any(), Mockito.any());

		Role role = permissionService.createRole(new Identifier("G1"), new Localized("ABC", "abc", "äbç"));

		/* find and ensure caching worked */
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		Mockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any());

		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		Mockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.any());
	}

	@Test
	public void testCacheNewRoleFromPrototype() {
		final AtomicInteger roleIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			Role arg = invocation.getArgument(0);
			return new Role(new Identifier(Integer.toString(roleIndex.getAndIncrement())), arg.getGroupId(), arg.getStatus(), arg.getName());
		}).given(delegate).createRole(Mockito.any());

		Role role = permissionService.createRole(new Role(null, new Identifier("G1"), Status.ACTIVE, new Localized("ABC", "abc", "äbç")));

		/* find and ensure caching worked */
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		Mockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any());

		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		Mockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.any());
	}

	@Test
	public void testCacheExistingRoleById() {
		Role role = new Role(new Identifier("R1"), new Identifier("G1"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));

		BDDMockito.willAnswer((invocation) -> {
			return role;
		}).given(delegate).findRole(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(role, permissionService.findRole(new Identifier("R1")));

		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findRole(Mockito.any());

		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.any());
	}

	@Test
	public void testCacheExistingRoleByCode() {
		Role role = new Role(new Identifier("R1"), new Identifier("G1"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));

		BDDMockito.willAnswer((invocation) -> {
			return role;
		}).given(delegate).findRoleByCode(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(role, permissionService.findRoleByCode("ABC"));

		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any());

		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(1)).findRoleByCode(Mockito.any());
	}

	@Test
	public void testCacheNonExistantRoleById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findRole(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(permissionService.findRole(new Identifier("1")));
		Assert.assertNull(permissionService.findRole(new Identifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findRole(Mockito.any());
	}

	@Test
	public void testCacheNonExistantRoleByCode() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findRoleByCode(Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(permissionService.findRoleByCode("ABC"));
		Assert.assertNull(permissionService.findRoleByCode("ABC"));
		BDDMockito.verify(delegate, Mockito.times(1)).findRoleByCode(Mockito.any());
	}

	@Test
	public void testUpdateExistingRole() {
		final AtomicInteger roleIndex = new AtomicInteger();
		final AtomicReference<Role> reference = new AtomicReference<Role>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new Role(new Identifier(Integer.toString(roleIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).createRole(Mockito.any(), Mockito.any());

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateRoleName(Mockito.any(Identifier.class), Mockito.any());

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableRole(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableRole(Mockito.any(Identifier.class));

		/* create and ensure cached */
		Role role = permissionService.createRole(new Identifier("G1"), new Localized("ABC", "abc", "äbç"));
		Assert.assertEquals(reference.get(), role);
		/* ensure the values are cached */
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());

		/* clear cache, update roles, and ensure cached */
		cacheManager.getCache("roles").clear();
		role = permissionService.updateRoleName(role.getRoleId(), new Localized("ABC", "def", "déf"));
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());

		/* clear cache, disable and ensure cached */
		cacheManager.getCache("roles").clear();
		role = permissionService.disableRole(role.getGroupId());
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());

		/* clear cache, disable and ensure cached */
		cacheManager.getCache("roles").clear();
		role = permissionService.enableRole(role.getGroupId());
		Assert.assertEquals(role, permissionService.findRole(role.getRoleId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		Assert.assertEquals(role, permissionService.findRoleByCode(role.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());
	}

	@Test
	public void testCachingFindRoleResults() {
		Role role1 = new Role(new Identifier("A"), new Identifier("G1"), Status.ACTIVE, new Localized("ABC", "abc", "äbç"));
		Role role2 = new Role(new Identifier("B"), new Identifier("G1"), Status.ACTIVE, new Localized("ABCD", "abcd", "äbçd"));
		Role role3 = new Role(new Identifier("C"), new Identifier("G1"), Status.ACTIVE, new Localized("ABCDE", "abcde", "äbçdé"));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(role1, role2, role3), 3);
		}).given(delegate).findRoles(Mockito.any(RolesFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), RolesFilter.getDefaultPaging(), List.of(role1, role2, role3), 3);
		}).given(delegate).findRoles(Mockito.any(RolesFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return List.of(role1.getCode(), role2.getCode(), role3.getCode());
		}).given(delegate).findActiveRoleCodesForGroup(Mockito.anyString());
		
		BDDMockito.willAnswer((invocation) -> {
			return List.of(role1, role2, role3);
		}).given(delegate).findRoles();
		

		Assert.assertEquals(3, permissionService.findActiveRoleCodesForGroup("G1").size());
		BDDMockito.verify(delegate, Mockito.times(1)).findActiveRoleCodesForGroup(Mockito.anyString());
		
		/* find users with paging and ensure cached results */
		cacheManager.getCache("roles").clear();
		Assert.assertEquals(3, permissionService.findRoles(new RolesFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(role1, permissionService.findRole(role1.getRoleId()));
		Assert.assertEquals(role1, permissionService.findRoleByCode(role1.getCode()));

		Assert.assertEquals(role2, permissionService.findRole(role2.getRoleId()));
		Assert.assertEquals(role2, permissionService.findRoleByCode(role2.getCode()));

		Assert.assertEquals(role3, permissionService.findRole(role3.getRoleId()));
		Assert.assertEquals(role3, permissionService.findRoleByCode(role3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());

		/* find users with default paging and ensure cached results */
		cacheManager.getCache("roles").clear();
		Assert.assertEquals(3, permissionService.findRoles(new RolesFilter()).getNumberOfElements());

		Assert.assertEquals(role1, permissionService.findRole(role1.getRoleId()));
		Assert.assertEquals(role1, permissionService.findRoleByCode(role1.getCode()));

		Assert.assertEquals(role2, permissionService.findRole(role2.getRoleId()));
		Assert.assertEquals(role2, permissionService.findRoleByCode(role2.getCode()));

		Assert.assertEquals(role3, permissionService.findRole(role3.getRoleId()));
		Assert.assertEquals(role3, permissionService.findRoleByCode(role3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());
		
		/* find users with default paging and ensure cached results */
		cacheManager.getCache("roles").clear();
		Assert.assertEquals(3, permissionService.findRoles().size());

		Assert.assertEquals(role1, permissionService.findRole(role1.getRoleId()));
		Assert.assertEquals(role1, permissionService.findRoleByCode(role1.getCode()));

		Assert.assertEquals(role2, permissionService.findRole(role2.getRoleId()));
		Assert.assertEquals(role2, permissionService.findRoleByCode(role2.getCode()));

		Assert.assertEquals(role3, permissionService.findRole(role3.getRoleId()));
		Assert.assertEquals(role3, permissionService.findRoleByCode(role3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findRole(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findRoleByCode(Mockito.anyString());
	}
}