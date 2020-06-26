package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class BusinessPositionJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<BusinessPosition, JsonElement> transformer;
	
	private BusinessPosition position;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new BusinessPositionJsonTransformer(crm);
		position = new BusinessPosition(
			crm.findOptionByLocalizedName(Crm.BUSINESS_SECTOR, Lang.ENGLISH, "IM/IT").getCode(),
			crm.findOptionByLocalizedName(Crm.BUSINESS_UNIT, "IMIT", Lang.ENGLISH, "Operations").getCode(),
			crm.findOptionByLocalizedName(Crm.BUSINESS_CLASSIFICATION, Lang.ENGLISH, "System Administrator").getCode()
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
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@type", "sector", "unit", "classification"), linked.keys());
		assertEquals("BusinessPosition", linked.getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("sector").keys());
		assertEquals("BUSINESS_SECTOR", linked.getObject("sector").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("IMIT", linked.getObject("sector").getString("@value").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("IM/IT", linked.getObject("sector").getString("@en").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("GI / TI", linked.getObject("sector").getString("@fr").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("unit").keys());
		assertEquals("BUSINESS_UNIT", linked.getObject("unit").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("OPS", linked.getObject("unit").getString("@value").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Operations", linked.getObject("unit").getString("@en").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Operations", linked.getObject("unit").getString("@fr").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("classification").keys());
		assertEquals("BUSINESS_CLASSIFICATION", linked.getObject("classification").getString("@type").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("SYS_ADMIN", linked.getObject("classification").getString("@value").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("System Administrator", linked.getObject("classification").getString("@en").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals("Administrateur du système", linked.getObject("classification").getString("@fr").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r"));
		assertEquals(position, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(position, Lang.ROOT);
		assertEquals(List.of("@type", "sector", "unit", "classification"), root.keys());
		assertEquals("BusinessPosition", root.getString("@type"));
		assertEquals("IMIT", root.getString("sector"));
		assertEquals("OPS", root.getString("unit"));
		assertEquals("SYS_ADMIN", root.getString("classification"));
		assertEquals(position, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(position, Lang.ENGLISH);
		assertEquals(List.of("@type", "sector", "unit", "classification"), english.keys());
		assertEquals("BusinessPosition", english.getString("@type"));
		assertEquals("IM/IT", english.getString("sector"));
		assertEquals("Operations", english.getString("unit"));
		assertEquals("System Administrator", english.getString("classification"));
		assertEquals(position, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(position, Lang.FRENCH);
		assertEquals(List.of("@type", "sector", "unit", "classification"), french.keys());
		assertEquals("BusinessPosition", french.getString("@type"));
		assertEquals("GI / TI", french.getString("sector"));
		assertEquals("Operations", french.getString("unit"));
		assertEquals("Administrateur du système", french.getString("classification"));
		assertEquals(position, transformer.parse(french, Lang.FRENCH));
	}
	
}
