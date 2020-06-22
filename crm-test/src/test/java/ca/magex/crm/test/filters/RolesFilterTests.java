package ca.magex.crm.test.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class RolesFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = RolesFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			/* ignore Identifiers fields */
			if (Identifier.class.isAssignableFrom(field.getType())) {
				continue;
			}
			assertTrue(RolesFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(RolesFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be code ascending */
		assertEquals(Sort.by(Order.asc("code")), RolesFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(RolesFilter.getDefaultSort(), RolesFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		RolesFilter filter = new RolesFilter();
		assertNull(filter.getGroupId());
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":null,\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(null, null, null, null, null), filter);
		assertEquals(new RolesFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withGroupId(new Identifier("G1"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());		
		assertEquals("{\"groupId\":\"G1\",\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withEnglishName("english");
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withFrenchName("french");
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withCode("code");
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		RolesFilter filter = new RolesFilter(Map.of());
		assertNull(filter.getGroupId());
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":null,\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(null, null, null, null, null), filter);
		assertEquals(new RolesFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code", "status", ""));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code", "status", "active"));
		assertEquals(new Identifier("G1"), filter.getGroupId());
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE), filter);
		assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new RolesFilter(Map.of("groupId", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate roles filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("englishName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate roles filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("frenchName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate roles filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("code", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate roles filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate roles filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		Role role = new Role(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, new Localized("RT", "Road and Track", "Route et Piste"));
		/* default filter should match */
		assertTrue(new RolesFilter().apply(role));
		
		/* test groupId match */
		assertTrue(new RolesFilter().withGroupId(new Identifier("G1")).apply(role));
		assertFalse(new RolesFilter().withGroupId(new Identifier("G2")).apply(role));
		
		/* test english match */
		assertTrue(new RolesFilter().withEnglishName("ROAD").apply(role));
		assertTrue(new RolesFilter().withEnglishName("Road and Track").apply(role));
		assertFalse(new RolesFilter().withEnglishName("bobby").apply(role));
		
		/* test french match */
		assertTrue(new RolesFilter().withFrenchName("PISTE").apply(role));
		assertTrue(new RolesFilter().withFrenchName("Route et Piste").apply(role));
		assertFalse(new RolesFilter().withFrenchName("bobby").apply(role));
		
		/* test code match */
		assertTrue(new RolesFilter().withCode("RT").apply(role));
		assertFalse(new RolesFilter().withCode("TR").apply(role));
		
		/* test status match */
		assertTrue(new RolesFilter().withStatus(Status.ACTIVE).apply(role));
		assertFalse(new RolesFilter().withStatus(Status.INACTIVE).apply(role));
		
	}
}
