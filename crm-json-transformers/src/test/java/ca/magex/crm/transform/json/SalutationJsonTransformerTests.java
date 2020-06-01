package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class SalutationJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Salutation, JsonElement> transformer;
	
	private Salutation salutation;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new SalutationJsonTransformer(crm);
		salutation = crm.findSalutationByLocalizedName(Lang.ENGLISH, "Mr.");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Salutation.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testLinkedJson() throws Exception {
		JsonObject linked = (JsonObject)transformer.format(salutation, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("Salutation", linked.getString("@type"));
		assertEquals("3", linked.getString("@value"));
		assertEquals("Mr.", linked.getString("@en"));
		assertEquals("M.", linked.getString("@fr"));
		assertEquals(salutation, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(salutation, Lang.ROOT);
		assertEquals("3", root.value());
		assertEquals(salutation, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(salutation, Lang.ENGLISH);
		assertEquals("Mr.", english.value());
		assertEquals(salutation, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(salutation, Lang.FRENCH);
		assertEquals("M.", french.value());
		assertEquals(salutation, transformer.parse(french, Lang.FRENCH));
	}
	
}
