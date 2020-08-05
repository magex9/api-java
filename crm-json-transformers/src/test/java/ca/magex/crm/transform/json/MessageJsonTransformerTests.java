package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.services.CrmConfigurationService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.MessageTypeIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.test.config.BasicTestConfig;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BasicTestConfig.class })
public class MessageJsonTransformerTests {
	
	@Autowired private Crm crm;
	
	@Autowired private CrmConfigurationService config;
	
	private Transformer<Message, JsonElement> transformer;
	
	private MessageTypeIdentifier error;
	
	private PhraseIdentifier reason;
	
	private Message message;
	
	private OrganizationIdentifier organizationId;
	
	@Before
	public void setup() {
		config.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		organizationId = new OrganizationIdentifier("KJj15ntU2G");
		transformer = new MessageJsonTransformer(crm);
		error = crm.findOptionByCode(Type.MESSAGE_TYPE, "ERROR").getOptionId();
		reason = crm.findMessageId("validation.field.required");
		message = new Message(organizationId, error, "prop", "", reason);
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
		assertEquals(List.of("@context", "identifier", "type", "value", "path", "reason"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/system/Message", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/KJj15ntU2G", linked.getString("identifier"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("type").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/MessageTypes", linked.getObject("type").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/message-types/error", linked.getObject("type").getString("@id"));
		assertEquals("ERROR", linked.getObject("type").getString("@value"));
		assertEquals("Error", linked.getObject("type").getString("@en"));
		assertEquals("Erreur", linked.getObject("type").getString("@fr"));
		assertEquals("", linked.getString("value"));
		assertEquals("prop", linked.getString("path"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("reason").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Phrases", linked.getObject("reason").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/phrases/validation/field/required", linked.getObject("reason").getString("@id"));
		assertEquals("VALIDATION/FIELD/REQUIRED", linked.getObject("reason").getString("@value"));
		assertEquals("Field is required", linked.getObject("reason").getString("@en"));
		assertEquals("Champ requis", linked.getObject("reason").getString("@fr"));
		assertEquals(message, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(message, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("context", "identifier", "type", "value", "path", "reason"), root.keys());
		assertEquals("organizations", root.getString("context"));
		assertEquals("KJj15ntU2G", root.getString("identifier"));
		assertEquals("ERROR", root.getString("type"));
		assertEquals("", root.getString("value"));
		assertEquals("prop", root.getString("path"));
		assertEquals("VALIDATION/FIELD/REQUIRED", root.getString("reason"));
		assertEquals(message, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(message, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("context", "identifier", "type", "value", "path", "reason"), english.keys());
		assertEquals("organizations", english.getString("context"));
		assertEquals("KJj15ntU2G", english.getString("identifier"));
		assertEquals("Error", english.getString("type"));
		assertEquals("", english.getString("value"));
		assertEquals("prop", english.getString("path"));
		assertEquals("Field is required", english.getString("reason"));
		assertEquals(message, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(message, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("context", "identifier", "type", "value", "path", "reason"), french.keys());
		assertEquals("organizations", french.getString("context"));
		assertEquals("KJj15ntU2G", french.getString("identifier"));
		assertEquals("Erreur", french.getString("type"));
		assertEquals("", french.getString("value"));
		assertEquals("prop", french.getString("path"));
		assertEquals("Champ requis", french.getString("reason"));
		assertEquals(message, transformer.parse(french, Lang.FRENCH));
	}
	
}
