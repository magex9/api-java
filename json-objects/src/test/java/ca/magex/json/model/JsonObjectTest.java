package ca.magex.json.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.Test;

public class JsonObjectTest {

	@Test
	public void testEmptyArray() throws Exception {
		JsonObject data = new JsonObject();
		assertEquals("{}", data.toString());
		assertEquals(0, data.size());
	}
	
	@Test
	public void testStringConstructor() throws Exception {
		JsonObject data = new JsonObject("{\"type\":1,\"test\":true,\"name\":\"test\"}");
		assertEquals("{\"type\":1,\"test\":true,\"name\":\"test\"}", JsonFormatter.compact(data));
		assertEquals("{\n" + 
			"  \"type\": 1,\n" + 
			"  \"test\": true,\n" + 
			"  \"name\": \"test\"\n" + 
			"}", data.toString());
		assertEquals(3, data.size());
	}
	
	@Test
	public void testWithClause() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("list", new JsonArray().with("a", "b", "c"));
		assertEquals(4, data.pairs().size());
		assertEquals(List.of("name", "type", "active", "list"),
			data.stream().map(p -> p.key()).collect(Collectors.toList()));
		assertEquals(List.of("name", "type", "active", "list"), data.keys());
		assertEquals(new JsonText("a"), data.values().get(0));
		assertEquals(new JsonNumber(3), data.values().get(1));
		assertEquals(new JsonBoolean(true), data.values().get(2));
		assertEquals(new JsonArray().with("a", "b", "c"), data.values().get(3));
	}
	
	@Test
	public void testContainsClause() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"));
		assertTrue(data.contains("name"));
		assertFalse(data.contains("age"));
		assertTrue(data.contains("name", JsonText.class));
		assertFalse(data.contains("name", JsonNumber.class));
		assertFalse(data.contains("name", JsonObject.class));
		assertFalse(data.contains("name", JsonElement.class));
		assertFalse(data.contains("undef", JsonElement.class));
		assertFalse(data.contains("undef", JsonObject.class));
	}
	
	@Test
	public void testSize() throws Exception {
		assertEquals(0, new JsonObject().size());
		assertEquals(1, new JsonObject().with("a", 2).size());
		assertEquals(1, new JsonObject().with("a", 2).with("b", JsonElement.UNDEFINED).size());
		assertEquals(2, new JsonObject().with("a", 2).with("b", JsonElement.UNDEFINED).with("b", true).size());
		assertEquals(2, new JsonObject().with("a", 2).with("b", JsonElement.UNDEFINED).with("c", true).size());
		assertEquals(0, new JsonObject().with("a", JsonElement.UNDEFINED).size());
	}
	
	@Test
	public void testRemovingEntries() throws Exception {
		assertEquals("{}", JsonFormatter.compact(new JsonObject().remove("x")));
		assertEquals("{\"a\":1}", JsonFormatter.compact(new JsonObject().with("a", 1).with("b", 2).remove("b")));
		assertEquals("{\"a\":1,\"b\":2}", JsonFormatter.compact(new JsonObject().with("a", 1).with("b", 2).remove("x")));
	}
	
	@Test
	public void testEmptyClause() throws Exception {
		assertTrue(new JsonObject().isEmpty());
		assertFalse(new JsonObject().with("a", 2).isEmpty());
		assertTrue(new JsonObject().with("a", JsonElement.UNDEFINED).isEmpty());
	}
	
	@Test
	public void testGet() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(new JsonObject().with("id", 42), data.get("obj"));
		try {
			data.get("missing");
			fail("Not an object");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetObject() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(new JsonObject().with("id", 42), data.getObject("obj"));
		try {
			data.getObject("name");
			fail("Not an object");
		} catch (ClassCastException expected) { }
		try {
			data.getObject("missing");
			fail("Not an object");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetArray() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(new JsonArray().with("a", "b", "c"), data.getArray("list"));
		try {
			data.getArray("name");
			fail("Not an array");
		} catch (ClassCastException expected) { }
		try {
			data.getArray("missing");
			fail("Not an array");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetString() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals("a", data.getString("name"));
		try {
			data.getString("type");
			fail("Not an string");
		} catch (ClassCastException expected) { }
		try {
			data.getString("missing");
			fail("Not an string");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetInt() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(Integer.valueOf(3), data.getInt("type"));
		try {
			data.getInt("name");
			fail("Not an int");
		} catch (ClassCastException expected) { }
		try {
			data.getInt("missing");
			fail("Not an int");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetLong() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(Long.valueOf(3), data.getLong("type"));
		try {
			data.getLong("name");
			fail("Not a long");
		} catch (ClassCastException expected) { }
		try {
			data.getLong("missing");
			fail("Not a long");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetFloat() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(Float.valueOf(3), data.getFloat("type"));
		try {
			data.getFloat("name");
			fail("Not a float");
		} catch (ClassCastException expected) { }
		try {
			data.getFloat("missing");
			fail("Not a float");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetBoolean() throws Exception {
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(true, data.getBoolean("active"));
		try {
			data.getBoolean("name");
			fail("Not a boolean");
		} catch (ClassCastException expected) { }
		try {
			data.getBoolean("missing");
			fail("Not a boolean");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetDate() throws Exception {
		LocalDate date = LocalDate.of(2020, 03, 02);
		LocalDateTime datetime = LocalDateTime.of(2020, 03, 02, 12, 22, 11);
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("date", date)
			.with("datetime", datetime)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(date, data.getDate("date"));
		try {
			data.getDate("active");
			fail("Not a date");
		} catch (ClassCastException expected) { }
		try {
			data.getDate("name");
			fail("Not a date");
		} catch (DateTimeParseException expected) { }
		try {
			data.getDate("missing");
			fail("Not a date");
		} catch (NoSuchElementException expected) { }
	}
	
	@Test
	public void testGetDateTime() throws Exception {
		LocalDate date = LocalDate.of(2020, 03, 02);
		LocalDateTime datetime = LocalDateTime.of(2020, 03, 02, 12, 22, 11);
		JsonObject data = new JsonObject()
			.with("name", "a")
			.with("type", 3)
			.with("active", true)
			.with("date", date)
			.with("datetime", datetime)
			.with("undef", JsonElement.UNDEFINED)
			.with("list", new JsonArray().with("a", "b", "c"))
			.with("obj", new JsonObject().with("id", 42));
		assertEquals(datetime, data.getDateTime("datetime"));
		try {
			data.getDateTime("active");
			fail("Not a date");
		} catch (ClassCastException expected) { }
		try {
			data.getDateTime("name");
			fail("Not a date");
		} catch (DateTimeParseException expected) { }
		try {
			data.getDateTime("missing");
			fail("Not a date");
		} catch (NoSuchElementException expected) { }
	}
	
}
