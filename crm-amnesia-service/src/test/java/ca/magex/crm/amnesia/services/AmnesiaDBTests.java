package ca.magex.crm.amnesia.services;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

public class AmnesiaDBTests {

	@Test
	public void testAmnesiaPasswordEncoder() throws Exception {
		assertEquals(AmnesiaPasswordEncoder.class, new AmnesiaDB().getPasswordEncoder().getClass());
		assertEquals(BCryptPasswordEncoder.class, new AmnesiaDB(new BCryptPasswordEncoder()).getPasswordEncoder().getClass());
	}
	
	@Test
	public void testSystemInitiailization() throws Exception {
		AmnesiaDB db = new AmnesiaDB();
		assertFalse(db.isInitialized());
		db.initialize("org", new PersonName(null, "Scott", null, "Finlay"), "admin@admin.com", "admin", "admin");
		assertTrue(db.isInitialized());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testResetData() throws Exception {
		AmnesiaDB db = new AmnesiaDB();
		Field field = AmnesiaDB.class.getDeclaredField("data");
		field.setAccessible(true);
		assertEquals(0, ((Map<Identifier, Serializable>)field.get(db)).size());
		new AmnesiaPermissionService(db).createGroup("A", new Localized("A"));
		assertEquals(1, ((Map<Identifier, Serializable>)field.get(db)).size());
		db.dump();
		db.reset();
		db.dump();
		assertEquals(0, ((Map<Identifier, Serializable>)field.get(db)).size());
	}
	
}
