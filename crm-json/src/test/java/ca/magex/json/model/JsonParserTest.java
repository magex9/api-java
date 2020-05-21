package ca.magex.json.model;

import static ca.magex.json.model.JsonParser.parseArray;
import static ca.magex.json.model.JsonParser.parseObject;
import static ca.magex.json.model.JsonParser.readFile;
import static ca.magex.json.model.JsonParser.readInputStream;
import static ca.magex.json.model.JsonParser.writeFile;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class JsonParserTest {

	@Test
	public void testParsingBook() throws Exception {
		testParsingFile("book");
	}
	
	@Test
	public void testParsingMovie() throws Exception {
		testParsingFile("movie");
	}
	
	public void testParsingFile(String name) throws Exception {
		File formattedFile = new File("src/test/resources/examples/" + name + "-formatted.json");
		JsonObject formattedObj = parseObject(formattedFile);
		assertEquals(formattedObj.toString(), readFile(formattedFile));
		assertEquals(JsonFormatter.formatted(formattedObj), readFile(formattedFile));
		
		File compactFile = new File("src/test/resources/examples/" + name + "-compact.json");
		JsonObject compactObj = parseObject(compactFile);
		assertEquals(formattedObj, compactObj);
		assertEquals(formattedObj.mid(), compactObj.mid());

		assertEquals(JsonFormatter.formatted(formattedObj), JsonFormatter.formatted(compactObj));
		assertEquals(JsonFormatter.compact(formattedObj), JsonFormatter.compact(compactObj));
		
		File outputFile = new File("target/" + name + "-formatted.json");
		writeFile(outputFile, formattedObj.toString());
		assertEquals(readFile(formattedFile), readFile(outputFile));
	}
	
	@Test
	public void testReadObjectResourceAndFileEquals() throws Exception {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("examples/book-formatted.json");
		assertNotNull(is);
		File file = new File("src/test/resources/examples/book-formatted.json");
		assertTrue(file.exists());
		assertEquals(parseObject(is), parseObject(file));
	}
	
	@Test
	public void testReadArrayResourceAndFileEquals() throws Exception {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("examples/people-formatted.json");
		assertNotNull(is);
		File file = new File("src/test/resources/examples/people-formatted.json");
		assertTrue(file.exists());
		assertEquals(parseArray(is), parseArray(file));
	}
	
	@Test
	public void testReadingNullInputStream() throws Exception {
		try {
			readInputStream(null);
			fail("Should throw exception");
		} catch (IOException e) { }
	}
	
	@Test
	public void testInvalidTrueKeywordSpellings() throws Exception {
		assertEquals(new JsonBoolean(true), JsonParser.parse("true"));
		try {
			JsonParser.parse("tap");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (tap)", e.getMessage());
		}
		try {
			JsonParser.parse("trap");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (trap)", e.getMessage());
		}
		try {
			JsonParser.parse("trump");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (trump)", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidFalseKeywordSpellings() throws Exception {
		assertEquals(new JsonBoolean(false), JsonParser.parse("false"));
		try {
			JsonParser.parse("fat");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (fat)", e.getMessage());
		}
		try {
			JsonParser.parse("flat");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (flat)", e.getMessage());
		}
		try {
			JsonParser.parse("falcon");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (falcon)", e.getMessage());
		}
		try {
			JsonParser.parse("falsa");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (falsa)", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidNullKeywordSpellings() throws Exception {
		assertEquals(JsonElement.UNDEFINED, JsonParser.parse("null"));
		try {
			JsonParser.parse("none");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (none)", e.getMessage());
		}
		try {
			JsonParser.parse("nun");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (nun)", e.getMessage());
		}
		try {
			JsonParser.parse("nuls");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unepxected base value at index: 0 (nuls)", e.getMessage());
		}
	}
	
	@Test
	public void testTrailingInformation() throws Exception {
		try {
			JsonParser.parse("");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Unable to parse empty text", e.getMessage());
		}
	}
	
	@Test
	public void testParsingEmptyObject() throws Exception {
		JsonParser.parse("{ \"person\": { \"name\": \"x\" } }");
		try {
			JsonParser.parse("{ \"person\": { x } }");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Expected a pair value at index: 14 (x } })", e.getMessage());
		}
		try {
			JsonParser.parse("{ \"person\": { } }");
			fail();
		} catch (RuntimeException e) {
			assertEquals("Expected a pair value at index: 14 (x } })", e.getMessage());
		}
	}
	
}