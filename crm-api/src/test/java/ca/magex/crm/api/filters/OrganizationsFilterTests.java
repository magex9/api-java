package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class OrganizationsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = OrganizationsFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			Assertions.assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assertions.assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be displayName ascending */
		Assertions.assertEquals(Sort.by(Order.asc("displayName")), OrganizationsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(OrganizationsFilter.getDefaultSort(), OrganizationsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		OrganizationsFilter filter = new OrganizationsFilter();
		Assertions.assertNull(filter.getDisplayName());		
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter(null, null), filter);
		Assertions.assertEquals(new OrganizationsFilter(null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter("display", null), filter);
		Assertions.assertEquals(new OrganizationsFilter("display", null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals("display", filter.getDisplayName());		
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter("display", Status.ACTIVE), filter);
		Assertions.assertEquals(new OrganizationsFilter("display", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		OrganizationsFilter filter = new OrganizationsFilter(Map.of());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter(null, null), filter);
		Assertions.assertEquals(new OrganizationsFilter(null, null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display"));
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter("display", null), filter);
		Assertions.assertEquals(new OrganizationsFilter("display", null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", ""));
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter("display", null), filter);
		Assertions.assertEquals(new OrganizationsFilter("display", null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", "active"));
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new OrganizationsFilter("display", Status.ACTIVE), filter);
		Assertions.assertEquals(new OrganizationsFilter("display", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new OrganizationsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
			
		try {
			new OrganizationsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new OrganizationsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		OrganizationSummary organization = new OrganizationSummary(new Identifier("ABC"), Status.ACTIVE, "Road and Track");
		/* default filter should match */
		Assertions.assertTrue(new OrganizationsFilter().apply(organization));
		
		/* test display match */
		Assertions.assertTrue(new OrganizationsFilter().withDisplayName("ROAD").apply(organization));
		Assertions.assertTrue(new OrganizationsFilter().withDisplayName("Road and Track").apply(organization));
		Assertions.assertFalse(new OrganizationsFilter().withDisplayName("bobby").apply(organization));
				
		/* test status match */
		Assertions.assertTrue(new OrganizationsFilter().withStatus(Status.ACTIVE).apply(organization));
		Assertions.assertFalse(new OrganizationsFilter().withStatus(Status.INACTIVE).apply(organization));
		
	}
}
