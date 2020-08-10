package ca.magex.json.util;

import static org.junit.Assert.*;

import static ca.magex.json.util.StringConverter.*;

import org.junit.Test;

public class StringConverterTests {

	@Test
	public void testFirstLetterUpperCase() throws Exception {
		assertNull(firstLetterUpperCase(null));
		assertEquals("A", firstLetterUpperCase("A"));
		assertEquals("A", firstLetterUpperCase("a"));
		assertEquals("Test", firstLetterUpperCase("test"));
		assertEquals("TEST", firstLetterUpperCase("TEST"));
	}
	
	@Test
	public void testFirstLetterLowerCase() throws Exception {
		assertNull(firstLetterLowerCase(null));
		assertEquals("a", firstLetterLowerCase("A"));
		assertEquals("a", firstLetterLowerCase("a"));
		assertEquals("test", firstLetterLowerCase("Test"));
		assertEquals("tEST", firstLetterLowerCase("TEST"));
	}
	
	@Test
	public void testUpperToLowerCase() throws Exception {
		try {
			fail(upperToLowerCase(null));
		} catch (IllegalArgumentException expected) { }
		try {
			fail(upperToLowerCase(""));
		} catch (IllegalArgumentException expected) { }
		assertEquals("test", upperToLowerCase("TEST"));
		assertEquals("test-string", upperToLowerCase("TEST_STRING"));
		assertEquals("test-42-b", upperToLowerCase("TEST_42_B"));
	}
	
}
