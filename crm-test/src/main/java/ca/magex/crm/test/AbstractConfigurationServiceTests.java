package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.json.model.JsonObject;

@Transactional
public abstract class AbstractConfigurationServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected CrmAuthenticationService auth;
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testSystemInitiailization() throws Exception {
		assertFalse(crm.isInitialized());
		Identifier systemId = crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin").getUserId();
		assertTrue(crm.isInitialized());
		try {
			assertEquals(systemId, crm.initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), SYSTEM_EMAIL, "admin", "admin").getUserId());
			fail("System is already initialized");
		} catch (DuplicateItemFoundException expected) { }
	}
	
	@Test
	public void testDataDump() throws Exception {
		crm.initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin");
		auth.login("admin", "admin");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		crm.dump(baos);
		String[] lines = baos.toString().split("\n");
		assertEquals(184, lines.length);
		for (String line : lines) {
			Matcher m = Pattern.compile("([/\\-A-Za-z0-9]+) => (\\{.*\\}|true|false)").matcher(line);
			if (!m.matches())
				fail("Line did not match the pattern: " + line);
			JsonObject json = new JsonObject(m.group(2));
			assertEquals("ACTIVE", json.getString("status"));
			if (json.contains("configurationId")) {
				assertEquals(m.group(1), json.getString("configurationId"));
			} else if (json.contains("optionId")) {
				assertEquals(m.group(1), json.getString("optionId"));
			} else if (json.contains("userId")) {
				assertEquals(m.group(1), json.getString("userId"));
			} else if (json.contains("personId")) {
				assertEquals(m.group(1), json.getString("personId"));
			} else if (json.contains("locationId")) {
				assertEquals(m.group(1), json.getString("locationId"));
			} else if (json.contains("organizationId")) {
				assertEquals(m.group(1), json.getString("organizationId"));
			} else {
				throw new IllegalArgumentException("Unexpected initialize line: " + line);
			}
		}
	}
	
	@Test
	public void testDataDumpToInvalidFile() throws Exception {
		crm.initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin");
		auth.login("admin", "admin");
		try {
			crm.dump(null);
			fail("Exception will be thrown");
		} catch (Exception e) { }
	}
	
}
