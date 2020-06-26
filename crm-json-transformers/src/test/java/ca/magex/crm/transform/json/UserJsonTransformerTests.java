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
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class UserJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<User, JsonElement> transformer;
	
	private PersonSummary person;
	
	private User user;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new UserJsonTransformer(crm);
		person = new PersonSummary(new Identifier("prsn"), new Identifier("org"), Status.ACTIVE, "ADMIN");
		user = new User(new Identifier("usr"), "admin", person, Status.ACTIVE, List.of("SYS_ADMIN", "ORG_USER"));
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(User.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(user, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), linked.keys());
		assertEquals("User", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("userId").keys());
		assertEquals("Identifier", linked.getObject("userId").getString("@type"));
		assertEquals("usr", linked.getObject("userId").getString("@id"));
		assertEquals("admin", linked.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), linked.getObject("person").keys());
		assertEquals("PersonSummary", linked.getObject("person").getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("person").getObject("personId").keys());
		assertEquals("Identifier", linked.getObject("person").getObject("personId").getString("@type"));
		assertEquals("prsn", linked.getObject("person").getObject("personId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("person").getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("person").getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("person").getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("person").getObject("status").keys());
		assertEquals("Status", linked.getObject("person").getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("person").getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("person").getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("person").getObject("status").getString("@fr"));
		assertEquals("ADMIN", linked.getObject("person").getString("displayName"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals(2, linked.getArray("roles").size());
		assertEquals("SYS_ADMIN", linked.getArray("roles").getString(0));
		assertEquals("ORG_USER", linked.getArray("roles").getString(1));
		assertEquals(user, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(user, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), root.keys());
		assertEquals("User", root.getString("@type"));
		assertEquals(user.getUserId().toString(), root.getString("userId"));
		assertEquals("admin", root.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), root.getObject("person").keys());
		assertEquals("PersonSummary", root.getObject("person").getString("@type"));
		assertEquals(person.getPersonId().toString(), root.getObject("person").getString("personId"));
		assertEquals(person.getOrganizationId().toString(), root.getObject("person").getString("organizationId"));
		assertEquals("active", root.getObject("person").getString("status"));
		assertEquals("ADMIN", root.getObject("person").getString("displayName"));
		assertEquals("active", root.getString("status"));
		assertEquals(2, root.getArray("roles").size());
		assertEquals("SYS_ADMIN", root.getArray("roles").getString(0));
		assertEquals("ORG_USER", root.getArray("roles").getString(1));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(user, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), english.keys());
		assertEquals("User", english.getString("@type"));
		assertEquals(user.getUserId().toString(), english.getString("userId"));
		assertEquals("admin", english.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), english.getObject("person").keys());
		assertEquals("PersonSummary", english.getObject("person").getString("@type"));
		assertEquals(person.getPersonId().toString(), english.getObject("person").getString("personId"));
		assertEquals(person.getOrganizationId().toString(), english.getObject("person").getString("organizationId"));
		assertEquals("Active", english.getObject("person").getString("status"));
		assertEquals("ADMIN", english.getObject("person").getString("displayName"));
		assertEquals("Active", english.getString("status"));
		assertEquals(2, english.getArray("roles").size());
		assertEquals("SYS_ADMIN", english.getArray("roles").getString(0));
		assertEquals("ORG_USER", english.getArray("roles").getString(1));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(user, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "userId", "username", "person", "status", "roles"), french.keys());
		assertEquals("User", french.getString("@type"));
		assertEquals(user.getUserId().toString(), french.getString("userId"));
		assertEquals("admin", french.getString("username"));
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName"), french.getObject("person").keys());
		assertEquals("PersonSummary", french.getObject("person").getString("@type"));
		assertEquals(person.getPersonId().toString(), french.getObject("person").getString("personId"));
		assertEquals(person.getOrganizationId().toString(), french.getObject("person").getString("organizationId"));
		assertEquals("Actif", french.getObject("person").getString("status"));
		assertEquals("ADMIN", french.getObject("person").getString("displayName"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(2, french.getArray("roles").size());
		assertEquals("SYS_ADMIN", french.getArray("roles").getString(0));
		assertEquals("ORG_USER", french.getArray("roles").getString(1));
	}
	
}
