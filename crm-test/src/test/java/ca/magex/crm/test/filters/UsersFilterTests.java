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

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

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
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":null,\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(null, null, null, null), filter);
		assertEquals(new UsersFilter(null, null, null, null).hashCode(), filter.hashCode());
				
		filter = filter.withPersonId(new PersonIdentifier("P1"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, null, null), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withUsername("Admin");
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", null), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = filter.withRoleId(new AuthenticationRoleIdentifier("USR"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":\"\\/options\\/authentication-roles\\/USR\"}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
				
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"roleId\":\"\\/options\\/authentication-roles\\/USR\"}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		UsersFilter filter = new UsersFilter(Map.of());
		assertNull(filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":null,\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(null, null, null, null), filter);
		assertEquals(new UsersFilter(null, null, null, null).hashCode(), filter.hashCode());
				
		filter = new UsersFilter(Map.of("personId", "P1"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertNull(filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, null, null), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("personId", "P1", "username", "Admin"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertNull(filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":null}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", null), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":\"\\/options\\/authentication-roles\\/USR\"}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
		
		filter = new UsersFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR", "status", ""));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		assertNull(filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":\"\\/options\\/authentication-roles\\/USR\"}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
				
		filter = new UsersFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR", "status", "active"));
		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		assertEquals("Admin", filter.getUsername());
		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"roleId\":\"\\/options\\/authentication-roles\\/USR\"}", filter.toString());
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		assertEquals(new UsersFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
		
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
		User user = new User(new UserIdentifier("DEF"), new PersonIdentifier("P1"), "Admin", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("USR"), new AuthenticationRoleIdentifier("ADM")));
		/* default filter should match */
		assertTrue(new UsersFilter().apply(user));
		
		/* test person match */
		assertTrue(new UsersFilter().withPersonId(new PersonIdentifier("P1")).apply(user));
		assertFalse(new UsersFilter().withPersonId(new PersonIdentifier("P2")).apply(user));
		
		/* test username match */
		assertTrue(new UsersFilter().withUsername("AD").apply(user));
		assertTrue(new UsersFilter().withUsername("Admin").apply(user));
		assertFalse(new UsersFilter().withUsername("User").apply(user));
		
		/* test role match */
		assertTrue(new UsersFilter().withRoleId(new AuthenticationRoleIdentifier("ADM")).apply(user));
		assertFalse(new UsersFilter().withRoleId(new AuthenticationRoleIdentifier("BDG")).apply(user));
		
		/* test status match */
		assertTrue(new UsersFilter().withStatus(Status.ACTIVE).apply(user));
		assertFalse(new UsersFilter().withStatus(Status.INACTIVE).apply(user));
		
	}
}
