package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@TestInstance(Lifecycle.PER_METHOD)
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
			Assertions.assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))), field.getName());
			Assertions.assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))), field.getName());
		}
		/* default sort should be username ascending */
		Assertions.assertEquals(Sort.by(Order.asc("username")), UsersFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(UsersFilter.getDefaultSort(), UsersFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		UsersFilter filter = new UsersFilter();
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":null,\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(null, null, null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withPersonId(new Identifier("P1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withUsername("Admin");
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = filter.withRole("USR");
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertEquals("USR", filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
				
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertEquals("USR", filter.getRole());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR"), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR").hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		UsersFilter filter = new UsersFilter(Map.of());
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":null,\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(null, null, null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":null,\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertNull(filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":null,\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertNull(filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":null}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertEquals("USR", filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR", "status", ""));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertEquals("USR", filter.getRole());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":null,\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR"), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), null, "Admin", "USR").hashCode(), filter.hashCode());
				
		filter = new UsersFilter(Map.of("organizationId", "G1", "personId", "P1", "username", "Admin", "role", "USR", "status", "active"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals(new Identifier("P1"), filter.getPersonId());
		Assertions.assertEquals("Admin", filter.getUsername());
		Assertions.assertEquals("USR", filter.getRole());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"personId\":\"P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"role\":\"USR\"}", filter.toString());
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR"), filter);
		Assertions.assertEquals(new UsersFilter(new Identifier("G1"), new Identifier("P1"), Status.ACTIVE, "Admin", "USR").hashCode(), filter.hashCode());
		
		try {
			new UsersFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new UsersFilter(Map.of("personId", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("username", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("role", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new UsersFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		PersonSummary person = new PersonSummary(new Identifier("P1"), new Identifier("G1"), Status.ACTIVE, "Bobby Thomson");
		User user = new User(new Identifier("DEF"), "Admin", person, Status.ACTIVE, List.of("USR", "ADM"));
		/* default filter should match */
		Assertions.assertTrue(new UsersFilter().apply(user));
		
		/* test organization match */
		Assertions.assertTrue(new UsersFilter().withOrganizationId(new Identifier("G1")).apply(user));
		Assertions.assertFalse(new UsersFilter().withOrganizationId(new Identifier("G2")).apply(user));
		
		/* test person match */
		Assertions.assertTrue(new UsersFilter().withPersonId(new Identifier("P1")).apply(user));
		Assertions.assertFalse(new UsersFilter().withPersonId(new Identifier("P2")).apply(user));
		
		/* test username match */
		Assertions.assertTrue(new UsersFilter().withUsername("AD").apply(user));
		Assertions.assertTrue(new UsersFilter().withUsername("Admin").apply(user));
		Assertions.assertFalse(new UsersFilter().withUsername("User").apply(user));
		
		/* test role match */
		Assertions.assertTrue(new UsersFilter().withRole("ADM").apply(user));
		Assertions.assertFalse(new UsersFilter().withRole("BDG").apply(user));
		
		/* test status match */
		Assertions.assertTrue(new UsersFilter().withStatus(Status.ACTIVE).apply(user));
		Assertions.assertFalse(new UsersFilter().withStatus(Status.INACTIVE).apply(user));
		
	}
}
