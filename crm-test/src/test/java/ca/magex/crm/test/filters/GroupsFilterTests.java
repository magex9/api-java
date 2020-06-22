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
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class GroupsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = GroupsFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			assertTrue(GroupsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(GroupsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be code ascending */
		assertEquals(Sort.by(Order.asc("code")), GroupsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(GroupsFilter.getDefaultSort(), GroupsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		GroupsFilter filter = new GroupsFilter();
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter(null, null, null, null), filter);
		assertEquals(new GroupsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withEnglishName("english");
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", null, null, null), filter);
		assertEquals(new GroupsFilter("english", null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withFrenchName("french");
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", null, null), filter);
		assertEquals(new GroupsFilter("english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withCode("code");
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE), filter);
		assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		GroupsFilter filter = new GroupsFilter(Map.of());
		assertNull(filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter(null, null, null, null), filter);
		assertEquals(new GroupsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english"));
		assertEquals("english", filter.getEnglishName());
		assertNull(filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", null, null, null), filter);
		assertEquals(new GroupsFilter("english", null, null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french"));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertNull(filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", null, null), filter);
		assertEquals(new GroupsFilter("english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code"));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code", "status", ""));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertNull(filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code", "status", "active"));
		assertEquals("english", filter.getEnglishName());
		assertEquals("french", filter.getFrenchName());
		assertEquals("code", filter.getCode());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE), filter);
		assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new GroupsFilter(Map.of("englishName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate groups filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("frenchName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate groups filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("code", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate groups filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate groups filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		Group group = new Group(new Identifier("ABC"), Status.ACTIVE, new Localized("RT", "Road and Track", "Route et Piste"));
		/* default filter should match */
		assertTrue(new GroupsFilter().apply(group));
		
		/* test english match */
		assertTrue(new GroupsFilter().withEnglishName("ROAD").apply(group));
		assertTrue(new GroupsFilter().withEnglishName("Road and Track").apply(group));
		assertFalse(new GroupsFilter().withEnglishName("bobby").apply(group));
		
		/* test french match */
		assertTrue(new GroupsFilter().withFrenchName("PISTE").apply(group));
		assertTrue(new GroupsFilter().withFrenchName("Route et Piste").apply(group));
		assertFalse(new GroupsFilter().withFrenchName("bobby").apply(group));
		
		/* test code match */
		assertTrue(new GroupsFilter().withCode("RT").apply(group));
		assertFalse(new GroupsFilter().withCode("TR").apply(group));
		
		/* test status match */
		assertTrue(new GroupsFilter().withStatus(Status.ACTIVE).apply(group));
		assertFalse(new GroupsFilter().withStatus(Status.INACTIVE).apply(group));
		
	}
}
