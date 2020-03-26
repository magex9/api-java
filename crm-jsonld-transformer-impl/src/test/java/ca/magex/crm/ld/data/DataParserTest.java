package ca.magex.crm.ld.data;

import static ca.magex.crm.ld.data.DataParser.parse;
import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

public class DataParserTest {

	@Test
	public void testBlankObject() throws Exception {
		String original = "{}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{}", el.compact());
		assertEquals("{}", el.formatted());
	}
	
	@Test
	public void testSimpleObject() throws Exception {
		String original = "{\"name\":\"Scott\"}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{\"name\":\"Scott\"}", el.compact());
		assertEquals("{\"name\": \"Scott\"}", el.formatted());
	}
	
	@Test
	public void testNestedObject() throws Exception {
		String original = "{\"name\":{\"first\":\"Scott\",\"active\":true}}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{\"name\":{\"first\":\"Scott\",\"active\":true}}", el.compact());
		assertEquals("{\n" +
			"  \"name\": {\n" + 
			"    \"first\": \"Scott\",\n" +
			"    \"active\": true\n" +
			"  }\n" +
			"}", el.formatted());
	}
	
	@Test
	public void testMultipleParameters() throws Exception {
		String original = "{\"name\":\"Scott\",\"active\":true,\"number\":42}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{\"name\":\"Scott\",\"active\":true,\"number\":42}", el.compact());
		assertEquals("{\n" + 
			"  \"name\": \"Scott\",\n" + 
			"  \"active\": true,\n" + 
			"  \"number\": 42\n" + 
			"}", el.formatted());
	}

	@Test
	public void testNull() throws Exception {
		String original = "null";
		DataElement el = parse(original);
		assertEquals(DataElement.class, el.getClass());
		assertEquals(original, el.compact());
		assertEquals(original, el.formatted());
	}
	
	@Test
	public void testNumberCasting() throws Exception {
		String text = "42";
		Number number = (Number)Integer.parseInt(text);
		assertEquals(text, number.toString());
	}
	
	@Test
	public void testInteger() throws Exception {
		String original = "42";
		DataElement el = parse(original);
		assertEquals(DataNumber.class, el.getClass());
		assertEquals(Integer.class, ((DataNumber)el).value().getClass());
		assertEquals(original, el.compact());
		assertEquals(original, el.formatted());
	}
	
	@Test
	public void testFloat() throws Exception {
		String original = "42.3";
		DataElement el = parse(original);
		assertEquals(DataNumber.class, el.getClass());
		assertEquals(Float.class, ((DataNumber)el).value().getClass());
		assertEquals(original, el.compact());
		assertEquals(original, el.formatted());
	}
	
	@Test
	public void testParseNumbers() throws Exception {
		String original = "{\"int\":5,\"float\":5.5}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{\"int\":5,\"float\":5.5}", el.compact());
		assertEquals("{\n" + 
				"  \"int\": 5,\n" + 
				"  \"float\": 5.5\n" + 
				"}", el.formatted());
	}
	
	@Test
	public void testBoolean() throws Exception {
		String original = "true";
		DataElement el = parse(original);
		assertEquals(DataBoolean.class, el.getClass());
		assertEquals(original, el.compact());
		assertEquals(original, el.formatted());
	}
	
	@Test
	public void testText() throws Exception {
		String original = "\"Scott\"";
		DataElement el = parse(original);
		assertEquals(DataText.class, el.getClass());
		assertEquals(original, el.compact());
		assertEquals(original, el.formatted());
	}
	
	@Test
	public void testWidget() throws Exception {
		String original = "{\"widget\": {\n" + 
				"    \"null\": null,\n" + 
				"    \"window\": {\n" + 
				"         \"153\": \"This is string\",\n" + 
				"        \"boolean\": true,\n" + 
				"        \"int\": 500,\n" + 
				"        \"float\": 5.555\n" + 
				"    }\n" + 
				"}}";
		DataElement el = parse(original);
		assertEquals(DataObject.class, el.getClass());
		assertEquals("{\"widget\":{\"null\":null,\"window\":{\"153\":\"This is string\",\"boolean\":true,\"int\":500,\"float\":5.555}}}", el.compact());
		assertEquals("{\n" + 
				"  \"widget\": {\n" + 
				"    \"null\": null,\n" + 
				"    \"window\": {\n" + 
				"      \"153\": \"This is string\",\n" + 
				"      \"boolean\": true,\n" + 
				"      \"int\": 500,\n" + 
				"      \"float\": 5.555\n" + 
				"    }\n" + 
				"  }\n" + 
				"}", el.formatted());
	}
	
	public void debug(String text) {
		System.out.println("== ORIGINAL ===============================");
		System.out.println(text);
		System.out.println("== EXPECTED ===============================");
		System.out.println(new JSONObject(text).toString(1));
		System.out.println("== COMPACT ================================");
		try {
			DataElement el = parse(text);
			System.out.println(el.compact());
			System.out.println("== FORMATTED ==============================");
			System.out.println(el.formatted());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
