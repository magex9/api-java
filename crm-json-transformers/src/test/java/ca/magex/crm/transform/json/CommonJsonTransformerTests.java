package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.json.model.JsonPair;

public class CommonJsonTransformerTests {

	@Test
	public void testFormatTextNull() throws Exception {
		CrmServices crm = new AmnesiaCrm();
		AbstractJsonTransformer<Group> transformer = new GroupJsonTransformer(crm);
		transformer.formatText(null, null, null);
	}
	
	@Test
	public void testFormatTextKey() throws Exception {
		CrmServices crm = new AmnesiaCrm();
		AbstractJsonTransformer<Group> transformer = new GroupJsonTransformer(crm);
		List<JsonPair> pairs = new ArrayList<JsonPair>();
		Group group = new Group(new Identifier("g"), Status.ACTIVE, GROUP);
		transformer.formatText(pairs, "code", group);
	}
	
	@Test
	public void testGetPropertyOptions() throws Exception {
		CrmServices crm = new AmnesiaCrm();
		AbstractJsonTransformer<Group> transformer = new GroupJsonTransformer(crm);
		Group group = new Group(new Identifier("g"), Status.ACTIVE, GROUP);
		try {
			transformer.getProperty(null, "code", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Object cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(group, null, String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Key cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(group, "code", null);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Type cannot be null", e.getMessage());
		}
		try {
			transformer.getProperty(group, "code", Group.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get code from {\"groupId\":\"g\",\"status\":\"ACTIVE\",\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
		try {
			transformer.getProperty(group, "invalid", String.class);
			fail("Illegal arguments");
		} catch (IllegalArgumentException e) {
			assertEquals("Unable to get invalid from {\"groupId\":\"g\",\"status\":\"ACTIVE\",\"name\":{\"code\":\"GRP\",\"en\":\"Group\",\"fr\":\"Groupe\"}}", e.getMessage());
		}
	}
	
}
