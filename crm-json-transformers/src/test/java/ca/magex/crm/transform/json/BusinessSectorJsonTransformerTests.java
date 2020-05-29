package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.Transformer;

public class BusinessSectorJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<BusinessSector> transformer;
	
	private BusinessSector sector;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new BusinessSectorJsonTransformer(crm);
		sector = crm.findBusinessSectorByLocalizedName(Lang.ENGLISH, "External");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(BusinessSector.class, transformer.getType());
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
		JsonObject linked = (JsonObject)transformer.format(sector, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("BusinessSector", linked.getString("@type"));
		assertEquals("1", linked.getString("@value"));
		assertEquals("External", linked.getString("@en"));
		assertEquals("External", linked.getString("@fr"));
		assertEquals(sector, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(sector, Lang.ROOT);
		assertEquals("1", root.value());
		assertEquals(sector, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(sector, Lang.ENGLISH);
		assertEquals("External", english.value());
		assertEquals(sector, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(sector, Lang.FRENCH);
		assertEquals("External", french.value());
		assertEquals(sector, transformer.parse(french, Lang.FRENCH));
	}
	
}
