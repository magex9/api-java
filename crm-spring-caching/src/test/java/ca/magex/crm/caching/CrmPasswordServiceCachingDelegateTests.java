package ca.magex.crm.caching;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.caching.util.CrmCacheKeyGenerator;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmPasswordServiceCachingDelegateTests {

	@Autowired private CrmPasswordService delegate;
	@Autowired private CacheManager cacheManager;

	private CacheTemplate cacheTemplate;
	private CrmPasswordServiceCachingDelegate passwordService;
	
	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Users);
		passwordService = new CrmPasswordServiceCachingDelegate(delegate, cacheTemplate);
	}
	
	@Test
	public void testCacheFindUser() {
		UserDetails user = new UserDetails(
				new UserIdentifier("u1"), 
				new OrganizationIdentifier("o1"), 
				new PersonIdentifier("p1"), 
				"user", 
				Status.ACTIVE, 
				List.of(new AuthenticationRoleIdentifier("SYS/ADM")), 
				System.currentTimeMillis());
		BDDMockito.willReturn(user).given(delegate).findUser(Mockito.anyString());
		
		Assert.assertEquals(user, passwordService.findUser("user"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.anyString());
		
		/* second call to find user should not call through to the delegate */
		Assert.assertEquals(user, passwordService.findUser("user"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.anyString());
		
		/* ensure it was cached under all 4 keys */
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateDetailsKey(user.getUserId()));
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateSummaryKey(user.getUserId()));
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateUsernameDetailsKey(user.getUsername()));
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateUsernameSummaryKey(user.getUsername()));
	}
	
	@Test
	public void testCacheNoUserFound() {
		BDDMockito.willReturn(null).given(delegate).findUser(Mockito.anyString());
		Assert.assertNull(passwordService.findUser("user"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.anyString());
		
		Assert.assertNull(passwordService.findUser("user"));
		BDDMockito.verify(delegate, Mockito.times(1)).findUser(Mockito.anyString());
		
		/* ensure it was cached under both keys */
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateUsernameDetailsKey("user"));
		cacheManager.getCacheNames().contains(CrmCacheKeyGenerator.getInstance().generateUsernameSummaryKey("user"));		
	}
	
	@Test
	public void testCacheEncodedPassword() {		
		BDDMockito.willReturn(false).given(delegate).isTempPassword("batman");
		BDDMockito.willReturn("ABC").given(delegate).getEncodedPassword("batman");
		BDDMockito.willReturn("BRUCE").given(delegate).generateTemporaryPassword("batman");
		BDDMockito.willReturn("Wayne").given(delegate).encodePassword("BRUCE");
		
		Assert.assertEquals("ABC", passwordService.getEncodedPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).getEncodedPassword(Mockito.anyString());
		
		Assert.assertFalse(passwordService.isTempPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).isTempPassword(Mockito.anyString());
		
		/* second call to find user should not call through to the delegate */
		Assert.assertEquals("ABC", passwordService.getEncodedPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).getEncodedPassword(Mockito.anyString());
		
		Assert.assertFalse(passwordService.isTempPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).isTempPassword(Mockito.anyString());
		
		/* call to update password should reset cache */
		passwordService.updatePassword("batman", "EFG");
		Assert.assertEquals("EFG", passwordService.getEncodedPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).getEncodedPassword(Mockito.anyString());
		
		Assert.assertFalse(passwordService.isTempPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).isTempPassword(Mockito.anyString());
		
		/* call to generate temporary should reset cache */
		Assert.assertEquals("BRUCE", passwordService.generateTemporaryPassword("batman"));
		Assert.assertEquals("Wayne", passwordService.getEncodedPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).getEncodedPassword(Mockito.anyString());
		
		Assert.assertTrue(passwordService.isTempPassword("batman"));
		BDDMockito.verify(delegate, Mockito.times(1)).isTempPassword(Mockito.anyString());
	}
	
	@Test
	public void testPassThrough() {
		BDDMockito.willReturn(true).given(delegate).isExpiredPassword(Mockito.anyString());
		BDDMockito.willReturn(true).given(delegate).verifyPassword(Mockito.anyString(), Mockito.anyString());
		BDDMockito.willReturn("A").given(delegate).encodePassword(Mockito.anyString());
		
		Assert.assertTrue(passwordService.isExpiredPassword("A"));
		Assert.assertTrue(passwordService.isExpiredPassword("A"));
		Assert.assertTrue(passwordService.isExpiredPassword("A"));
		Assert.assertTrue(passwordService.isExpiredPassword("A"));
		BDDMockito.verify(delegate, Mockito.times(4)).isExpiredPassword("A");
		
		Assert.assertTrue(passwordService.verifyPassword("A", "A"));
		Assert.assertTrue(passwordService.verifyPassword("A", "A"));
		Assert.assertTrue(passwordService.verifyPassword("A", "A"));
		Assert.assertTrue(passwordService.verifyPassword("A", "A"));
		BDDMockito.verify(delegate, Mockito.times(4)).verifyPassword("A", "A");
		
		Assert.assertEquals("A", passwordService.encodePassword("A"));
		Assert.assertEquals("A", passwordService.encodePassword("A"));
		Assert.assertEquals("A", passwordService.encodePassword("A"));
		Assert.assertEquals("A", passwordService.encodePassword("A"));
		BDDMockito.verify(delegate, Mockito.times(4)).encodePassword("A");
	}
}