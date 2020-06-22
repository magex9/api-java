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

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class PersonsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = PersonsFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			/* ignore Identifiers fields */
			if (Identifier.class.isAssignableFrom(field.getType())) {
				continue;
			}
			assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be display name ascending */
		assertEquals(Sort.by(Order.asc("displayName")), PersonsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		assertEquals(PersonsFilter.getDefaultSort(), PersonsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		PersonsFilter filter = new PersonsFilter();
		assertNull(filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertEquals("{\"organizationId\":null,\"displayName\":null,\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(null, null, null), filter);
		assertEquals(new PersonsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getStatus());		
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), null, null), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());
				
		filter = filter.withStatus(Status.ACTIVE);
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		PersonsFilter filter = new PersonsFilter(Map.of());
		assertNull(filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":null,\"displayName\":null,\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(null, null, null), filter);
		assertEquals(new PersonsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = new PersonsFilter(Map.of("organizationId", "G1"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertNull(filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), null, null), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), null, null).hashCode(), filter.hashCode());
				
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());		
						
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertNull(filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());
		
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		assertEquals(new Identifier("G1"), filter.getOrganizationId());
		assertEquals("display", filter.getDisplayName());
		assertEquals(Status.ACTIVE, filter.getStatus());
		assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE), filter);
		assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new PersonsFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate persons filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new PersonsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate persons filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new PersonsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			assertEquals("Unable to instantiate persons filter", apie.getMessage());
			assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new PersonsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		PersonSummary person = new PersonSummary(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, "Bobby Thomson");
		/* default filter should match */
		assertTrue(new PersonsFilter().apply(person));
		
		/* test organization match */
		assertTrue(new PersonsFilter().withOrganizationId(new Identifier("G1")).apply(person));
		assertFalse(new PersonsFilter().withOrganizationId(new Identifier("G2")).apply(person));
		
		/* test display name match */
		assertTrue(new PersonsFilter().withDisplayName("BOBBY").apply(person));
		assertTrue(new PersonsFilter().withDisplayName("Bobby Thomson").apply(person));
		assertFalse(new PersonsFilter().withDisplayName("Robby").apply(person));
		
		/* test status match */
		assertTrue(new PersonsFilter().withStatus(Status.ACTIVE).apply(person));
		assertFalse(new PersonsFilter().withStatus(Status.INACTIVE).apply(person));
		
	}
}
