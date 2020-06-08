package ca.magex.crm.transform.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;

public class StatusJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Status, JsonElement> transformer;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new StatusJsonTransformer(crm);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Status.class, transformer.getSourceType());
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
		JsonObject root = (JsonObject)transformer.format(Status.ACTIVE, null);
		assertEquals(List.of("@type", "@value", "@en", "@fr"), root.keys());
		assertEquals("Status", root.getString("@type"));
		assertEquals("active", root.getString("@value"));
		assertEquals("Active", root.getString("@en"));
		assertEquals("Actif", root.getString("@fr"));

		assertEquals(new JsonText("active"), transformer.format(Status.ACTIVE, Lang.ROOT));
		assertEquals(new JsonText("Active"), transformer.format(Status.ACTIVE, Lang.ENGLISH));
		assertEquals(new JsonText("Actif"), transformer.format(Status.ACTIVE, Lang.FRENCH));

		assertEquals(new JsonText("inactive"), transformer.format(Status.INACTIVE, Lang.ROOT));
		assertEquals(new JsonText("Inactive"), transformer.format(Status.INACTIVE, Lang.ENGLISH));
		assertEquals(new JsonText("Inactif"), transformer.format(Status.INACTIVE, Lang.FRENCH));

		assertEquals(new JsonText("pending"), transformer.format(Status.PENDING, Lang.ROOT));
		assertEquals(new JsonText("Pending"), transformer.format(Status.PENDING, Lang.ENGLISH));
		assertEquals(new JsonText("En attente"), transformer.format(Status.PENDING, Lang.FRENCH));
	}
	
	@Test
	public void testParsingJson() throws Exception {
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, null), null));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.ROOT), Lang.ROOT));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.ENGLISH), Lang.ENGLISH));
		assertEquals(Status.ACTIVE, transformer.parse(transformer.format(Status.ACTIVE, Lang.FRENCH), Lang.FRENCH));
		
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, null), null));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.ROOT), Lang.ROOT));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.ENGLISH), Lang.ENGLISH));
		assertEquals(Status.INACTIVE, transformer.parse(transformer.format(Status.INACTIVE, Lang.FRENCH), Lang.FRENCH));
		
		assertNull(transformer.parse(transformer.format(null, null), null));
		assertNull(transformer.parse(transformer.format(null, Lang.ROOT), Lang.ROOT));
		assertNull(transformer.parse(transformer.format(null, Lang.ENGLISH), Lang.ENGLISH));
		assertNull(transformer.parse(transformer.format(null, Lang.FRENCH), Lang.FRENCH));
	}
		
}
