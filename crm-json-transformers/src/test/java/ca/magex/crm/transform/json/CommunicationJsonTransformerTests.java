package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class CommunicationJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<Communication, JsonElement> transformer;
	
	private Communication communication;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new CommunicationJsonTransformer(crm);
		communication = WORK_COMMUNICATIONS;
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(Communication.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(communication, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", linked.getString("@context"));
		assertEquals("Developer", linked.getString("jobTitle"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("language").keys());
		assertEquals("http://api.magex.ca/crm/schema/lookup/Languages", linked.getObject("language").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/languages/en", linked.getObject("language").getString("@id"));
		assertEquals("EN", linked.getObject("language").getString("@value"));
		assertEquals("English", linked.getObject("language").getString("@en"));
		assertEquals("Anglais", linked.getObject("language").getString("@fr"));
		assertEquals("user@work.ca", linked.getString("email"));
		assertEquals(List.of("@context", "number", "extension"), linked.getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", linked.getObject("homePhone").getString("@context"));
		assertEquals("5551234567", linked.getObject("homePhone").getString("number"));
		assertEquals("42", linked.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", linked.getString("faxNumber"));
		assertEquals(communication, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(communication, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), root.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", root.getString("@context"));
		assertEquals("Developer", root.getString("jobTitle"));
		assertEquals("http://api.magex.ca/crm/rest/lookups/languages/en", root.getString("language"));
		assertEquals("user@work.ca", root.getString("email"));
		assertEquals(List.of("@context", "number", "extension"), root.getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", root.getObject("homePhone").getString("@context"));
		assertEquals("5551234567", root.getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getString("faxNumber"));
		assertEquals(communication, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(communication, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), english.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", english.getString("@context"));
		assertEquals("Developer", english.getString("jobTitle"));
		assertEquals("English", english.getString("language"));
		assertEquals("user@work.ca", english.getString("email"));
		assertEquals(List.of("@context", "number", "extension"), english.getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", english.getObject("homePhone").getString("@context"));
		assertEquals("5551234567", english.getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getString("faxNumber"));
		assertEquals(communication, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(communication, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), french.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", french.getString("@context"));
		assertEquals("Developer", french.getString("jobTitle"));
		assertEquals("Anglais", french.getString("language"));
		assertEquals("user@work.ca", french.getString("email"));
		assertEquals(List.of("@context", "number", "extension"), french.getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", french.getObject("homePhone").getString("@context"));
		assertEquals("5551234567", french.getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getString("faxNumber"));
		assertEquals(communication, transformer.parse(french, Lang.FRENCH));
	}
	
}
