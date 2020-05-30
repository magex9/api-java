package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class LanguageJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Language, JsonElement> transformer;
	
	private Language language;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new LanguageJsonTransformer(crm);
		language = crm.findLanguageByLocalizedName(Lang.ENGLISH, "English");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Language.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(language, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("Language", linked.getString("@type"));
		assertEquals("EN", linked.getString("@value"));
		assertEquals("English", linked.getString("@en"));
		assertEquals("Anglais", linked.getString("@fr"));
		assertEquals(language, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(language, Lang.ROOT);
		assertEquals("EN", root.value());
		assertEquals(language, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(language, Lang.ENGLISH);
		assertEquals("English", english.value());
		assertEquals(language, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(language, Lang.FRENCH);
		assertEquals("Anglais", french.value());
		assertEquals(language, transformer.parse(french, Lang.FRENCH));
	}
	
}
