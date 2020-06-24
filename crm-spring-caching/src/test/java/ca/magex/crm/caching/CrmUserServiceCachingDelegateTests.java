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

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
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
			return new User(new Identifier(Integer.toString(userIndex.getAndIncrement())), invocation.getArgument(1), Mockito.mock(PersonSummary.class), Status.ACTIVE, invocation.getArgument(2));
		}).given(delegate).createUser(Mockito.any(Identifier.class), Mockito.anyString(), Mockito.anyList());
		User user = userService.createUser(new Identifier("ABC"), "userA", List.of("DEV"));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(Identifier.class), Mockito.anyString(), Mockito.anyList());

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheNewUserFromPrototype() {
		final AtomicInteger userIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			User arg = invocation.getArgument(0);
			return new User(new Identifier(Integer.toString(userIndex.getAndIncrement())), arg.getUsername(), arg.getPerson(), arg.getStatus(), arg.getRoles());
		}).given(delegate).createUser(Mockito.any(User.class));
		User user = userService.createUser(new User(null, "user", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("DEV")));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(User.class));

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserById() {
		User user = new User(new Identifier("A"), "user", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("DEV"));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUser(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUser(new Identifier("A")));

		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.any(Identifier.class));

		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserByUsername() {
		User user = new User(new Identifier("A"), "user", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("DEV"));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUser(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUserByUsername("user"));

		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserByUsername(Mockito.anyString());

		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUser(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUser(new Identifier("1")));
		Assert.assertNull(userService.findUser(new Identifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.any(Identifier.class));
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
			reference.set(new User(new Identifier(Integer.toString(userIndex.getAndIncrement())), invocation.getArgument(1), Mockito.mock(PersonSummary.class), Status.ACTIVE, invocation.getArgument(2)));
			return reference.get();
		}).given(delegate).createUser(Mockito.any(Identifier.class), Mockito.anyString(), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withRoles(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateUserRoles(Mockito.any(Identifier.class), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableUser(Mockito.any(Identifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableUser(Mockito.any(Identifier.class));
				
		
		/* create and ensure cached */
		User user = userService.createUser(new Identifier("ABC"), "userABC", List.of("A", "B"));
		Assert.assertEquals(reference.get(), user);
		/* ensure the details and summary are cached */
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());

		/* clear cache, update roles, and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.updateUserRoles(user.getUserId(), List.of("C", "D", "E"));
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.disableUser(user.getUserId());
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.enableUser(user.getUserId());
		Assert.assertEquals(user, userService.findUser(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		Assert.assertEquals(user, userService.findUserByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateUserRoles(Mockito.eq(new Identifier("JJ")), Mockito.any());
		Assert.assertNull(userService.updateUserRoles(new Identifier("JJ"), List.of("C", "D", "E")));
		Assert.assertNull(userService.findUser(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new Identifier("JJ"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableUser(Mockito.eq(new Identifier("KK")));
		Assert.assertNull(userService.disableUser(new Identifier("KK")));
		Assert.assertNull(userService.findUser(new Identifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new Identifier("KK"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableUser(Mockito.eq(new Identifier("LL")));
		Assert.assertNull(userService.enableUser(new Identifier("LL")));
		Assert.assertNull(userService.findUser(new Identifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUser(new Identifier("LL"));
	}
	
	@Test
	public void testCachingFindResults() {
		User user1 = new User(new Identifier("A"), "user1", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("A"));
		User user2 = new User(new Identifier("B"), "user2", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("A"));
		User user3 = new User(new Identifier("C"), "user3", Mockito.mock(PersonSummary.class), Status.ACTIVE, List.of("A"));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(user1, user2, user3), 3);
		}).given(delegate).findUsers(Mockito.any(UsersFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(user1, user2, user3), 3);
		}).given(delegate).findUsers(Mockito.any(UsersFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(user1, user2, user3), 3);
		}).given(delegate).findActiveUserForOrg(Mockito.any(Identifier.class));

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

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
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

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
		
		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findActiveUserForOrg(new Identifier("O")).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUser(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUser(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUser(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUser(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserByUsername(Mockito.anyString());
	}
	
	@Test
	public void testPasswordInteraction() {
		BDDMockito.willReturn(true).given(delegate).changePassword(Mockito.any(), Mockito.anyString(), Mockito.anyString());
		BDDMockito.willReturn("hello").given(delegate).resetPassword(Mockito.any());
		
		/* change password, ensure the delegate got called */
		Assert.assertTrue(userService.changePassword(new Identifier("A"), "aa", "bb"));
		BDDMockito.verify(delegate, Mockito.times(1)).changePassword(Mockito.any(), Mockito.anyString(), Mockito.anyString());
		
		/* reset password, ensure the delegate got called */
		Assert.assertEquals("hello", userService.resetPassword(new Identifier("B")));
		BDDMockito.verify(delegate, Mockito.times(1)).resetPassword(Mockito.any());
	}
}
