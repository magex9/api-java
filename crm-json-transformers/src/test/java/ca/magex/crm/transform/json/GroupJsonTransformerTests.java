package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonObject;
import ca.magex.json.util.Transformer;

public class GroupJsonTransformerTests {
	
	private CrmServices crm;
	
	private Transformer<Group> transformer;
	
	private Group group;
	
	@Before
	public void setup() {
		crm = new AmnesiaServices();
		transformer = new GroupJsonTransformer(crm);
		group = new Group(new Identifier("abc"), Status.PENDING, GROUP);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Group.class, transformer.getType());
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
		JsonObject linked = (JsonObject)transformer.format(group, null);
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), linked.keys());
		assertEquals("Group", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("groupId").keys());
		assertEquals("Identifier", linked.getObject("groupId").getString("@type"));
		assertEquals("abc", linked.getObject("groupId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("pending", linked.getObject("status").getString("@value"));
		assertEquals("Pending", linked.getObject("status").getString("@en"));
		assertEquals("En attente", linked.getObject("status").getString("@fr"));
		assertEquals("GRP", linked.getString("code"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("name").keys());
		assertEquals("Localized", linked.getObject("name").getString("@type"));
		assertEquals("GRP", linked.getObject("name").getString("@value"));
		assertEquals("Group", linked.getObject("name").getString("@en"));
		assertEquals("Groupe", linked.getObject("name").getString("@fr"));
		assertEquals(group, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(group, Lang.ROOT);
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Group", root.getString("@type"));
		assertEquals("abc", root.getString("groupId"));
		assertEquals("pending", root.getString("status"));
		assertEquals("GRP", root.getString("code"));
		assertEquals("GRP", root.getString("name"));
		try {
			assertEquals(group, transformer.parse(root, Lang.ROOT));
			fail("Unable to build Localized objects");
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().startsWith("Unable to parse: "));
		}
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(group, Lang.ENGLISH);
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Group", root.getString("@type"));
		assertEquals("abc", root.getString("groupId"));
		assertEquals("Pending", root.getString("status"));
		assertEquals("GRP", root.getString("code"));
		assertEquals("Group", root.getString("name"));
		try {
			assertEquals(group, transformer.parse(root, Lang.ENGLISH));
			fail("Unable to build Localized objects");
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().startsWith("Unable to parse: "));
		}
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(group, Lang.FRENCH);
		assertEquals(List.of("@type", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Group", root.getString("@type"));
		assertEquals("abc", root.getString("groupId"));
		assertEquals("En attente", root.getString("status"));
		assertEquals("GRP", root.getString("code"));
		assertEquals("Groupe", root.getString("name"));
		try {
			assertEquals(group, transformer.parse(root, Lang.ENGLISH));
			fail("Unable to build Localized objects");
		} catch (UnsupportedOperationException e) {
			assertTrue(e.getMessage().startsWith("Unable to parse: "));
		}
	}
	
}
