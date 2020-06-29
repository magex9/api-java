package ca.magex.crm.test.filters;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import ca.magex.crm.api.filters.LocalizedFilter;
import ca.magex.crm.api.system.Localized;

public class LocalizedFilterTests {

	@Test
	public void testSortOptions() {
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_ASC));
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_ENGLISH_DESC));
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_ASC));
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_FRENCH_DESC));
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_ASC));
		assertTrue(LocalizedFilter.getSortOptions().contains(LocalizedFilter.SORT_CODE_DESC));

		/* default sort should be code ascending */
		assertEquals(LocalizedFilter.SORT_CODE_ASC, LocalizedFilter.getDefaultSort());

		/* default paging, should use default sort */
		assertEquals(LocalizedFilter.getDefaultSort(), LocalizedFilter.getDefaultPaging().getSort());
	}

	@Test
	public void testFilterConstructs() {
		LocalizedFilter filter = new LocalizedFilter();
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter(null, null, null), filter);
		assertEquals(new LocalizedFilter(null, null, null).hashCode(), filter.hashCode());

		filter = filter.withEnglishName("english");
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter("english", null, null), filter);
		assertEquals(new LocalizedFilter("english", null, null).hashCode(), filter.hashCode());

		filter = filter.withFrenchName("french");
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter("english", "french", null), filter);
		assertEquals(new LocalizedFilter("english", "french", null).hashCode(), filter.hashCode());

		filter = filter.withCode("code");
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\"}", filter.toString());
		assertEquals(new LocalizedFilter("english", "french", "code"), filter);
		assertEquals(new LocalizedFilter("english", "french", "code").hashCode(), filter.hashCode());
	}

	@Test
	public void testFilterMapConstructions() {
		LocalizedFilter filter = new LocalizedFilter(Map.of());
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter(null, null, null), filter);
		assertEquals(new LocalizedFilter(null, null, null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english"));
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter("english", null, null), filter);
		assertEquals(new LocalizedFilter("english", null, null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french"));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null}", filter.toString());
		assertEquals(new LocalizedFilter("english", "french", null), filter);
		assertEquals(new LocalizedFilter("english", "french", null).hashCode(), filter.hashCode());

		filter = new LocalizedFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code"));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\"}", filter.toString());
		assertEquals(new LocalizedFilter("english", "french", "code"), filter);
		assertEquals(new LocalizedFilter("english", "french", "code").hashCode(), filter.hashCode());
	}

	@Test
	public void testApplyFilter() {
		Localized localized = new Localized("code", "english", "french");
		/* default filter should match */
		assertTrue(new LocalizedFilter().apply(localized));

		/* test english match */
		assertTrue(new LocalizedFilter().withEnglishName("EN").apply(localized));
		assertTrue(new LocalizedFilter().withEnglishName("English").apply(localized));
		assertFalse(new LocalizedFilter().withEnglishName("bobby").apply(localized));

		/* test french match */
		assertTrue(new LocalizedFilter().withFrenchName("FR").apply(localized));
		assertTrue(new LocalizedFilter().withFrenchName("French").apply(localized));
		assertFalse(new LocalizedFilter().withFrenchName("bobby").apply(localized));

		/* test code match */
		assertTrue(new LocalizedFilter().withCode("CODE").apply(localized));
		assertFalse(new LocalizedFilter().withCode("TR").apply(localized));
	}
}