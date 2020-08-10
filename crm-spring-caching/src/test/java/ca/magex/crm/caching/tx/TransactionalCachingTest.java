package ca.magex.crm.caching.tx;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class })
public class TransactionalCachingTest {

	@Autowired private CacheManager cacheManager;
	@Autowired private PlatformTransactionManager txManager;

	@Test
	public void testEventualCachingFunction() {
		TransactionTemplate tx = new TransactionTemplate(txManager);
		CacheTemplate cache = new CacheTemplate(cacheManager, "testCache");

		/* commit */
		tx.executeWithoutResult((status) -> {
			cache.put(List.of(Pair.of("key1", "value1")));
		});
		Assert.assertEquals("value1", cache.getIfPresent("key1"));

		/* commit multiple updates */
		tx.executeWithoutResult((status) -> {
			cache.put(List.of(Pair.of("key1", "value2")));

			cache.put(List.of(Pair.of("key1", "value3")));
		});
		Assert.assertEquals("value3", cache.getIfPresent("key1"));

		/* rollback */
		tx.executeWithoutResult((status) -> {
			cache.put(List.of(Pair.of("key2", "value2")));
			status.setRollbackOnly();
		});

		Assert.assertNull(cache.getIfPresent("key2"));
	}

	@Test
	public void testImmediateCachingFunctions() {
		TransactionTemplate tx = new TransactionTemplate(txManager);
		CacheTemplate cache = new CacheTemplate(cacheManager, "testCache");

		/* put if absent is immediate and will not rollback */
		tx.executeWithoutResult((status) -> {
			cache.putIfAbsent(List.of(Pair.of("key1", "value1")));
			status.setRollbackOnly();
		});
		Assert.assertEquals("value1", cache.getIfPresent("key1"));
	}
}
