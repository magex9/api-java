package ca.magex.crm.caching;

import java.util.List;
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

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.LanguageIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmOptionServiceCachingDelegateTests {

	@Autowired private CrmOptionService delegate;
	@Autowired private CacheManager cacheManager;

	private CacheTemplate cacheTemplate;
	private CrmOptionServiceCachingDelegate optionService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Options);
		optionService = new CrmOptionServiceCachingDelegate(delegate, cacheTemplate);
	}
	
	@Test
	public void testCacheNewOption() {
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			return new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, name, 100L);
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());
		
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			return new Option(new LanguageIdentifier(name.getCode()), invocation.getArgument(0), Type.LANGUAGE, Status.ACTIVE, Option.IMMUTABLE, name, 100L);
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.LANGUAGE), Mockito.any());
		
		Option countryOption = optionService.createOption(null, Type.COUNTRY, new Localized("PT", "Petoria", "Pètorie"));
		BDDMockito.verify(delegate, Mockito.times(1)).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());

		/* should have added the details to the cache */
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());

		/* should have added the code to the cache */
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
		
		Option languageOption = optionService.createOption(null, Type.LANGUAGE, new Localized("PT", "Petoria", "Pètorie"));
		BDDMockito.verify(delegate, Mockito.times(1)).createOption(Mockito.isNull(), Mockito.eq(Type.LANGUAGE), Mockito.any());

		/* should have added the details to the cache */
		Assert.assertEquals(languageOption, optionService.findOption(languageOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());

		/* should have added the code to the cache */
		Assert.assertEquals(languageOption, optionService.findOptionByCode(Type.LANGUAGE, languageOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testCacheNewOptionFromPrototype() {
		BDDMockito.willAnswer((invocation) -> {
			Option option = invocation.getArgument(0);
			return new Option(new CountryIdentifier(option.getName().getCode()), option.getParentId(), option.getType(), option.getStatus(), option.getMutable(), option.getName(), 100L);
		}).given(delegate).createOption(Mockito.any(Option.class));
		
		Option countryOption = optionService.createOption(new Option(null, null, Type.COUNTRY, Status.ACTIVE, Option.MUTABLE, new Localized("PT", "Petoria", "Pètorie"), 100L));
		BDDMockito.verify(delegate, Mockito.times(1)).createOption(Mockito.any(Option.class));

		/* should have added the details to the cache */
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());

		/* should have added the code to the cache */
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testCacheExistingOptionById() {
		BDDMockito.willAnswer((invocation) -> {
			return new Option(invocation.getArgument(0), null, Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, new Localized("PT", "Petoria", "Pètorie"), 100L);
		}).given(delegate).findOption(Mockito.any(OptionIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Option countryOption = optionService.findOption(new CountryIdentifier("PT"));
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(1)).findOption(Mockito.any(OptionIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());		
	}
	
	@Test
	public void testCacheExistingOptionByCode() {
		BDDMockito.willAnswer((invocation) -> {
			return new Option(new CountryIdentifier(invocation.getArgument(1)), null, Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, new Localized("PT", "Petoria", "Pètorie"), 100L);
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Option countryOption = optionService.findOptionByCode(Type.COUNTRY, "PT");
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any(OptionIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(1)).findOptionByCode(Mockito.any(), Mockito.any());		
	}
	
	@Test
	public void testCacheNonExistantOptionById() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findOption(Mockito.any(OptionIdentifier.class));
		
		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(optionService.findOption(new CountryIdentifier("PT")));
		Assert.assertNull(optionService.findOption(new CountryIdentifier("PT")));
		BDDMockito.verify(delegate, Mockito.times(1)).findOption(Mockito.any(OptionIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantOptionByCode() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(optionService.findOptionByCode(Type.COUNTRY, "PT"));
		Assert.assertNull(optionService.findOptionByCode(Type.COUNTRY, "PT"));
		BDDMockito.verify(delegate, Mockito.times(1)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testDisableCachedOption() {
		final AtomicReference<Option> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			reference.set(new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, name, 100L));
			return reference.get();
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disableOption(Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOption(Mockito.any());
		
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		Option countryOption = optionService.createOption(null, Type.COUNTRY, new Localized("PT", "Petoria", "Pètorie"));
		Assert.assertEquals(reference.get(), countryOption);
		/* ensure the option is cached */
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* make sure the option is cached after the disable */
		countryOption = optionService.disableOption(countryOption.getOptionId());
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* disable non existent option (should cache the fact that it doesn't exist) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disableOption(new CountryIdentifier("JJ"));
		Assert.assertNull(optionService.disableOption(new CountryIdentifier("JJ")));
		Assert.assertNull(optionService.findOption(new CountryIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertNull(optionService.findOptionByCode(Type.COUNTRY, new CountryIdentifier("JJ").getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testEnableCachedOption() {
		final AtomicReference<Option> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			reference.set(new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, name, 100L));
			return reference.get();
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).enableOption(Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOption(Mockito.any());
		
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		Option countryOption = optionService.createOption(null, Type.COUNTRY, new Localized("PT", "Petoria", "Pètorie"));
		Assert.assertEquals(reference.get(), countryOption);
		/* ensure the option is cached */
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* make sure the option is cached after the enable */
		countryOption = optionService.enableOption(countryOption.getOptionId());
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* enable non existent option (should cache the fact that it doesn't exist) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableOption(new CountryIdentifier("JJ"));
		Assert.assertNull(optionService.enableOption(new CountryIdentifier("JJ")));
		Assert.assertNull(optionService.findOption(new CountryIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertNull(optionService.findOptionByCode(Type.COUNTRY, new CountryIdentifier("JJ").getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testUpdateCachedOption() {
		final AtomicReference<Option> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			reference.set(new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, name, 100L));
			return reference.get();
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updateOptionName(Mockito.any(), Mockito.any());
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOption(Mockito.any());
		
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		Option countryOption = optionService.createOption(null, Type.COUNTRY, new Localized("PT", "Petoria", "Pètorie"));
		Assert.assertEquals(reference.get(), countryOption);
		/* ensure the option is cached */
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* make sure the option is cached after the update */
		countryOption = optionService.updateOptionName(countryOption.getOptionId(), new Localized(countryOption.getCode(), "Petoria2", "Pètorie2"));
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* enable non existent option (should cache the fact that it doesn't exist) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enableOption(new CountryIdentifier("JJ"));
		Assert.assertNull(optionService.enableOption(new CountryIdentifier("JJ")));
		Assert.assertNull(optionService.findOption(new CountryIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		Assert.assertNull(optionService.findOptionByCode(Type.COUNTRY, new CountryIdentifier("JJ").getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void testCachingFindDetailsResults() {
		Option country1 = new Option(new CountryIdentifier("A"), null, Type.COUNTRY, Status.ACTIVE, Option.MUTABLE, new Localized("A", "a_en", "a_fr"), 100L);
		Option country2 = new Option(new CountryIdentifier("B"), null, Type.COUNTRY, Status.ACTIVE, Option.MUTABLE, new Localized("B", "b_en", "b_fr"), 100L);
		Option country3 = new Option(new CountryIdentifier("C"), null, Type.COUNTRY, Status.ACTIVE, Option.MUTABLE, new Localized("C", "c_en", "c_fr"), 100L);

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(country1, country2, country3), 3);
		}).given(delegate).findOptions(Mockito.any(), Mockito.any());

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countOptions(Mockito.any());

		Assert.assertEquals(3l, optionService.countOptions(new OptionsFilter()));

		/* find organization details with paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Options).clear();
		Assert.assertEquals(3, optionService.findOptions(new OptionsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(country1, optionService.findOption(country1.getOptionId()));
		Assert.assertEquals(country1, optionService.findOptionByCode(Type.COUNTRY, country1.getCode()));
		
		Assert.assertEquals(country2, optionService.findOption(country2.getOptionId()));
		Assert.assertEquals(country2, optionService.findOptionByCode(Type.COUNTRY, country2.getCode()));
		
		Assert.assertEquals(country3, optionService.findOption(country3.getOptionId()));
		Assert.assertEquals(country3, optionService.findOptionByCode(Type.COUNTRY, country3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());

		/* find organization details with default paging and ensure cached results */
		cacheManager.getCache(CachingConfig.Caches.Options).clear();
		Assert.assertEquals(3, optionService.findOptions(new OptionsFilter()).getNumberOfElements());

		Assert.assertEquals(country1, optionService.findOption(country1.getOptionId()));
		Assert.assertEquals(country1, optionService.findOptionByCode(Type.COUNTRY, country1.getCode()));
		
		Assert.assertEquals(country2, optionService.findOption(country2.getOptionId()));
		Assert.assertEquals(country2, optionService.findOptionByCode(Type.COUNTRY, country2.getCode()));
		
		Assert.assertEquals(country3, optionService.findOption(country3.getOptionId()));
		Assert.assertEquals(country3, optionService.findOptionByCode(Type.COUNTRY, country3.getCode()));

		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any());
		BDDMockito.verify(delegate, Mockito.times(0)).findOptionByCode(Mockito.any(), Mockito.any());
	}
}
