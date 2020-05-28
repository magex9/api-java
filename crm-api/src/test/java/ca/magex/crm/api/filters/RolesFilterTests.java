package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
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
			Assertions.assertTrue(RolesFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))), field.getName());
			Assertions.assertTrue(RolesFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))), field.getName());
		}
		/* default sort should be code ascending */
		Assertions.assertEquals(Sort.by(Order.asc("code")), RolesFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(RolesFilter.getDefaultSort(), RolesFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		RolesFilter filter = new RolesFilter();
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":null,\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(null, null, null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withGroupId(new Identifier("G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getGroupId());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());		
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withEnglishName("english");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withFrenchName("french");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withCode("code");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		RolesFilter filter = new RolesFilter(Map.of());
		Assertions.assertNull(filter.getGroupId());
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":null,\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(null, null, null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(null, null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getGroupId());
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), null, null, null, null).hashCode(), filter.hashCode());
		
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", null, null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code", "status", ""));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new RolesFilter(Map.of("groupId", "G1", "englishName", "english", "frenchName", "french", "code", "code", "status", "active"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"groupId\":\"G1\",\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE), filter);
		Assertions.assertEquals(new RolesFilter(new Identifier("G1"), "english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new RolesFilter(Map.of("groupId", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate roles filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("englishName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate roles filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("frenchName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate roles filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("code", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate roles filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate roles filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new RolesFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		Role role = new Role(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, new Localized("RT", "Road and Track", "Route et Piste"));
		/* default filter should match */
		Assertions.assertTrue(new RolesFilter().apply(role));
		
		/* test groupId match */
		Assertions.assertTrue(new RolesFilter().withGroupId(new Identifier("G1")).apply(role));
		Assertions.assertFalse(new RolesFilter().withGroupId(new Identifier("G2")).apply(role));
		
		/* test english match */
		Assertions.assertTrue(new RolesFilter().withEnglishName("ROAD").apply(role));
		Assertions.assertTrue(new RolesFilter().withEnglishName("Road and Track").apply(role));
		Assertions.assertFalse(new RolesFilter().withEnglishName("bobby").apply(role));
		
		/* test french match */
		Assertions.assertTrue(new RolesFilter().withFrenchName("PISTE").apply(role));
		Assertions.assertTrue(new RolesFilter().withFrenchName("Route et Piste").apply(role));
		Assertions.assertFalse(new RolesFilter().withFrenchName("bobby").apply(role));
		
		/* test code match */
		Assertions.assertTrue(new RolesFilter().withCode("RT").apply(role));
		Assertions.assertFalse(new RolesFilter().withCode("TR").apply(role));
		
		/* test status match */
		Assertions.assertTrue(new RolesFilter().withStatus(Status.ACTIVE).apply(role));
		Assertions.assertFalse(new RolesFilter().withStatus(Status.INACTIVE).apply(role));
		
	}
}
