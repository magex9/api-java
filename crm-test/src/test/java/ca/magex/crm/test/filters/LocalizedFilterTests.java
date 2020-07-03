package ca.magex.crm.test.filters;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.filters.LocalizedFilter;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Localized;

public class LocalizedFilterTests {

	@Test
	public void testSortOptions() {
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_ASC));
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_DESC));
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_ASC));
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_DESC));
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_ASC));
		Assert.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_DESC));

		/* default sort should be code ascending */
		Assert.assertEquals(LocalizedFilter.SORT_CODE_ASC, LocalizedFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assert.assertEquals(LocalizedFilter.getDefaultSort(), LocalizedFilter.getDefaultPaging().getSort());
	}
	
	private void assertFilterEquals(LocalizedFilter expected, LocalizedFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	public void testFilterConstructs() {
		LocalizedFilter filter = new LocalizedFilter();
		Assert.assertNull(filter.getEnglishName());
		Assert.assertNull(filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter(null, null, null), filter);

		filter = filter.withEnglishName("english");
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertNull(filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", null, null), filter);

		filter = filter.withFrenchName("french");
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertEquals("french", filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", "french", null), filter);

		filter = filter.withCode("code");
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertEquals("french", filter.getFrenchName());
		Assert.assertEquals("code", filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", "french", "code"), filter);
	}

	@Test
	public void testFilterMapConstructions() {
		LocalizedFilter filter = new LocalizedFilter(Map.of());
		Assert.assertNull(filter.getEnglishName());
		Assert.assertNull(filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter(null, null, null), filter);

		filter = new LocalizedFilter(Map.of("englishName", "english"));
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertNull(filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", null, null), filter);

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french"));
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertEquals("french", filter.getFrenchName());
		Assert.assertNull(filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", "french", null), filter);

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code"));
		Assert.assertEquals("english", filter.getEnglishName());
		Assert.assertEquals("french", filter.getFrenchName());
		Assert.assertEquals("code", filter.getCode());
		assertFilterEquals(new LocalizedFilter("english", "french", "code"), filter);
	}

	@Test
	public void testApplyFilter() {
		Localized localized = new Localized("code", "english", "french");
		/* default filter should match */
		Assert.assertTrue(new LocalizedFilter().apply(localized));

		/* test english match */
		Assert.assertTrue(new LocalizedFilter().withEnglishName("EN").apply(localized));
		Assert.assertTrue(new LocalizedFilter().withEnglishName("English").apply(localized));
		Assert.assertFalse(new LocalizedFilter().withEnglishName("bobby").apply(localized));

		/* test french match */
		Assert.assertTrue(new LocalizedFilter().withFrenchName("FR").apply(localized));
		Assert.assertTrue(new LocalizedFilter().withFrenchName("French").apply(localized));
		Assert.assertFalse(new LocalizedFilter().withFrenchName("bobby").apply(localized));

		/* test code match */
		Assert.assertTrue(new LocalizedFilter().withCode("CODE").apply(localized));
		Assert.assertFalse(new LocalizedFilter().withCode("TR").apply(localized));
	}
}