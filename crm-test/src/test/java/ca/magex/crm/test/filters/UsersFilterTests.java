package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class UsersFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = UsersFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			/* ignore Identifiers fields */
			if (Identifier.class.isAssignableFrom(field.getType())) {
				continue;
			}
			/* ignore role since you can't sort by a list */
			if (field.getName().contentEquals("role")) {
				continue;
			}
			assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be username ascending */
		assertEquals(Sort.by(Order.asc("username")), UsersFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(UsersFilter.getDefaultSort(), UsersFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		UsersFilter filter = new UsersFilter();
		assertNull(filter.getOrganizationId());
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":null,\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(null, null, null, null, null), filter);
		assertEquals(new UsersFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withPersonId(new Identifier("P1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withUsername("Admin");
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = filter.withRole("USR");
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals("USR", filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
				
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals("USR", filter.getRole());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR"), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR").hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		UsersFilter filter = new UsersFilter(Map.of());
		assertNull(filter.getOrganizationId());
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":null,\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(null, null, null, null, null), filter);
		assertEquals(new UsersFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertNull(filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":null}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals("USR", filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR", "status", ""));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals("USR", filter.getRole());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
				
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR", "status", "active"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals(new Identifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals("USR", filter.getRole());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR"), filter);
		assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR").hashCode(), filter.hashCode());
		
		try {
			new UsersFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate users filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new UsersFilter(Map.of("personId", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate users filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("username", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate users filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("role", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate users filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate users filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new UsersFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		PersonSummary person = new PersonSummary(new Identifier("P1"), new Identifier("G1"), Status.ACTIVE, "Bobby Thomson");
		User user = new User(new Identifier("DEF"), "Admin", person, Status.ACTIVE, List.of("USR", "ADM"));
		/* default filter should match */
		assertTrue(new UsersFilter().apply(user));
		
		/* test organization match */
		assertTrue(new UsersFilter().withOrganizationId(new Identifier("G1")).apply(user));
		assertFalse(new UsersFilter().withOrganizationId(new Identifier("G2")).apply(user));
		
		/* test person match */
		assertTrue(new UsersFilter().withPersonId(new Identifier("P1")).apply(user));
		assertFalse(new UsersFilter().withPersonId(new Identifier("P2")).apply(user));
		
		/* test username match */
		assertTrue(new UsersFilter().withUsername("AD").apply(user));
		assertTrue(new UsersFilter().withUsername("Admin").apply(user));
		assertFalse(new UsersFilter().withUsername("User").apply(user));
		
		/* test role match */
		assertTrue(new UsersFilter().withRole("ADM").apply(user));
		assertFalse(new UsersFilter().withRole("BDG").apply(user));
		
		/* test status match */
		assertTrue(new UsersFilter().withStatus(Status.ACTIVE).apply(user));
		assertFalse(new UsersFilter().withStatus(Status.INACTIVE).apply(user));
		
	}
}
