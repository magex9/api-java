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

import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.crm.UserSummary;
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
			return new UserDetails(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), new OrganizationIdentifier("O"), invocation.getArgument(0), invocation.getArgument(1), Status.ACTIVE, invocation.getArgument(2));
		}).given(delegate).createUser(Mockito.any(), Mockito.anyString(), Mockito.anyList());
		UserDetails user = userService.createUser(new PersonIdentifier("PP"), "userA", List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(), Mockito.anyString(), Mockito.anyList());

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		
		/* should have added the summary to the cache */
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());

		/* should have added the details to the cache by name */
		Assert.assertEquals(user.asSummary(), userService.findUserSummaryByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummaryByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheNewUserFromPrototype() {
		final AtomicInteger userIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			UserDetails arg = invocation.getArgument(0);
			return new UserDetails(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), arg.getOrganizationId(), arg.getPersonId(), arg.getUsername(), arg.getStatus(), arg.getAuthenticationRoleIds());
		}).given(delegate).createUser(Mockito.any(UserDetails.class));		
		UserDetails user = userService.createUser(new UserDetails(null, new OrganizationIdentifier("ABC"), new PersonIdentifier("PP"), "userA", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN"))));
		BDDMockito.verify(delegate, Mockito.times(1)).createUser(Mockito.any(UserDetails.class));

		/* should have added the details to the cache */
		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		
		/* should have added the summary to the cache */
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));

		/* should have added the details to the cache by name */
		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());

		/* should have added the details to the cache by name */
		Assert.assertEquals(user.asSummary(), userService.findUserSummaryByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummaryByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserById() {
		UserDetails user = new UserDetails(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserDetails(Mockito.any(UserIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return user.asSummary();
		}).given(delegate).findUserSummary(Mockito.any(UserIdentifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserDetailsByUsername(Mockito.anyString());

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserSummaryByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUserDetails(new UserIdentifier("A")));

		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetails(Mockito.any(UserIdentifier.class));
		
		/* should have added the summary to the cache */
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));

		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());

		Assert.assertEquals(user.asSummary(), userService.findUserSummaryByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummaryByUsername(Mockito.anyString());
	}

	@Test
	public void testCacheExistingUserByUsername() {
		UserDetails user = new UserDetails(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserDetails(Mockito.any(UserIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return user.asSummary();
		}).given(delegate).findUserSummary(Mockito.any(UserIdentifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserDetailsByUsername(Mockito.anyString());

		BDDMockito.willAnswer((invocation) -> {
			return user;
		}).given(delegate).findUserSummaryByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user, userService.findUserDetailsByUsername("user"));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertEquals(user.asSummary(), userService.findUserSummaryByUsername("user"));

		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetailsByUsername(Mockito.anyString());

		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserDetailsById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUserDetails(Mockito.any(UserIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("1")));
		
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetails(Mockito.any(UserIdentifier.class));
		
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserSummaryById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUserSummary(Mockito.any(UserIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("1")));
		
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserSummary(Mockito.any(UserIdentifier.class));
		
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantUserByUsername() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findUserDetailsByUsername(Mockito.anyString());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(userService.findUserDetailsByUsername("bobby"));
		Assert.assertNull(userService.findUserDetailsByUsername("bobby"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetailsByUsername(Mockito.anyString());
	}
	
	@Test
	public void testUpdateExistingUser() {
		final AtomicInteger userIndex = new AtomicInteger();
		final AtomicReference<UserDetails> reference = new AtomicReference<UserDetails>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new UserDetails(new UserIdentifier(Integer.toString(userIndex.getAndIncrement())), new OrganizationIdentifier("O"), invocation.getArgument(0), invocation.getArgument(1), Status.ACTIVE, invocation.getArgument(2)));
			return reference.get();
		}).given(delegate).createUser(Mockito.any(), Mockito.anyString(), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withAuthenticationRoleIds(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateUserAuthenticationRoles(Mockito.any(UserIdentifier.class), Mockito.anyList());
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableUser(Mockito.any(UserIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enableUser(Mockito.any(UserIdentifier.class));
		
		/* create and ensure cached */
		UserDetails user = userService.createUser(new PersonIdentifier("P"), "userABC", List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		Assert.assertEquals(reference.get(), user);
		/* ensure the details and summary are cached */
		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());

		/* clear cache, update roles, and ensure cached */
		cacheManager.getCache("users").clear();
		user = userService.updateUserAuthenticationRoles(user.getUserId(), List.of(new AuthenticationRoleIdentifier("SYS/ADMIN")));
		Assert.assertEquals(user, userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user.asSummary(), userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		Assert.assertEquals(user, userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());
		Assert.assertEquals(user.asSummary(), userService.findUserSummaryByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummaryByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		UserSummary summary = userService.disableUser(user.getUserId());
		Assert.assertEquals(summary, userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		Assert.assertNull(userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetails(Mockito.any(UserIdentifier.class));
		Assert.assertNull(userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetailsByUsername(Mockito.anyString());
		Assert.assertNull(userService.findUserSummaryByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(1)).findUserDetailsByUsername(Mockito.anyString());
		
		/* clear cache, disable and ensure cached */
		cacheManager.getCache("users").clear();
		summary = userService.enableUser(user.getUserId());
		Assert.assertEquals(summary, userService.findUserSummary(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		Assert.assertNull(userService.findUserDetails(user.getUserId()));
		BDDMockito.verify(delegate, Mockito.times(2)).findUserDetails(Mockito.any(UserIdentifier.class));
		Assert.assertNull(userService.findUserDetailsByUsername(user.getUsername()));
		BDDMockito.verify(delegate, Mockito.times(2)).findUserDetailsByUsername(Mockito.anyString());
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updateUserAuthenticationRoles(Mockito.eq(new UserIdentifier("JJ")), Mockito.any());
		Assert.assertNull(userService.updateUserAuthenticationRoles(new UserIdentifier("JJ"), List.of(new AuthenticationRoleIdentifier("SYS/ADMIN"))));
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(new UserIdentifier("JJ"));
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(new UserIdentifier("JJ"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableUser(Mockito.eq(new UserIdentifier("KK")));
		Assert.assertNull(userService.disableUser(new UserIdentifier("KK")));
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(new UserIdentifier("KK"));
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(new UserIdentifier("KK"));
		
		/* update non existent user (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableUser(Mockito.eq(new UserIdentifier("LL")));
		Assert.assertNull(userService.enableUser(new UserIdentifier("LL")));
		Assert.assertNull(userService.findUserDetails(new UserIdentifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(new UserIdentifier("LL"));
		Assert.assertNull(userService.findUserSummary(new UserIdentifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(new UserIdentifier("LL"));
	}
	
	@Test
	public void testCachingFindResults() {
		UserDetails user1 = new UserDetails(new UserIdentifier("A"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user1", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		UserDetails user2 = new UserDetails(new UserIdentifier("B"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user2", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		UserDetails user3 = new UserDetails(new UserIdentifier("C"), new OrganizationIdentifier("O"), new PersonIdentifier("P"), "user3", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("CRM/ADMIN")));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(user1, user2, user3), 3);
		}).given(delegate).findUserDetails(Mockito.any(UsersFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(user1, user2, user3), 3);
		}).given(delegate).findUserDetails(Mockito.any(UsersFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(user1.asSummary(), user2.asSummary(), user3.asSummary()), 3);
		}).given(delegate).findUserSummaries(Mockito.any(UsersFilter.class), Mockito.any(Paging.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countUsers(Mockito.any(UsersFilter.class));

		Assert.assertEquals(3l, userService.countUsers(new UsersFilter()));

		/* find users with paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUserDetails(new UsersFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUserDetails(user1.getUserId()));
		Assert.assertEquals(user1.asSummary(), userService.findUserSummary(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserDetailsByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUserDetails(user2.getUserId()));
		Assert.assertEquals(user2.asSummary(), userService.findUserSummary(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserDetailsByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUserDetails(user3.getUserId()));
		Assert.assertEquals(user3.asSummary(), userService.findUserSummary(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserDetailsByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());

		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUserDetails(new UsersFilter()).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUserDetails(user1.getUserId()));
		Assert.assertEquals(user1.asSummary(), userService.findUserSummary(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserDetailsByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUserDetails(user2.getUserId()));
		Assert.assertEquals(user2.asSummary(), userService.findUserSummary(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserDetailsByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUserDetails(user3.getUserId()));
		Assert.assertEquals(user3.asSummary(), userService.findUserSummary(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserDetailsByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());
		
		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUserDetails(new UsersFilter(), UsersFilter.getDefaultPaging()).getNumberOfElements());

		Assert.assertEquals(user1, userService.findUserDetails(user1.getUserId()));
		Assert.assertEquals(user1.asSummary(), userService.findUserSummary(user1.getUserId()));
		Assert.assertEquals(user1, userService.findUserDetailsByUsername(user1.getUsername()));

		Assert.assertEquals(user2, userService.findUserDetails(user2.getUserId()));
		Assert.assertEquals(user2.asSummary(), userService.findUserSummary(user2.getUserId()));
		Assert.assertEquals(user2, userService.findUserDetailsByUsername(user2.getUsername()));

		Assert.assertEquals(user3, userService.findUserDetails(user3.getUserId()));
		Assert.assertEquals(user3.asSummary(), userService.findUserSummary(user3.getUserId()));
		Assert.assertEquals(user3, userService.findUserDetailsByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetails(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserDetailsByUsername(Mockito.anyString());
		
		/* find users with default paging and ensure cached results */
		cacheManager.getCache("users").clear();
		Assert.assertEquals(3, userService.findUserSummaries(new UsersFilter(), UsersFilter.getDefaultPaging()).getNumberOfElements());

		Assert.assertEquals(user1.asSummary(), userService.findUserSummary(user1.getUserId()));
		Assert.assertNull(userService.findUserDetails(user1.getUserId()));
		Assert.assertNull(userService.findUserDetailsByUsername(user1.getUsername()));

		Assert.assertEquals(user2.asSummary(), userService.findUserSummary(user2.getUserId()));
		Assert.assertNull(userService.findUserDetails(user2.getUserId()));
		Assert.assertNull(userService.findUserDetailsByUsername(user2.getUsername()));

		Assert.assertEquals(user3.asSummary(), userService.findUserSummary(user3.getUserId()));
		Assert.assertNull(userService.findUserDetails(user3.getUserId()));
		Assert.assertNull(userService.findUserDetailsByUsername(user3.getUsername()));

		BDDMockito.verify(delegate, Mockito.times(3)).findUserDetails(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findUserSummary(Mockito.any(UserIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(3)).findUserDetailsByUsername(Mockito.anyString());
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
