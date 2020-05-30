package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class BusinessClassificationJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<BusinessClassification, JsonElement> transformer;
	
	private BusinessClassification classification;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new BusinessClassificationJsonTransformer(crm);
		classification = crm.findBusinessClassificationByLocalizedName(Lang.ENGLISH, "Developer");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(BusinessClassification.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(classification, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("BusinessClassification", linked.getString("@type"));
		assertEquals("1", linked.getString("@value"));
		assertEquals("Developer", linked.getString("@en"));
		assertEquals("Développeur", linked.getString("@fr"));
		assertEquals(classification, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(classification, Lang.ROOT);
		assertEquals("1", root.value());
		assertEquals(classification, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(classification, Lang.ENGLISH);
		assertEquals("Developer", english.value());
		assertEquals(classification, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(classification, Lang.FRENCH);
		assertEquals("Développeur", french.value());
		assertEquals(classification, transformer.parse(french, Lang.FRENCH));
	}
	
}
