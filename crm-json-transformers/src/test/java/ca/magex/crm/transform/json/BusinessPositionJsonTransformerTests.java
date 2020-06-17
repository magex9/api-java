package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class BusinessPositionJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<BusinessPosition, JsonElement> transformer;
	
	private BusinessPosition position;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new BusinessPositionJsonTransformer(crm);
		position = new BusinessPosition(
			crm.findBusinessSectorByLocalizedName(Lang.ENGLISH, "Information Technology").getCode(),
			crm.findBusinessUnitByLocalizedName(Lang.ENGLISH, "Solutions").getCode(),
			crm.findBusinessClassificationByLocalizedName(Lang.ENGLISH, "Developer").getCode()
		);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(BusinessPosition.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(position, null);
		assertEquals(List.of("@type", "sector", "unit", "classification"), linked.keys());
		assertEquals("BusinessPosition", linked.getString("@type"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("sector").keys());
		assertEquals("BusinessSector", linked.getObject("sector").getString("@type"));
		assertEquals("4", linked.getObject("sector").getString("@value"));
		assertEquals("Information Technology", linked.getObject("sector").getString("@en"));
		assertEquals("Technologie Informatique", linked.getObject("sector").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("unit").keys());
		assertEquals("BusinessUnit", linked.getObject("unit").getString("@type"));
		assertEquals("1", linked.getObject("unit").getString("@value"));
		assertEquals("Solutions", linked.getObject("unit").getString("@en"));
		assertEquals("Solutions", linked.getObject("unit").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("classification").keys());
		assertEquals("BusinessClassification", linked.getObject("classification").getString("@type"));
		assertEquals("1", linked.getObject("classification").getString("@value"));
		assertEquals("Developer", linked.getObject("classification").getString("@en"));
		assertEquals("Développeur", linked.getObject("classification").getString("@fr"));
		assertEquals(position, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(position, Lang.ROOT);
		assertEquals(List.of("@type", "sector", "unit", "classification"), root.keys());
		assertEquals("BusinessPosition", root.getString("@type"));
		assertEquals("4", root.getString("sector"));
		assertEquals("1", root.getString("unit"));
		assertEquals("1", root.getString("classification"));
		assertEquals(position, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(position, Lang.ENGLISH);
		assertEquals(List.of("@type", "sector", "unit", "classification"), english.keys());
		assertEquals("BusinessPosition", english.getString("@type"));
		assertEquals("Information Technology", english.getString("sector"));
		assertEquals("Solutions", english.getString("unit"));
		assertEquals("Developer", english.getString("classification"));
		assertEquals(position, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(position, Lang.FRENCH);
		assertEquals(List.of("@type", "sector", "unit", "classification"), french.keys());
		assertEquals("BusinessPosition", french.getString("@type"));
		assertEquals("Technologie Informatique", french.getString("sector"));
		assertEquals("Solutions", french.getString("unit"));
		assertEquals("Développeur", french.getString("classification"));
		assertEquals(position, transformer.parse(french, Lang.FRENCH));
	}
	
}
