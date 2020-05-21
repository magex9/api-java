package ca.magex.json.model;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Test;

import ca.magex.json.util.FormattedStringBuilder;

public class JsonElementTest {

	@Test
	public void testBasicDataElement() throws Exception {
		JsonElement el = new JsonElement();
		assertEquals(JsonElement.UNDEFINED, el);
		assertEquals("", el.mid());
		assertEquals("", new JsonElement().mid());
	}
	
	@Test
	public void testDataElementMid() throws Exception {
		JsonElement el = new JsonElement("abc");
		assertEquals("abc", el.mid());
	}
	
	@Test
	public void testValidatingKeys() throws Exception {
		try {
			JsonElement.validateKey(null);
			fail("Keys must be not null");
		} catch (IllegalArgumentException e) {
			assertEquals("Key cannot be null", e.getMessage());
		}
		try {
			JsonElement.validateKey("");
			fail("Keys must not be blank");
		} catch (IllegalArgumentException e) { 
			assertEquals("Invalid key: ", e.getMessage());
		}
	}
	
	@Test
	public void testSpecialKeys() throws Exception {
		assertFalse(isValidKey(null));
		assertFalse(isValidKey(""));
		assertFalse(isValidKey(" "));
		assertFalse(isValidKey("\t"));
		assertFalse(isValidKey("\n"));
	}
	
	@Test
	public void testRefKey() throws Exception {
		assertTrue(isValidKey("$ref"));
	}

	@Test
	public void testDateKey() throws Exception {
		assertFalse(isValidKey("2020-01-01"));
	}
	
	@Test
	public void testAlphaNumericKeys() throws Exception {
		assertTrue(isValidKey("ABCD"));
		assertTrue(isValidKey("ABC132"));
		assertTrue(isValidKey("abcdEFTHI01930"));
		assertTrue(isValidKey(buildString(10, "abc")));
		assertTrue(isValidKey(buildString(100, "abcde")));
		assertTrue(isValidKey(buildString(200, "abcde")));
		assertTrue(isValidKey(buildString(255, "abcde")));
		assertFalse(isValidKey(buildString(256, "abcde")));
		assertFalse(isValidKey(buildString(300, "abcde")));
		assertFalse(isValidKey(buildString(400, "abcde")));
	}
	
	public String buildString(int length, String text) {
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(text);
		}
		return sb.substring(0, length);
	}
	
	public boolean isValidKey(String key) {
		try {
			JsonElement.validateKey(key);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
	
	@Test
	public void testNullElements() throws Exception {
		assertEquals(JsonElement.UNDEFINED.mid(), new JsonText(null).mid());
		assertEquals(JsonElement.UNDEFINED.mid(), new JsonNumber(null).mid());
		assertEquals(JsonElement.UNDEFINED.mid(), new JsonBoolean(null).mid());
		assertEquals(JsonElement.UNDEFINED.toString(), new JsonText(null).toString());
		assertEquals(JsonElement.UNDEFINED.toString(), new JsonNumber(null).toString());
		assertEquals(JsonElement.UNDEFINED.toString(), new JsonBoolean(null).toString());
	}

	@Test
	public void testCastNull() throws Exception {
		assertEquals(JsonElement.UNDEFINED, JsonElement.cast(null));
	}
	
	@Test
	public void testCastDataElement() throws Exception {
		assertEquals(JsonElement.class, JsonElement.cast(JsonElement.UNDEFINED).getClass());
		assertEquals(JsonText.class, JsonElement.cast(new JsonText("test")).getClass());
		assertEquals(JsonNumber.class, JsonElement.cast(new JsonNumber(10)).getClass());
		assertEquals(JsonBoolean.class, JsonElement.cast(new JsonBoolean(true)).getClass());
		assertEquals(JsonArray.class, JsonElement.cast(new JsonArray()).getClass());
	}
	
	@Test
	public void testCastNumber() throws Exception {
		assertEquals(JsonNumber.class, JsonElement.cast(10).getClass());
		assertEquals(JsonNumber.class, JsonElement.cast(0.05).getClass());
	}
	
	@Test
	public void testCastText() throws Exception {
		assertEquals(JsonText.class, JsonElement.cast("Hello").getClass());
		assertEquals(JsonText.class, JsonElement.cast("Français").getClass());
	}
	
	@Test
	public void testCastBoolean() throws Exception {
		assertEquals(JsonBoolean.class, JsonElement.cast(true).getClass());
		assertEquals(JsonBoolean.class, JsonElement.cast(false).getClass());
		assertEquals(JsonBoolean.class, JsonElement.cast(Boolean.TRUE).getClass());
		assertEquals(JsonBoolean.class, JsonElement.cast(Boolean.FALSE).getClass());
	}
	
	@Test
	public void testCastInvalid() throws Exception {
		try {
			JsonElement.cast(new Object());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
		try {
			JsonElement.cast(new Date());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
		try {
			JsonElement.cast(new FormattedStringBuilder());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
	}
	
	@Test
	public void testUnwrapNull() throws Exception {
		assertNull(JsonElement.unwrap(null));
	}
	
	@Test
	public void testUnwrapJsonText() throws Exception {
		assertEquals("text", JsonElement.unwrap(new JsonText("text")));
		assertEquals(null, JsonElement.unwrap(new JsonText(null)));
		assertEquals("Français", JsonElement.unwrap(new JsonText("Français")));
	}
	
	@Test
	public void testUnwrapJsonNumber() throws Exception {
		assertEquals(10, JsonElement.unwrap(new JsonNumber(10)));
		assertEquals(-10, JsonElement.unwrap(new JsonNumber(-10)));
		assertEquals(0.005, JsonElement.unwrap(new JsonNumber(0.005)));
		assertEquals(Long.MAX_VALUE, JsonElement.unwrap(new JsonNumber(Long.MAX_VALUE)));
		assertEquals(Double.MIN_VALUE, JsonElement.unwrap(new JsonNumber(Double.MIN_VALUE)));
	}
	
	@Test
	public void testUnwrapBoolean() throws Exception {
		assertEquals(true, JsonElement.unwrap(new JsonBoolean(true)));
		assertEquals(false, JsonElement.unwrap(new JsonBoolean(false)));
		assertEquals(true, JsonElement.unwrap(new JsonBoolean(Boolean.TRUE)));
		assertEquals(false, JsonElement.unwrap(new JsonBoolean(Boolean.FALSE)));
	}
	
	@Test
	public void testUnwrapInvalid() throws Exception {
		try {
			JsonElement.unwrap(new Object());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
		try {
			JsonElement.unwrap(new Date());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
		try {
			JsonElement.unwrap(LocalDateTime.now());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
		try {
			JsonElement.unwrap(new FormattedStringBuilder());
			fail("Invalid object");
		} catch (IllegalArgumentException e) { }
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new JsonElement().hashCode(), new JsonElement().hashCode());
		assertEquals(new JsonNumber(10).hashCode(), new JsonNumber(10).hashCode());
		assertEquals(new JsonBoolean(true).hashCode(), new JsonBoolean(true).hashCode());
		assertEquals(new JsonText("text").hashCode(), new JsonText("text").hashCode());
		
		assertNotEquals(new JsonNumber(10).hashCode(), new JsonNumber(-10).hashCode());
		assertNotEquals(new JsonBoolean(true).hashCode(), new JsonBoolean(false).hashCode());
		assertNotEquals(new JsonText("text").hashCode(), new JsonText("other").hashCode());
	}
	
	@Test
	public void testToStringBasics() throws Exception {
		assertEquals("null", new JsonElement().toString());
		assertEquals("10", new JsonNumber(10).toString());
		assertEquals("true", new JsonBoolean(true).toString());
		assertEquals("\"text\"", new JsonText("text").toString());
	}
	
	@Test
	public void testToStringify() throws Exception {
		assertEquals("null", JsonFormatter.compact(new JsonElement()));
		assertEquals("10", JsonFormatter.compact(new JsonNumber(10)));
		assertEquals("true", JsonFormatter.compact(new JsonBoolean(true)));
		assertEquals("\"text\"", JsonFormatter.compact(new JsonText("text")));
	}
	
}
