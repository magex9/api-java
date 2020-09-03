package ca.magex.crm.test.utils;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.utils.StringEscapeUtils;

/**
 * Tests for the String Escape Utils Extensions
 * 
 * @author Jonny
 */
public class StringEscapeUtilsTests {

	@Test
	public void testEscapeRegex() {
		String value = "[A-Z]";
		String escaped = StringEscapeUtils.escapeRegex(value);		
		
		/* ensure that the original value matches the escaped value */
		Assert.assertTrue(value.matches(escaped));
	}
}