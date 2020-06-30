package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;

public class OptionsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = OptionsFilter.class.getDeclaredFields();
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
			if (field.getName().contentEquals("name")) {
				continue;
			}
			/* ignore role since you can't sort by a list */
			if (field.getName().contentEquals("type")) {
				continue;
			}
			assertTrue(field.getName() + " missing from options", OptionsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(OptionsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be username ascending */
		assertEquals(Sort.by(Order.asc("code")), OptionsFilter.getDefaultSort());

		/* default paging, should use default sort */
		assertEquals(OptionsFilter.getDefaultSort(), OptionsFilter.getDefaultPaging().getSort());
	}

	@Test
	public void testFilterConstructs() {
		OptionsFilter filter = new OptionsFilter();
		assertNull(filter.getName());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":null,\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(null, null, null, null), filter);
		assertEquals(new OptionsFilter(null, null, null, null).hashCode(), filter.hashCode());

		filter = filter.withName(Lang.ROOT, "ADM");
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"{\\\"left\\\":\\\"\\\",\\\"right\\\":\\\"ADM\\\"}\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM");
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"{\\\"left\\\":\\\"\\\",\\\"right\\\":\\\"ADM\\\"}\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV"));
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"{\\\"left\\\":\\\"\\\",\\\"right\\\":\\\"ADM\\\"}\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE);
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"{\\\"left\\\":\\\"\\\",\\\"right\\\":\\\"ADM\\\"}\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":\"AUTHENTICATION_ROLE\",\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE).withStatus(Status.ACTIVE);
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"name\":\"{\\\"left\\\":\\\"\\\",\\\"right\\\":\\\"ADM\\\"}\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":\"AUTHENTICATION_ROLE\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	@Ignore
	public void testFilterMapConstructions() {
		OptionsFilter filter = new OptionsFilter(Map.of());
		assertNull(filter.getName());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":null,\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(null, null, null, null), filter);
		assertEquals(new OptionsFilter(null, null, null, null).hashCode(), filter.hashCode());

		filter = new OptionsFilter(Map.of("name", "ADM"));
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,ADM)\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());
		
		filter = new OptionsFilter(Map.of("englishName", "Admin"));
		assertEquals(Lang.ENGLISH, filter.getName().getKey());
		assertEquals("Admin", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,Admin)\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());
		
		filter = new OptionsFilter(Map.of("frenchName", "Admin"));
		assertEquals(Lang.FRENCH, filter.getName().getKey());
		assertEquals("Admin", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,Admin)\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM");
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertNull(filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,ADM)\",\"parentId\":null,\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), null, null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV"));
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertNull(filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,ADM)\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":null,\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), null, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), null, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE);
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		assertNull(filter.getStatus());
		assertEquals("{\"name\":\"(,ADM)\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":" + Type.AUTHENTICATION_ROLE + ",\"status\":null}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null).hashCode(), filter.hashCode());

		filter = filter.withOptionCode("ADM").withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE).withStatus(Status.ACTIVE);
		assertEquals(Lang.ROOT, filter.getName().getKey());
		assertEquals("ADM", filter.getName().getValue());
		assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"name\":\"(,ADM)\",\"parentId\":\"\\/options\\/authentication-groups\\/DEV\",\"type\":" + Type.AUTHENTICATION_ROLE + ",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE), filter);
		assertEquals(new OptionsFilter(new ImmutablePair<>(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE).hashCode(), filter.hashCode());
	}

	//	@Test
	//	public void testFilterMapConstructions() {
	//		OptionsFilter filter = new OptionsFilter(Map.of());
	//		assertNull(filter.getPersonId());
	//		assertNull(filter.getUsername());
	//		assertNull(filter.getRoleId());
	//		assertNull(filter.getStatus());
	//		assertEquals("{\"personId\":null,\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
	//		assertEquals(new OptionsFilter(null, null, null, null), filter);
	//		assertEquals(new OptionsFilter(null, null, null, null).hashCode(), filter.hashCode());
	//				
	//		filter = new OptionsFilter(Map.of("personId", "P1"));
	//		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
	//		assertNull(filter.getUsername());
	//		assertNull(filter.getRoleId());
	//		assertNull(filter.getStatus());
	//		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":null,\"roleId\":null}", filter.toString());
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, null, null), filter);
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, null, null).hashCode(), filter.hashCode());
	//		
	//		filter = new OptionsFilter(Map.of("personId", "P1", "username", "Admin"));
	//		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
	//		assertEquals("Admin", filter.getUsername());
	//		assertNull(filter.getRoleId());
	//		assertNull(filter.getStatus());
	//		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":null}", filter.toString());
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", null), filter);
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", null).hashCode(), filter.hashCode());
	//		
	//		filter = new OptionsFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR"));
	//		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
	//		assertEquals("Admin", filter.getUsername());
	//		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
	//		assertNull(filter.getStatus());
	//		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":\"\\/options\\/authenticationRoles\\/USR\"}", filter.toString());
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
	//		
	//		filter = new OptionsFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR", "status", ""));
	//		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
	//		assertEquals("Admin", filter.getUsername());
	//		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
	//		assertNull(filter.getStatus());
	//		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":null,\"username\":\"Admin\",\"roleId\":\"\\/options\\/authenticationRoles\\/USR\"}", filter.toString());
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
	//				
	//		filter = new OptionsFilter(Map.of("personId", "P1", "username", "Admin", "roleId", "USR", "status", "active"));
	//		assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
	//		assertEquals("Admin", filter.getUsername());
	//		assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
	//		assertEquals(Status.ACTIVE, filter.getStatus());
	//		assertEquals("{\"personId\":\"\\/persons\\/P1\",\"status\":\"ACTIVE\",\"username\":\"Admin\",\"roleId\":\"\\/options\\/authenticationRoles\\/USR\"}", filter.toString());
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
	//		assertEquals(new OptionsFilter(new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")).hashCode(), filter.hashCode());
	//		
	//		try {
	//			new OptionsFilter(Map.of("personId", 1));
	//		}
	//		catch(ApiException apie) {
	//			assertEquals("Unable to instantiate users filter", apie.getMessage());
	//			assertTrue(apie.getCause() instanceof ClassCastException);
	//		}	
	//		
	//		try {
	//			new OptionsFilter(Map.of("username", 1));
	//		}
	//		catch(ApiException apie) {
	//			assertEquals("Unable to instantiate users filter", apie.getMessage());
	//			assertTrue(apie.getCause() instanceof ClassCastException);
	//		}	
	//		
	//		try {
	//			new OptionsFilter(Map.of("role", 1));
	//		}
	//		catch(ApiException apie) {
	//			assertEquals("Unable to instantiate users filter", apie.getMessage());
	//			assertTrue(apie.getCause() instanceof ClassCastException);
	//		}	
	//		
	//		try {
	//			new OptionsFilter(Map.of("status", 1));
	//		}
	//		catch(ApiException apie) {
	//			assertEquals("Unable to instantiate users filter", apie.getMessage());
	//			assertTrue(apie.getCause() instanceof ClassCastException);
	//		}
	//		
	//		try {
	//			new OptionsFilter(Map.of("status", "dormant"));
	//		}
	//		catch(ApiException apie) {
	//			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
	//		}		
	//	}
	//	
	//	@Test
	//	public void testApplyFilter() {
	//		User user = new User(new UserIdentifier("DEF"), new PersonIdentifier("P1"), "Admin", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("USR"), new AuthenticationRoleIdentifier("ADM")));
	//		/* default filter should match */
	//		assertTrue(new OptionsFilter().apply(user));
	//		
	//		/* test person match */
	//		assertTrue(new OptionsFilter().withPersonId(new PersonIdentifier("P1")).apply(user));
	//		assertFalse(new OptionsFilter().withPersonId(new PersonIdentifier("P2")).apply(user));
	//		
	//		/* test username match */
	//		assertTrue(new OptionsFilter().withUsername("AD").apply(user));
	//		assertTrue(new OptionsFilter().withUsername("Admin").apply(user));
	//		assertFalse(new OptionsFilter().withUsername("User").apply(user));
	//		
	//		/* test role match */
	//		assertTrue(new OptionsFilter().withRoleId(new AuthenticationRoleIdentifier("ADM")).apply(user));
	//		assertFalse(new OptionsFilter().withRoleId(new AuthenticationRoleIdentifier("BDG")).apply(user));
	//		
	//		/* test status match */
	//		assertTrue(new OptionsFilter().withStatus(Status.ACTIVE).apply(user));
	//		assertFalse(new OptionsFilter().withStatus(Status.INACTIVE).apply(user));
	//		
	//	}
}
