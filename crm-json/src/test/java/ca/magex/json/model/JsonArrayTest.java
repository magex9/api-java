package ca.magex.json.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonParser;
import ca.magex.json.model.JsonText;

public class JsonArrayTest {

	@Test
	public void testEmptyArray() throws Exception {
		JsonArray array = new JsonArray();
		assertEquals("[]", array.toString());
		assertEquals(0, array.size());
	}
	
	@Test
	public void testStringConstructor() throws Exception {
		JsonArray array = new JsonArray("[1,true,\"test\"]");
		assertEquals("[1,true,\"test\"]", JsonFormatter.compact(array));
		assertEquals("[\n" + 
				"  1,\n" + 
				"  true,\n" + 
				"  \"test\"\n" + 
				"]", array.toString());
		assertEquals(3, array.size());
	}
	
	@Test
	public void testSingleQuote() throws Exception {
		List<JsonElement> elements = new ArrayList<JsonElement>();
		elements.add(new JsonText("a'b"));
		JsonArray array = new JsonArray(elements);
		assertEquals("[\"a\\'b\"]", JsonFormatter.compact(array));
		String compact = JsonFormatter.compact(array);
		assertEquals(compact, JsonFormatter.compact(JsonParser.parseArray(compact)));
		assertEquals(compact, JsonFormatter.formatted(JsonParser.parseArray(compact)));
	}
	
	@Test
	public void testQuoteInVariable() throws Exception {
		List<JsonElement> elements = new ArrayList<JsonElement>();
		elements.add(new JsonText("Quote's"));
		elements.add(new JsonText("\"Double\" Quotes"));
		JsonArray array = new JsonArray(elements);
		assertEquals("[\"Quote\\'s\",\"\\\"Double\\\" Quotes\"]", JsonFormatter.compact(array));
		String compact = JsonFormatter.compact(array);
		String formatted = JsonFormatter.formatted(array);
		
		assertEquals(compact, JsonFormatter.compact(JsonParser.parseArray(compact)));
		assertEquals(formatted, JsonFormatter.formatted(JsonParser.parseArray(compact)));
	}
	
	@Test
	public void testStreamingElements() throws Exception {
		JsonArray list = new JsonArray().with("a", 3, "b", true);
		assertEquals(List.of("a", "b"), list.stream()
				.filter(e -> e instanceof JsonText)
				.map(e -> ((JsonText)e).value()).collect(Collectors.toList()));
	}
	
	@Test
	public void testArraySize() throws Exception {
		assertEquals(0, new JsonArray().size());
		assertEquals(3, new JsonArray().with(1, 2, 3).size());
	}
	
	@Test
	public void testEmptyList() throws Exception {
		assertTrue(new JsonArray().isEmpty());
		assertFalse(new JsonArray().with(1, 2, 3).isEmpty());
	}
	
	@Test
	public void testGetByIndex() throws Exception {
		LocalDate date = LocalDate.now();
		LocalDateTime datetime = LocalDateTime.now();
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();
		JsonArray list = new JsonArray().with("a", 1, 2L, 3f, true, date, datetime, object, array);
		assertEquals("a", list.getString(0));
		assertEquals(Integer.valueOf(1), list.getInt(1));
		assertEquals(Long.valueOf(2), list.getLong(2));
		assertEquals(Float.valueOf(3), list.getFloat(3));
		assertEquals(true, list.getBoolean(4));
		assertEquals(date, list.getDate(5));
		assertEquals(datetime, list.getDateTime(6));
		assertEquals(object, list.getObject(7));
		assertEquals(array, list.getArray(8));
		
		
	}
	
}
