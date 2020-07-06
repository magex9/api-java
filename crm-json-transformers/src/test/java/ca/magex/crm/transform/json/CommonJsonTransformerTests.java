package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonPair;

public class CommonJsonTransformerTests {

	@Test
	public void testFormatTextNull() throws Exception {
		CrmServices crm = TestCrm.build();
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		transformer.formatText(null, null, null);
	}
	
	@Test
	public void testFormatTextKey() throws Exception {
		CrmServices crm = TestCrm.build();
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		Option option = new Option(new AuthenticationGroupIdentifier("o"), 
				new AuthenticationGroupIdentifier("p"), Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, GROUP);
		transformer.formatText(pairs, "code", option);
	}
	
	@Test
	public void testGetPropertyOptions() throws Exception {
		CrmServices crm = TestCrm.build();
		AbstractJsonTransformer<Option> transformer = new OptionJsonTransformer(crm);
		Option option = new Option(new AuthenticationGroupIdentifier("o"), 
				new AuthenticationGroupIdentifier("p"), Type.AUTHENTICATION_GROUP, Status.ACTIVE, false, GROUP);
		try {
			transformer.getProperty(null, "code", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Object cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, null, String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Key cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, "code", null);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Type cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(option, "code", Option.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get code from {\"optionId\":\"\\/options\\/authentication-groups\\/o\",\"parentId\":\"\\/options\\/authentication-groups\\/p\",\"type\":{\"name\":{\"code\":\"AUTHENTICATION_GROUPS\",\"en\":\"Authentication Groups\",\"fr\":\"Groupes d'authentification\"},\"parent\":null,\"recrussive\":true,\"extendable\":true,\"choice\":false},\"status\":\"ACTIVE\",\"mutable\":false,\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
		try {
			transformer.getProperty(option, "invalid", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get invalid from {\"optionId\":\"\\/options\\/authentication-groups\\/o\",\"parentId\":\"\\/options\\/authentication-groups\\/p\",\"type\":{\"name\":{\"code\":\"AUTHENTICATION_GROUPS\",\"en\":\"Authentication Groups\",\"fr\":\"Groupes d'authentification\"},\"parent\":null,\"recrussive\":true,\"extendable\":true,\"choice\":false},\"status\":\"ACTIVE\",\"mutable\":false,\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
	}
	
}
