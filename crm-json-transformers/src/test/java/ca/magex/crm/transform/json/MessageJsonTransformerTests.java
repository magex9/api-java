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
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonAsserts;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class MessageJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Message, JsonElement> transformer;
	
	private MessageTypeIdentifier error;
	
	private PhraseIdentifier reason;
	
	private Message message;
	
	private OrganizationIdentifier organizationId;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		organizationId = new OrganizationIdentifier("KJj15ntU2G");
		transformer = new MessageJsonTransformer(crm);
		error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();
		reason = crm.findMessageId("validation.field.required");
		message = new Message(organizationId, error, "prop", reason);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Message.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(message, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "identifier", "type", "path", "reason"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/system/Message", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/KJj15ntU2G", linked.getString("identifier"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("type").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookups/MessageTypes", linked.getObject("type").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/message-types/error", linked.getObject("type").getString("@id"));
		assertEquals("ERROR", linked.getObject("type").getString("@value"));
		assertEquals("Error", linked.getObject("type").getString("@en"));
		assertEquals("Erreur", linked.getObject("type").getString("@fr"));
		assertEquals("prop", linked.getString("path"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("reason").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookups/Phrases", linked.getObject("reason").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/phrases/validation/field/required", linked.getObject("reason").getString("@id"));
		assertEquals("VALIDATION/FIELD/REQUIRED", linked.getObject("reason").getString("@value"));
		assertEquals("Field is required", linked.getObject("reason").getString("@en"));
		assertEquals("Champ requis", linked.getObject("reason").getString("@fr"));
		assertEquals(message, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(message, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("context", "identifier", "type", "path", "reason"), root.keys());
		assertEquals("organizations", root.getString("context"));
		assertEquals("KJj15ntU2G", root.getString("identifier"));
		assertEquals("ERROR", root.getString("type"));
		assertEquals("prop", root.getString("path"));
		assertEquals("validation.field.required", root.getString("reason"));
		assertEquals(message, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(message, Lang.ENGLISH);
		JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), english.keys());
		assertEquals("Message", english.getString("@type"));
		assertEquals(organizationId.toString(), english.getString("identifier"));
		assertEquals("error", english.getString("type"));
		assertEquals("prop", english.getString("path"));
		assertEquals("English reason", english.getString("reason"));
		assertEquals(message, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(message, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "identifier", "type", "path", "reason"), french.keys());
		assertEquals("Message", french.getString("@type"));
		assertEquals(organizationId.toString(), french.getString("identifier"));
		assertEquals("error", french.getString("type"));
		assertEquals("prop", french.getString("path"));
		assertEquals("Raison française", french.getString("reason"));
		assertEquals(message, transformer.parse(french, Lang.FRENCH));
	}
	
}
