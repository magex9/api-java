package ca.magex.json.model;

import static org.junit.Assert.*;
import static ca.magex.json.model.JsonFormatter.*;

import org.junit.Test;

public class JsonFormatterTest {

	@Test
	public void testIndentedFormatter() throws Exception {
		JsonFormatter formatter = new JsonFormatter(true);
		assertTrue(formatter.isIndented());
		assertEquals(0, formatter.getIndentation().intValue());
		assertEquals(1, formatter.indent().getIndentation().intValue());
		assertEquals(1, formatter.getIndentation().intValue());
		assertEquals(2, formatter.indent().getIndentation().intValue());
		assertEquals(2, formatter.getIndentation().intValue());
		assertEquals(1, formatter.unindent().getIndentation().intValue());
		assertEquals(1, formatter.getIndentation().intValue());
		assertEquals(0, formatter.unindent().getIndentation().intValue());
		assertEquals(0, formatter.getIndentation().intValue());
	}
	
	@Test
	public void testUnindentedFormatter() throws Exception {
		JsonFormatter formatter = new JsonFormatter(false);
		assertFalse(formatter.isIndented());
		assertNull(formatter.getIndentation());
		assertNull(formatter.indent().getIndentation());
		assertNull(formatter.getIndentation());
		assertNull(formatter.indent().getIndentation());
		assertNull(formatter.getIndentation());
		assertNull(formatter.unindent().getIndentation());
		assertNull(formatter.getIndentation());
		assertNull(formatter.unindent().getIndentation());
		assertNull(formatter.getIndentation());
	}
	
	@Test
	public void testFormattedPrefix() throws Exception {
		JsonFormatter formatter = new JsonFormatter(true);
		assertEquals("", new String(formatter.prefix()));
		assertEquals(new String(INDENT), new String(formatter.indent().prefix()));
		assertEquals(new String(INDENT) + new String(INDENT), new String(formatter.indent().prefix()));
		assertEquals(new String(INDENT) + new String(INDENT), new String(formatter.prefix()));
		assertEquals(new String(INDENT), new String(formatter.setIndentation(1).prefix()));
	}
	
	@Test
	public void testUnFormattedPrefix() throws Exception {
		JsonFormatter formatter = new JsonFormatter(false);
		assertEquals("", new String(formatter.prefix()));
		assertEquals(new String(BLANK), new String(formatter.indent().prefix()));
		assertEquals(new String(BLANK), new String(formatter.indent().prefix()));
		assertEquals(new String(BLANK), new String(formatter.prefix()));
		assertEquals(new String(BLANK), new String(formatter.setIndentation(5).prefix()));
	}
	
	@Test
	public void testNullStreams() throws Exception {
		try {
			new JsonFormatter(true).stream(new JsonElement(), null);
			fail("Should not allow null output streams");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to write to null OutputStream", e.getMessage());
		}
	}
	
	@Test
	public void testStringifyUndefined() throws Exception {
		assertEquals("null", new JsonFormatter(true).stringify(new JsonElement()));
	}
	
	@Test
	public void testStringifyCustomElement() throws Exception {
		try {
			assertEquals("\"test\"", new JsonFormatter(true).stringify(new CustomJsonElement()));
			fail("Cannot parse custom elements");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to write to output stream: class java.io.ByteArrayOutputStream", e.getMessage());
		}
	}
	
	@Test
	public void testFakeOutputStreams() throws Exception {
		try {
			new JsonFormatter(true).stream(new JsonElement(), new FakeOutputStream());
			fail("Cannto parse custom elements");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to write to output stream: class ca.magex.json.model.FakeOutputStream", e.getMessage());
		}
		try {
			new JsonFormatter(true).stream((JsonElement)new JsonNumber(42), new FakeOutputStream());
			fail("Cannto parse custom elements");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to write to output stream: class ca.magex.json.model.FakeOutputStream", e.getMessage());
		}
	}
	
	@Test
	public void testStreamText() throws Exception {
		assertEquals("null", new JsonFormatter(true).stringify(new JsonText(null)));
		assertEquals("\"a\"", new JsonFormatter(true).stringify(new JsonText("a")));
	}
	
	@Test
	public void testStreamNumber() throws Exception {
		assertEquals("null", new JsonFormatter(true).stringify(new JsonNumber(null)));
		assertEquals("55.5", new JsonFormatter(true).stringify(new JsonNumber(55.5)));
	}
	
	@Test
	public void testStreamBoolean() throws Exception {
		assertEquals("null", new JsonFormatter(true).stringify(new JsonBoolean(null)));
		assertEquals("true", new JsonFormatter(true).stringify(new JsonBoolean(true)));
		assertEquals("false", new JsonFormatter(true).stringify(new JsonBoolean(false)));
	}
	
	@Test
	public void testFormattedSingleJsonObjecctNotIndenting() throws Exception {
		JsonObject data = new JsonObject().with("name", "Scott");
		assertEquals("{\"name\": \"Scott\"}", new JsonFormatter(true).stringify(data));
		assertEquals("{\"name\":\"Scott\"}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testFormattedLinkedJsonObjecctNotIndenting() throws Exception {
		JsonObject data = new JsonObject().with("person", new JsonObject().with("name", "Scott"));
		assertEquals("{\n  \"person\": {\"name\": \"Scott\"}\n}", new JsonFormatter(true).stringify(data));
		assertEquals("{\"person\":{\"name\":\"Scott\"}}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testFormattedLinkedJsonObjecctIndenting() throws Exception {
		JsonObject data = new JsonObject().with("person", new JsonObject().with("name", "Scott").with("active", true));
		assertEquals("{\n  \"person\": {\n    \"name\": \"Scott\",\n    \"active\": true\n  }\n}", new JsonFormatter(true).stringify(data));
		assertEquals("{\"person\":{\"name\":\"Scott\",\"active\":true}}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testFormattedMultipleJsonObjectIndenting() throws Exception {
		JsonObject data = new JsonObject().with("name", "Scott").with("active", true);
		assertEquals("{\n  \"name\": \"Scott\",\n  \"active\": true\n}", new JsonFormatter(true).stringify(data));
		assertEquals("{\"name\":\"Scott\",\"active\":true}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testFormattedSingleJsonArrayNotIndenting() throws Exception {
		JsonArray data = new JsonArray().with("Scott");
		assertEquals("[\"Scott\"]", new JsonFormatter(true).stringify(data));
		assertEquals("[\"Scott\"]", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testFormattedMultipleJsonArrayIndenting() throws Exception {
		JsonArray data = new JsonArray().with("Scott", true);
		assertEquals("[\n  \"Scott\",\n  true\n]", new JsonFormatter(true).stringify(data));
		assertEquals("[\"Scott\",true]", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testSimpleJsonLdContext() throws Exception {
		JsonObject data = new JsonObject()
			.with("@context", "http://magex.ca")
			.with("@type", "Person")
			.with("name", "Scott");
		assertEquals("{\"@context\":\"http://magex.ca\",\"@type\":\"Person\",\"name\":\"Scott\"}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testSameJsonLdContext() throws Exception {
		JsonObject data = new JsonObject()
			.with("@context", "http://schema.org")
			.with("@type", "Person")
			.with("name", "Scott")
			.with("address", new JsonObject()
				.with("@context", "http://schema.org")
				.with("@type", "Address")
				.with("city", "Ottawa")
				.with("country", "Canada"));
		assertEquals("{\"@context\":\"http://schema.org\",\"@type\":\"Person\",\"name\":\"Scott\",\"address\":{\"@type\":\"Address\",\"city\":\"Ottawa\",\"country\":\"Canada\"}}", new JsonFormatter(false).stringify(data));
	}
	
	@Test
	public void testDifferentJsonLdContext() throws Exception {
		JsonObject data = new JsonObject()
			.with("@context", "http://schema.org")
			.with("@type", "Person")
			.with("name", "Scott")
			.with("address", new JsonObject()
				.with("@context", "http://schema.org")
				.with("@type", "Address")
				.with("city", "Ottawa")
				.with("country", "Canada"))
			.with("communication", new JsonObject()
				.with("@context", "http://magex.ca")
				.with("@type", "Telephone")
				.with("number", "5556667777"));
		
		String text = new JsonFormatter(false).stringify(data);
		assertEquals("{\"@context\":\"http://schema.org\",\"@type\":\"Person\",\"name\":\"Scott\",\"address\":{\"@type\":\"Address\",\"city\":\"Ottawa\",\"country\":\"Canada\"},\"communication\":{\"@context\":\"http://magex.ca\",\"@type\":\"Telephone\",\"number\":\"5556667777\"}}", text);

		JsonObject reloaded = JsonParser.parseObject(text);
		assertNotEquals(data, reloaded);
		
		JsonObject updated = reloaded.with("address", reloaded.getObject("address").remove("@context"));
		assertEquals(data.toString(), updated.toString());
		
	}
	
}