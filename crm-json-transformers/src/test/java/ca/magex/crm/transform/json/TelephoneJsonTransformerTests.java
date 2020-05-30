package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.PERSON_TELEPHONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class TelephoneJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Telephone, JsonElement> transformer;
	
	private Telephone telephone;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new TelephoneJsonTransformer(crm);
		telephone = PERSON_TELEPHONE;
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Telephone.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(telephone, null);
		assertEquals(List.of("@type", "number", "extension"), linked.keys());
		assertEquals("6135551234", linked.getString("number"));
		assertEquals("42", linked.getString("extension"));
		assertEquals(telephone, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(telephone, Lang.ROOT);
		assertEquals(List.of("@type", "number", "extension"), root.keys());
		assertEquals("6135551234", root.getString("number"));
		assertEquals("42", root.getString("extension"));
		assertEquals(telephone, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(telephone, Lang.ENGLISH);
		assertEquals(List.of("@type", "number", "extension"), english.keys());
		assertEquals("6135551234", english.getString("number"));
		assertEquals("42", english.getString("extension"));
		assertEquals(telephone, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(telephone, Lang.FRENCH);
		assertEquals(List.of("@type", "number", "extension"), french.keys());
		assertEquals("6135551234", french.getString("number"));
		assertEquals("42", french.getString("extension"));
		assertEquals(telephone, transformer.parse(french, Lang.FRENCH));
	}
	
}
