package ca.magex.json.model;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ca.magex.json.model.JsonPair;

public class JsonPairTest {

	@Test
	public void testDataElementKeyAndValueNull() throws Exception {
		try {
			JsonPair.class.getConstructor(new Class[] { String.class, JsonElement.class })
				.newInstance(new Object[] { null, null });
			fail("Key cannot be null");
		} catch (InvocationTargetException e) {
			assertEquals(IllegalArgumentException.class, e.getTargetException().getClass());
			assertEquals("Key cannot be null", e.getTargetException().getMessage());
		}
	}
	
	@Test
	public void testDataElementKeyNull() throws Exception {
		try {
			JsonPair.class.getConstructor(new Class[] { String.class, JsonElement.class })
				.newInstance(new Object[] { null, new JsonText("value") });
			fail("Key cannot be null");
		} catch (InvocationTargetException e) {
			assertEquals(IllegalArgumentException.class, e.getTargetException().getClass());
			assertEquals("Key cannot be null", e.getTargetException().getMessage());
		}
	}
	
	@Test
	public void testDataElementValueNull() throws Exception {
		JsonPair pair = JsonPair.class.getConstructor(new Class[] { String.class, JsonElement.class })
			.newInstance(new Object[] { "key", null });
		assertEquals("b0eafee3eafce16dad331ec1785a95d9", pair.mid());
		assertEquals("key", pair.key());
		assertEquals(JsonElement.UNDEFINED, pair.value());
	}
	
	@Test
	public void testDataPairText() throws Exception {
		JsonPair pair = new JsonPair("key", "value");
		assertEquals("a02d0ba9e804a9125267d76ff9234bdc", pair.mid());
		assertEquals("key", pair.key());
		assertEquals(JsonText.class, pair.value().getClass());
		assertEquals("value", ((JsonText)pair.value()).value());
	}
	
	@Test
	public void testDataPairNumber() throws Exception {
		JsonPair pair = new JsonPair("key", 10);
		assertEquals("f749de0df8ac53e2a9365097c52ebe73", pair.mid());
		assertEquals("key", pair.key());
		assertEquals(JsonNumber.class, pair.value().getClass());
		assertEquals(10, ((JsonNumber)pair.value()).value());
	}
	
	@Test
	public void testDataPairBooleanTrue() throws Exception {
		JsonPair pair = new JsonPair("key", true);
		assertEquals("4040c80b204df9c80406797bd1ffa841", pair.mid());
		assertEquals("key", pair.key());
		assertEquals(JsonBoolean.class, pair.value().getClass());
		assertEquals(true, ((JsonBoolean)pair.value()).value());
	}
	
	@Test
	public void testDataPairBooleanFalse() throws Exception {
		JsonPair pair = new JsonPair("key", false);
		assertEquals("96fd663b14e0e4a6e49374c6b39e0b34", pair.mid());
		assertEquals("key", pair.key());
		assertEquals(JsonBoolean.class, pair.value().getClass());
		assertEquals(false, ((JsonBoolean)pair.value()).value());
	}
	
}
