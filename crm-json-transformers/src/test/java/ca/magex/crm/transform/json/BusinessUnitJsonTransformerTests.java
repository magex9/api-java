package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class BusinessUnitJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<BusinessUnit, JsonElement> transformer;
	
	private BusinessUnit unit;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new BusinessUnitJsonTransformer(crm);
		unit = crm.findBusinessUnitByLocalizedName(Lang.ENGLISH, "Solutions");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(BusinessUnit.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(unit, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("BusinessUnit", linked.getString("@type"));
		assertEquals("1", linked.getString("@value"));
		assertEquals("Solutions", linked.getString("@en"));
		assertEquals("Solutions", linked.getString("@fr"));
		assertEquals(unit, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(unit, Lang.ROOT);
		assertEquals("1", root.value());
		assertEquals(unit, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(unit, Lang.ENGLISH);
		assertEquals("Solutions", english.value());
		assertEquals(unit, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(unit, Lang.FRENCH);
		assertEquals("Solutions", french.value());
		assertEquals(unit, transformer.parse(french, Lang.FRENCH));
	}
	
}
