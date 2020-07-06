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
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class UserJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<User, JsonElement> transformer;
	
	private UserIdentifier userId;
	
	private PersonIdentifier personId;
	
	private OrganizationIdentifier organizationId;
	
	private User user;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new UserJsonTransformer(crm);
		userId = new UserIdentifier("R61MD142WM");
		personId = new PersonIdentifier("Q69WVGMAce");
		organizationId = new OrganizationIdentifier("gGe79u4rWg");
		user = new User(userId, organizationId, personId, "admin", Status.ACTIVE, List.of(
			crm.findOptionByCode(Type.AUTHENTICATION_ROLE, "CRM/ADMIN").getOptionId(),
			crm.findOptionByCode(Type.AUTHENTICATION_ROLE, "ORG/ADMIN").getOptionId()
		));
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
		assertEquals(List.of("@context", "userId", "organizationId", "personId", "username", "status", "roleIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/User", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/users/" + userId.getId(), linked.getString("userId"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getId(), linked.getString("organizationId"));
		assertEquals("http://api.magex.ca/crm/rest/persons/" + personId.getId(), linked.getString("personId"));
		assertEquals("admin", linked.getString("username"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals(2, linked.getArray("roleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("roleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", linked.getArray("roleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/crm/admin", linked.getArray("roleIds").getObject(0).getString("@id"));
		assertEquals("CRM/ADMIN", linked.getArray("roleIds").getObject(0).getString("@value"));
		assertEquals("CRM Admin", linked.getArray("roleIds").getObject(0).getString("@en"));
		assertEquals("Administrateur GRC", linked.getArray("roleIds").getObject(0).getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("roleIds").getObject(1).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/AuthenticationRoles", linked.getArray("roleIds").getObject(1).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/authentication-roles/org/admin", linked.getArray("roleIds").getObject(1).getString("@id"));
		assertEquals("ORG/ADMIN", linked.getArray("roleIds").getObject(1).getString("@value"));
		assertEquals("Organization Admin", linked.getArray("roleIds").getObject(1).getString("@en"));
		assertEquals("Administrateur de l'organisation", linked.getArray("roleIds").getObject(1).getString("@fr"));
		assertEquals(user, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(user, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "roleIds"), root.keys());
		assertEquals(userId.getId(), root.getString("userId"));
		assertEquals(organizationId.getId(), root.getString("organizationId"));
		assertEquals(personId.getId(), root.getString("personId"));
		assertEquals("admin", root.getString("username"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals(2, root.getArray("roleIds").size());
		assertEquals("CRM/ADMIN", root.getArray("roleIds").getString(0));
		assertEquals("ORG/ADMIN", root.getArray("roleIds").getString(1));
		assertEquals(user, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(user, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "roleIds"), english.keys());
		assertEquals(userId.getId(), english.getString("userId"));
		assertEquals(organizationId.getId(), english.getString("organizationId"));
		assertEquals(personId.getId(), english.getString("personId"));
		assertEquals("admin", english.getString("username"));
		assertEquals("Active", english.getString("status"));
		assertEquals(2, english.getArray("roleIds").size());
		assertEquals("CRM Admin", english.getArray("roleIds").getString(0));
		assertEquals("Organization Admin", english.getArray("roleIds").getString(1));
		assertEquals(user, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(user, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("userId", "organizationId", "personId", "username", "status", "roleIds"), french.keys());
		assertEquals(userId.getId(), french.getString("userId"));
		assertEquals(organizationId.getId(), french.getString("organizationId"));
		assertEquals(personId.getId(), french.getString("personId"));
		assertEquals("admin", french.getString("username"));
		assertEquals("Actif", french.getString("status"));
		assertEquals(2, french.getArray("roleIds").size());
		assertEquals("Administrateur GRC", french.getArray("roleIds").getString(0));
		assertEquals("Administrateur de l'organisation", french.getArray("roleIds").getString(1));
		assertEquals(user, transformer.parse(french, Lang.FRENCH));
	}
	
}
