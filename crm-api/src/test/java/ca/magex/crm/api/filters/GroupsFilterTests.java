package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
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
			Assertions.assertTrue(GroupsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assertions.assertTrue(GroupsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be code ascending */
		Assertions.assertEquals(Sort.by(Order.asc("code")), GroupsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(GroupsFilter.getDefaultSort(), GroupsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		GroupsFilter filter = new GroupsFilter();
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter(null, null, null, null), filter);
		Assertions.assertEquals(new GroupsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withEnglishName("english");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", null, null, null), filter);
		Assertions.assertEquals(new GroupsFilter("english", null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withFrenchName("french");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", null, null), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withCode("code");
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		GroupsFilter filter = new GroupsFilter(Map.of());
		Assertions.assertNull(filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":null,\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter(null, null, null, null), filter);
		Assertions.assertEquals(new GroupsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertNull(filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":null,\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", null, null, null), filter);
		Assertions.assertEquals(new GroupsFilter("english", null, null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertNull(filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", null, null), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", null, null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code", "status", ""));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", null).hashCode(), filter.hashCode());
		
		filter = new GroupsFilter(Map.of("englishName", "english", "frenchName", "french", "code", "code", "status", "active"));
		Assertions.assertEquals("english", filter.getEnglishName());
		Assertions.assertEquals("french", filter.getFrenchName());
		Assertions.assertEquals("code", filter.getCode());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"englishName\":\"english\",\"frenchName\":\"french\",\"code\":\"code\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE), filter);
		Assertions.assertEquals(new GroupsFilter("english", "french", "code", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new GroupsFilter(Map.of("englishName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate groups filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("frenchName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate groups filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("code", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate groups filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate groups filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new GroupsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		Group group = new Group(new Identifier("ABC"), Status.ACTIVE, new Localized("RT", "Road and Track", "Route et Piste"));
		/* default filter should match */
		Assertions.assertTrue(new GroupsFilter().apply(group));
		
		/* test english match */
		Assertions.assertTrue(new GroupsFilter().withEnglishName("ROAD").apply(group));
		Assertions.assertTrue(new GroupsFilter().withEnglishName("Road and Track").apply(group));
		Assertions.assertFalse(new GroupsFilter().withEnglishName("bobby").apply(group));
		
		/* test french match */
		Assertions.assertTrue(new GroupsFilter().withFrenchName("PISTE").apply(group));
		Assertions.assertTrue(new GroupsFilter().withFrenchName("Route et Piste").apply(group));
		Assertions.assertFalse(new GroupsFilter().withFrenchName("bobby").apply(group));
		
		/* test code match */
		Assertions.assertTrue(new GroupsFilter().withCode("RT").apply(group));
		Assertions.assertFalse(new GroupsFilter().withCode("TR").apply(group));
		
		/* test status match */
		Assertions.assertTrue(new GroupsFilter().withStatus(Status.ACTIVE).apply(group));
		Assertions.assertFalse(new GroupsFilter().withStatus(Status.INACTIVE).apply(group));
		
	}
}
