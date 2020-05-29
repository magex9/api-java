package ca.magex.crm.api.filters;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ca.magex.crm.api.system.Localized;

@TestInstance(Lifecycle.PER_METHOD)
public class LocalizedFilterTests {

	@Test
	public void testSortOptions() {
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_ASC));
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_DESC));
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_ASC));
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_DESC));
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_ASC));
		Assertions.assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_DESC));

		/* default sort should be code ascending */
		Assertions.assertEquals(LocalizedFilter.SORT_CODE_ASC, LocalizedFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assertions.assertEquals(LocalizedFilter.getDefaultSort(), LocalizedFilter.getDefaultPaging().getSort());
	}

	@Test
	public void testFilterConstructs() {
		LocalizedFilter filter = new LocalizedFilter();
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter(null, null, null), filter);
		Assertions.assertEquals(new LocalizedFilter(null, null, null).hashCode(), filter.hashCode());

		filter = filter.withEnglishName("english");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", null, null), filter);
		Assertions.assertEquals(new LocalizedFilter("english", null, null).hashCode(), filter.hashCode());

		filter = filter.withFrenchName("french");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", "french", null), filter);
		Assertions.assertEquals(new LocalizedFilter("english", "french", null).hashCode(), filter.hashCode());

		filter = filter.withCode("code");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\"}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", "french", "code"), filter);
		Assertions.assertEquals(new LocalizedFilter("english", "french", "code").hashCode(), filter.hashCode());
	}

	@Test
	public void testFilterMapConstructions() {
		LocalizedFilter filter = new LocalizedFilter(Map.of());
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter(null, null, null), filter);
		Assertions.assertEquals(new LocalizedFilter(null, null, null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", null, null), filter);
		Assertions.assertEquals(new LocalizedFilter("english", null, null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", "french", null), filter);
		Assertions.assertEquals(new LocalizedFilter("english", "french", null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\"}", filter.toString());
		Assertions.assertEquals(new LocalizedFilter("english", "french", "code"), filter);
		Assertions.assertEquals(new LocalizedFilter("english", "french", "code").hashCode(), filter.hashCode());
	}

	@Test
	public void testApplyFilter() {
		Localized localized = new Localized("code", "english", "french");
		/* default filter should match */
		Assertions.assertTrue(new LocalizedFilter().apply(localized));

		/* test english match */
		Assertions.assertTrue(new LocalizedFilter().withEnglishName("EN").apply(localized));
		Assertions.assertTrue(new LocalizedFilter().withEnglishName("English").apply(localized));
		Assertions.assertFalse(new LocalizedFilter().withEnglishName("bobby").apply(localized));

		/* test french match */
		Assertions.assertTrue(new LocalizedFilter().withFrenchName("FR").apply(localized));
		Assertions.assertTrue(new LocalizedFilter().withFrenchName("French").apply(localized));
		Assertions.assertFalse(new LocalizedFilter().withFrenchName("bobby").apply(localized));

		/* test code match */
		Assertions.assertTrue(new LocalizedFilter().withCode("CODE").apply(localized));
		Assertions.assertFalse(new LocalizedFilter().withCode("TR").apply(localized));
	}
}