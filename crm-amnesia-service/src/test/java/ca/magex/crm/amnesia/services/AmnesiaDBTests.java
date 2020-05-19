package ca.magex.crm.amnesia.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public class AmnesiaDBTests {

	@Test
	public void testAmnesiaPasswordEncoder() throws Exception {
		assertEquals(AmnesiaPasswordEncoder.class, new AmnesiaDB(new AmnesiaPasswordEncoder()).getPasswordEncoder().getClass());
		assertEquals(BCryptPasswordEncoder.class, new AmnesiaDB(new BCryptPasswordEncoder()).getPasswordEncoder().getClass());
	}
	
	@Test
	public void testSystemInitiailization() throws Exception {
		AmnesiaDB db = new AmnesiaDB(new AmnesiaPasswordEncoder());
		assertFalse(db.isInitialized());
		Identifier systemId = db.initialize("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin");
		assertTrue(db.isInitialized());
		assertEquals(systemId, db.initialize("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin"));
	}
	
	@Test
	public void testCheckImplementations() throws Exception {
		AmnesiaDB db = new AmnesiaDB(new AmnesiaPasswordEncoder());
		assertEquals(AmnesiaLookupService.class, db.getLookups().getClass());
		assertEquals(AmnesiaPermissionService.class, db.getPermissions().getClass());
		assertEquals(AmnesiaOrganizationService.class, db.getOrganizations().getClass());
		assertEquals(AmnesiaLocationService.class, db.getLocations().getClass());
		assertEquals(AmnesiaPersonService.class, db.getPersons().getClass());
		assertEquals(AmnesiaUserService.class, db.getUsers().getClass());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testResetData() throws Exception {
		AmnesiaDB db = new AmnesiaDB(new AmnesiaPasswordEncoder());
		Field field = AmnesiaDB.class.getDeclaredField("data");
		field.setAccessible(true);
		assertEquals(0, ((Map<Identifier, Serializable>)field.get(db)).size());
		new AmnesiaPermissionService(db).createGroup(new Localized("A", "A", "A"));
		assertEquals(1, ((Map<Identifier, Serializable>)field.get(db)).size());
		db.dump();
		db.reset();
		db.dump();
		assertEquals(0, ((Map<Identifier, Serializable>)field.get(db)).size());
	}
	
	@Test
	public void testDataDump() throws Exception {
		AmnesiaDB db = new AmnesiaDB(new AmnesiaPasswordEncoder());
		db.getPermissions().createGroup(new Localized("A", "A", "A"));
		db.getPermissions().createGroup(new Localized("B", "B", "B"));
		db.getPermissions().createGroup(new Localized("C", "C", "C"));
		db.getPermissions().createGroup(new Localized("D", "D", "D"));
		db.dump(System.out);
	}
	
}
