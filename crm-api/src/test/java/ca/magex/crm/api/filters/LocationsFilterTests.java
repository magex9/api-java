package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@TestInstance(Lifecycle.PER_METHOD)
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
			Assertions.assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))), field.getName());
			Assertions.assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))), field.getName());
		}
		/* default sort should be display name ascending */
		Assertions.assertEquals(Sort.by(Order.asc("displayName")), LocationsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(LocationsFilter.getDefaultSort(), LocationsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		LocationsFilter filter = new LocationsFilter();
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":null,\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(null, null, null, null), filter);
		Assertions.assertEquals(new LocationsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());		
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null).hashCode(), filter.hashCode());
		
		filter = filter.withReference("ref");
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals("ref", filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());		
		
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals("ref", filter.getReference());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		LocationsFilter filter = new LocationsFilter(Map.of());
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":null,\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(null, null, null, null), filter);
		Assertions.assertEquals(new LocationsFilter(null, null, null, null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), null, null, null).hashCode(), filter.hashCode());
		
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", null, null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals("ref", filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());
						
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals("ref", filter.getReference());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", null).hashCode(), filter.hashCode());
		
		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals("ref", filter.getReference());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"reference\":\"ref\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE), filter);
		Assertions.assertEquals(new LocationsFilter(new Identifier("G1"), "display", "ref", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new LocationsFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("reference", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}		
		
		try {
			new LocationsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new LocationsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		LocationSummary location = new LocationSummary(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, "RT", "Road and Track");
		/* default filter should match */
		Assertions.assertTrue(new LocationsFilter().apply(location));
		
		/* test organization match */
		Assertions.assertTrue(new LocationsFilter().withOrganizationId(new Identifier("G1")).apply(location));
		Assertions.assertFalse(new LocationsFilter().withOrganizationId(new Identifier("G2")).apply(location));
		
		/* test display name match */
		Assertions.assertTrue(new LocationsFilter().withDisplayName("ROAD").apply(location));
		Assertions.assertTrue(new LocationsFilter().withDisplayName("Road and Track").apply(location));
		Assertions.assertFalse(new LocationsFilter().withDisplayName("bobby").apply(location));
		
		/* test reference match */
		Assertions.assertTrue(new LocationsFilter().withReference("R").apply(location));
		Assertions.assertTrue(new LocationsFilter().withReference("rt").apply(location));
		Assertions.assertFalse(new LocationsFilter().withReference("bobby").apply(location));
		
		/* test status match */
		Assertions.assertTrue(new LocationsFilter().withStatus(Status.ACTIVE).apply(location));
		Assertions.assertFalse(new LocationsFilter().withStatus(Status.INACTIVE).apply(location));
		
	}
}
