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
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmUserServiceCachingDelegateTests {

	@Autowired private CrmUserService delegate;
	@Autowired private CacheManager cacheManager;
	
	private CacheTemplate cacheTemplate;
	private CrmUserServiceCachingDelegate userService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Users);
		userService = new CrmUserServiceCachingDelegate(delegate, cacheTemplate);
	}

	@Test
	public void testCacheNewUser() {
		final AtomicInteger userIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new User(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), Status.ACTIVE, invocation.getArgument(3));
		}).given(delegate).createUser(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyList());
		User user = userService.createUser(new OrganizationIdentifier("ABC"), new PersonIdentifier("PP"), "userA", List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyList());

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheNewUserFromPrototype() {
		final AtomicInteger userIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			User arg = invocation.getArgument(0);
			return new User(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), arg.getOrganizationId(), arg.getPersonId(), arg.getUsername(), arg.getStatus(), arg.getAuthenticationRoleIds());
		}).given(delegate).createUser(Mockito.any(User.class));		User user = userService.createUser(new User(null, new OrganizationIdentifier("ABC"), new PersonIdentifier("PP"), "userA", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN"))));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(User.class));

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserById() {
		User user = new User(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUser(Mockito.any(UserIdentifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUser(new UserIdentifier("A")));

		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.any(UserIdentifier.class));

		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserByUsername() {
		User user = new User(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUser(Mockito.any(UserIdentifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUserByUsername("user"));

		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserByUsername(Mockito.anyString());

		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUser(Mockito.any(UserIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUser(new UserIdentifier("1")));
		Assert.assertNull(userService.findUser(new UserIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.any(UserIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserByUsername() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUserByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUserByUsername("bobby"));
		Assert.assertNull(userService.findUserByUsername("bobby"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserByUsername(Mockito.anyString());
	}
	
	@Test
	public void testUpdateExistingUser() {
		final AtomicInteger userIndex = new AtomicInteger();
		final AtomicReference<User> reference = new AtomicReference<User>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new User(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2), Status.ACTIVE, invocation.getArgument(3)));
			return reference.get();
		}).given(delegate).createUser(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withAuthenticationRoleIds(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateUserRoles(Mockito.any(UserIdentifier.class), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableUser(Mockito.any(UserIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableUser(Mockito.any(UserIdentifier.class));
				
		
		/* create and ensure cached */
		User user = userService.createUser(new OrganizationIdentifier("O"), new PersonIdentifier("P"), "userABC", List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		Assert.assertEquals(reference.get(), user);
		/* ensure the details and summary are cached */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());

		/* clear cache, update roles, and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.updateUserRoles(user.getUserId(), List.of(new AuthenticationRoleIdentifier("SYS/ADMIN")));
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.disableUser(user.getUserId());
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.enableUser(user.getUserId());
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateUserRoles(Mockito.eq(new UserIdentifier("JJ")), Mockito.any());
		Assert.assertNull(userService.updateUserRoles(new UserIdentifier("JJ"), List.of(new AuthenticationRoleIdentifier("SYS/ADMIN"))));
		Assert.assertNull(userService.findUser(new UserIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new UserIdentifier("JJ"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableUser(Mockito.eq(new UserIdentifier("KK")));
		Assert.assertNull(userService.disableUser(new UserIdentifier("KK")));
		Assert.assertNull(userService.findUser(new UserIdentifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new UserIdentifier("KK"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableUser(Mockito.eq(new UserIdentifier("LL")));
		Assert.assertNull(userService.enableUser(new UserIdentifier("LL")));
		Assert.assertNull(userService.findUser(new UserIdentifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new UserIdentifier("LL"));
	}
	
	@Test
	public void testCachingFindResults() {
		User user1 = new User(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user1", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		User user2 = new User(new UserIdentifier("B"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user2", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		User user3 = new User(new UserIdentifier("C"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user3", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(user1, user2, user3), 3);
		}).given(delegate).findUsers(Mockito.any(UsersFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(user1, user2, user3), 3);
		}).given(delegate).findUsers(Mockito.any(UsersFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countUsers(Mockito.any(UsersFilter.class));

		Assert.assertEquals(3l, userService.countUsers(new UsersFilter()));

		/* find users with paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUsers(new UsersFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUser(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUser(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUser(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());

		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUsers(new UsersFilter()).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUser(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUser(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUser(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUsers(new UsersFilter(), UsersFilter.getDefaultPaging()).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUser(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUser(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUser(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}
	
	@Test
	public void testPasswordInteraction() {
		BDDMockito.willReturn(true).given(delegate).changePassword(Mockito.any(), Mockito.anyString(), Mockito.anyString());
		BDDMockito.willReturn("hello").given(delegate).resetPassword(Mockito.any());
		
		/* change password, ensure the delegate got called */
		Assert.assertTrue(userService.changePassword(new UserIdentifier("A"), "aa", "bb"));
		BDDMockito.verify(delegate, Mockito.times(1)).changePassword(Mockito.any(), Mockito.anyString(), Mockito.anyString());
		
		/* reset password, ensure the delegate got called */
		Assert.assertEquals("hello", userService.resetPassword(new UserIdentifier("B")));
		BDDMockito.verify(delegate, Mockito.times(1)).resetPassword(Mockito.any());
	}
}
