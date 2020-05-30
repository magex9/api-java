package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class LocalizedJsonTransformerTests {
	
	private Transformer<Localized, JsonElement> transformer;
	
	@Before
	public void setup() {
		transformer = new LocalizedJsonTransformer(new AmnesiaServices());
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Localized.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testFormatJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(GROUP, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), root.keys());
		assertEquals("Localized", root.getString("@type"));
		assertEquals("GRP", root.getString("@value"));
		assertEquals("Group", root.getString("@en"));
		assertEquals("Groupe", root.getString("@fr"));

		assertEquals(new JsonText("GRP"), transformer.format(GROUP, Lang.ROOT));
		assertEquals(new JsonText("Group"), transformer.format(GROUP, Lang.ENGLISH));
		assertEquals(new JsonText("Groupe"), transformer.format(GROUP, Lang.FRENCH));
	}
	
	@Test
	public void testParsingJson() throws Exception {
		assertEquals(GROUP, transformer.parse(transformer.format(GROUP, null), null));
		try {
			assertEquals(GROUP, transformer.parse(transformer.format(GROUP, Lang.ROOT), Lang.ROOT));
			fail("Unsupported by locale");
		} catch (UnsupportedOperationException e) {
			assertEquals("Unsupported json element: JsonText", e.getMessage());
		}
		try {
			assertEquals(GROUP, transformer.parse(transformer.format(GROUP, Lang.ENGLISH), Lang.ENGLISH));
			fail("Unsupported by locale");
		} catch (UnsupportedOperationException e) {
			assertEquals("Unsupported json element: JsonText", e.getMessage());
		}
		try {
			assertEquals(GROUP, transformer.parse(transformer.format(GROUP, Lang.FRENCH), Lang.FRENCH));
			fail("Unsupported by locale");
		} catch (UnsupportedOperationException e) {
			assertEquals("Unsupported json element: JsonText", e.getMessage());
		}
	}
		
}
