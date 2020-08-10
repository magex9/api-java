package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.test.config.BasicTestConfig;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class LocalizedJsonTransformerTests {
	
	@Autowired private Crm crm;
	
	@Autowired private CrmConfigurationService config;
	
	private Transformer<Localized, JsonElement> transformer;
	
	@Before
	public void setup() {
		config.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new LocalizedJsonTransformer(crm);
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
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), root.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/system/Localized", root.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/dictionary/GRP", root.getString("@id"));
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
