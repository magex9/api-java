package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class RoleJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Role, JsonElement> transformer;
	
	private Role role;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new RoleJsonTransformer(crm);
		role = new Role(new Identifier("abc"), new Identifier("grp"), Status.PENDING, ORG_ADMIN);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Role.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(role, null);
		assertEquals(List.of("@type", "roleId", "groupId", "status", "code", "name"), linked.keys());
		assertEquals("Role", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("roleId").keys());
		assertEquals("Identifier", linked.getObject("roleId").getString("@type"));
		assertEquals("abc", linked.getObject("roleId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("groupId").keys());
		assertEquals("Identifier", linked.getObject("groupId").getString("@type"));
		assertEquals("grp", linked.getObject("groupId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("pending", linked.getObject("status").getString("@value"));
		assertEquals("Pending", linked.getObject("status").getString("@en"));
		assertEquals("En attente", linked.getObject("status").getString("@fr"));
		assertEquals("ORG_ADMIN", linked.getString("code"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("name").keys());
		assertEquals("Localized", linked.getObject("name").getString("@type"));
		assertEquals("ORG_ADMIN", linked.getObject("name").getString("@value"));
		assertEquals("Organization Administrator", linked.getObject("name").getString("@en"));
		assertEquals("Adminstrator du l'organization", linked.getObject("name").getString("@fr"));
		assertEquals(role, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(role, Lang.ROOT);
		assertEquals(List.of("@type", "roleId", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Role", root.getString("@type"));
		assertEquals("abc", root.getString("roleId"));
		assertEquals("grp", root.getString("groupId"));
		assertEquals("pending", root.getString("status"));
		assertEquals("ORG_ADMIN", root.getString("code"));
		assertEquals("ORG_ADMIN", root.getString("name"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(role, Lang.ENGLISH);
		assertEquals(List.of("@type", "roleId", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Role", root.getString("@type"));
		assertEquals("abc", root.getString("roleId"));
		assertEquals("grp", root.getString("groupId"));
		assertEquals("Pending", root.getString("status"));
		assertEquals("ORG_ADMIN", root.getString("code"));
		assertEquals("Organization Administrator", root.getString("name"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(role, Lang.FRENCH);
		assertEquals(List.of("@type", "roleId", "groupId", "status", "code", "name"), root.keys());
		assertEquals("Role", root.getString("@type"));
		assertEquals("abc", root.getString("roleId"));
		assertEquals("grp", root.getString("groupId"));
		assertEquals("En attente", root.getString("status"));
		assertEquals("ORG_ADMIN", root.getString("code"));
		assertEquals("Adminstrator du l'organization", root.getString("name"));
	}
	
}
