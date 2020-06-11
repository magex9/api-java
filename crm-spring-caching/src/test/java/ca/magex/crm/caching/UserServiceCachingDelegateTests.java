package ca.magex.crm.caching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.test.config.MockConfig;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, TestConfig.class, MockConfig.class })
@ActiveProfiles(profiles = { MagexCrmProfiles.CRM_NO_AUTH })
public class UserServiceCachingDelegateTests {

	@Autowired private CrmUserService delegate;
	@Autowired private CacheManager cacheManager;
	@Autowired @Qualifier("UserServiceCachingDelegate") private CrmUserService userService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
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
}
