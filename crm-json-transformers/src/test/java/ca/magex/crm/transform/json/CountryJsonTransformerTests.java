package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Lang;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.Transformer;

public class CountryJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<Country> transformer;
	
	private Country country;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new CountryJsonTransformer(crm);
		country = crm.findCountryByLocalizedName(Lang.ENGLISH, "United States");
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Country.class, transformer.getType());
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
		JsonObject linked = (JsonObject)transformer.format(country, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.keys());
		assertEquals("Country", linked.getString("@type"));
		assertEquals("US", linked.getString("@value"));
		assertEquals("United States", linked.getString("@en"));
		assertEquals("États-Unis d'Amérique", linked.getString("@fr"));
		assertEquals(country, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonText root = (JsonText)transformer.format(country, Lang.ROOT);
		assertEquals("US", root.value());
		assertEquals(country, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonText english = (JsonText)transformer.format(country, Lang.ENGLISH);
		assertEquals("United States", english.value());
		assertEquals(country, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonText french = (JsonText)transformer.format(country, Lang.FRENCH);
		assertEquals("États-Unis d'Amérique", french.value());
		assertEquals(country, transformer.parse(french, Lang.FRENCH));
	}
	
}
