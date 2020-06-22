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

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class LocationsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = LocationsFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			/* ignore Identifiers fields */
			if (Identifier.class.isAssignableFrom(field.getType())) {
				continue;
			}
			assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be display name ascending */
		assertEquals(Sort.by(Order.asc("displayName")), LocationsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(LocationsFilter.getDefaultSort(), LocationsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		LocationsFilter filter = new LocationsFilter();
		assertNull(filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":null,\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(null, null, null, null), filter);
		assertEquals(new LocationsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());		
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withReference("ref");
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals("ref", filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());		
		
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals("ref", filter.getReference());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		LocationsFilter filter = new LocationsFilter(Map.of());
		assertNull(filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":null,\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(null, null, null, null), filter);
		assertEquals(new LocationsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null).hashCode(), filter.hashCode());
		
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":null,\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals("ref", filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());
						
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals("ref", filter.getReference());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals("ref", filter.getReference());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE), filter);
		assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new LocationsFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate locations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate locations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("reference", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate locations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}		
		
		try {
			new LocationsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate locations filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		LocationSummary location = new LocationSummary(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, "RT", "Road and Track");
		/* default filter should match */
		assertTrue(new LocationsFilter().apply(location));
		
		/* test organization match */
		assertTrue(new LocationsFilter().withOrganizationId(new Identifier("G1")).apply(location));
		assertFalse(new LocationsFilter().withOrganizationId(new Identifier("G2")).apply(location));
		
		/* test display name match */
		assertTrue(new LocationsFilter().withDisplayName("ROAD").apply(location));
		assertTrue(new LocationsFilter().withDisplayName("Road and Track").apply(location));
		assertFalse(new LocationsFilter().withDisplayName("bobby").apply(location));
		
		/* test reference match */
		assertTrue(new LocationsFilter().withReference("R").apply(location));
		assertTrue(new LocationsFilter().withReference("rt").apply(location));
		assertFalse(new LocationsFilter().withReference("bobby").apply(location));
		
		/* test status match */
		assertTrue(new LocationsFilter().withStatus(Status.ACTIVE).apply(location));
		assertFalse(new LocationsFilter().withStatus(Status.INACTIVE).apply(location));
		
	}
}
