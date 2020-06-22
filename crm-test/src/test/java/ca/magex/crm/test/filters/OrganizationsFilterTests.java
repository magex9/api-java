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

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OrganizationsFilter;
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
			if (field.getName().equals("group")) {
				continue;
			}
			assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be displayName ascending */
		assertEquals(Sort.by(Order.asc("displayName")), OrganizationsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(OrganizationsFilter.getDefaultSort(), OrganizationsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		OrganizationsFilter filter = new OrganizationsFilter();
		assertNull(filter.getDisplayName());		
		assertNull(filter.getStatus());
		assertEquals("{\"displayName\":null,\"status\":null,\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter(null, null, null), filter);
		assertEquals(new OrganizationsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"displayName\":\"display\",\"status\":null,\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter("display", null, null), filter);
		assertEquals(new OrganizationsFilter("display", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals("display", filter.getDisplayName());		
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"displayName\":\"display\",\"status\":\"ACTIVE\",\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter("display", Status.ACTIVE, null), filter);
		assertEquals(new OrganizationsFilter("display", Status.ACTIVE, null).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		OrganizationsFilter filter = new OrganizationsFilter(Map.of());
		assertNull(filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"displayName\":null,\"status\":null,\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter(null, null, null), filter);
		assertEquals(new OrganizationsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display"));
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"displayName\":\"display\",\"status\":null,\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter("display", null, null), filter);
		assertEquals(new OrganizationsFilter("display", null, null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", ""));
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"displayName\":\"display\",\"status\":null,\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter("display", null, null), filter);
		assertEquals(new OrganizationsFilter("display", null, null).hashCode(), filter.hashCode());
		
		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", "active"));
		assertEquals("display", filter.getDisplayName());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"displayName\":\"display\",\"status\":\"ACTIVE\",\"group\":null}", filter.toString());
		assertEquals(new OrganizationsFilter("display", Status.ACTIVE, null), filter);
		assertEquals(new OrganizationsFilter("display", Status.ACTIVE, null).hashCode(), filter.hashCode());
		
		try {
			new OrganizationsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
			
		try {
			new OrganizationsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new OrganizationsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		OrganizationDetails organization = new OrganizationDetails(new Identifier("ABC"), Status.ACTIVE, "Road and Track", null, null, List.of("ORG"));
		/* default filter should match */
		assertTrue(new OrganizationsFilter().apply(organization));
		
		/* test display match */
		assertTrue(new OrganizationsFilter().withDisplayName("ROAD").apply(organization));
		assertTrue(new OrganizationsFilter().withDisplayName("Road and Track").apply(organization));
		assertFalse(new OrganizationsFilter().withDisplayName("bobby").apply(organization));
				
		/* test status match */
		assertTrue(new OrganizationsFilter().withStatus(Status.ACTIVE).apply(organization));
		assertFalse(new OrganizationsFilter().withStatus(Status.INACTIVE).apply(organization));
		
	}
}
