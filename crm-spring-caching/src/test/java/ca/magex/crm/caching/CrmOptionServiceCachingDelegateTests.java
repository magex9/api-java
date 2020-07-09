package ca.magex.crm.caching;

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

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.services.CrmOptionService;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
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
			return new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, name);
		}).given(delegate).createOption(Mockito.isNull(), Mockito.eq(Type.COUNTRY), Mockito.any());
		
		BDDMockito.willAnswer((invocation) -> {
			Localized name = invocation.getArgument(2);
			return new Option(new CountryIdentifier(name.getCode()), invocation.getArgument(0), Type.LANGUAGE, Status.ACTIVE, Option.IMMUTABLE, name);
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
			return new Option(new CountryIdentifier(option.getName().getCode()), option.getParentId(), option.getType(), option.getStatus(), option.getMutable(), option.getName());
		}).given(delegate).createOption(Mockito.any(Option.class));
		
		Option countryOption = optionService.createOption(new Option(null, null, Type.COUNTRY, Status.ACTIVE, Option.MUTABLE, new Localized("PT", "Petoria", "Pètorie")));
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
			return new Option(invocation.getArgument(0), null, Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, new Localized("PT", "Petoria", "Pètorie"));
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
			return new Option(new CountryIdentifier(invocation.getArgument(1)), null, Type.COUNTRY, Status.ACTIVE, Option.IMMUTABLE, new Localized("PT", "Petoria", "Pètorie"));
		}).given(delegate).findOptionByCode(Mockito.any(), Mockito.any());

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Option countryOption = optionService.findOptionByCode(Type.COUNTRY, "PT");
		Assert.assertEquals(countryOption, optionService.findOption(countryOption.getOptionId()));
		Assert.assertEquals(countryOption, optionService.findOptionByCode(Type.COUNTRY, countryOption.getCode()));
		BDDMockito.verify(delegate, Mockito.times(0)).findOption(Mockito.any(OptionIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(1)).findOptionByCode(Mockito.any(), Mockito.any());		
	}
}
