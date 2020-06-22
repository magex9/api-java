package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.GROUP;
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

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public abstract class AbstractConfigurationServiceTests {

	@Autowired
	private Crm crm;
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@Test
	public void testSystemInitiailization() throws Exception {
		assertFalse(crm.isInitialized());
		Identifier systemId = crm.initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin").getUserId();
		assertTrue(crm.isInitialized());
		try {
			assertEquals(systemId, crm.initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin").getUserId());
			fail("System is already initialized");
		} catch (DuplicateItemFoundException expected) { }
	}
	
	@Test
	public void testDataDump() throws Exception {
		crm.createGroup(new Localized("A", "A", "A"));
		crm.createGroup(new Localized("B", "B", "B"));
		crm.createGroup(new Localized("C", "C", "C"));
		crm.createGroup(new Localized("D", "D", "D"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println(baos.toString());
		crm.dump(baos);
		String[] lines = baos.toString().split("\n");
		assertEquals(4, lines.length);
		for (String line : lines) {
			Matcher m = Pattern.compile("([A-Za-z0-9]+) => \\{\"groupId\":\"([A-Za-z0-9]+)\",\"status\":\"ACTIVE\",\"name\":\\{\"code\":\"(A|B|C|D)\",\"en\":\"([A-Z])\",\"fr\":\"([A-Z])\"\\}\\}").matcher(line);
			if (!m.matches())
				fail("Line did not match the pattern: " + line);
			assertEquals(m.group(1), m.group(2));
			assertEquals(m.group(3), m.group(4));
			assertEquals(m.group(3), m.group(5));
		}
	}
	
	@Test
	public void testDataDumpToInvalidFile() throws Exception {
		crm.createGroup(GROUP);
		try {
			crm.dump(null);
			fail("Exception will be thrown");
		} catch (Exception e) { }
	}
	
}
