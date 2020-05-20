package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public abstract class AbstractInitializationServiceTests {

	public abstract CrmInitializationService getInitializationService();

	public abstract CrmPermissionService getPermissionService();
	
	@Before
	public void setup() {
		getInitializationService().reset();
	}
	
	@Test
	public void testSystemInitiailization() throws Exception {
		assertFalse(getInitializationService().isInitialized());
		Identifier systemId = getInitializationService().initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin").getUserId();
		assertTrue(getInitializationService().isInitialized());
		assertEquals(systemId, getInitializationService().initializeSystem("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin").getUserId());
	}
	
	@Test
	public void testDataDump() throws Exception {
		getPermissionService().createGroup(new Localized("A", "A", "A"));
		getPermissionService().createGroup(new Localized("B", "B", "B"));
		getPermissionService().createGroup(new Localized("C", "C", "C"));
		getPermissionService().createGroup(new Localized("D", "D", "D"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println(baos.toString());
		getInitializationService().dump(baos);
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
		getPermissionService().createGroup(GROUP);
		try {
			getInitializationService().dump(null);
			fail("Exception will be thrown");
		} catch (Exception e) { }
	}
	
}
