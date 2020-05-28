package ca.magex.crm.api.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
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
			Assertions.assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))), field.getName());
			Assertions.assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))), field.getName());
		}
		/* default sort should be display name ascending */
		Assertions.assertEquals(Sort.by(Order.asc("displayName")), PersonsFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assertions.assertEquals(PersonsFilter.getDefaultSort(), PersonsFilter.getDefaultPaging().getSort());
	}
	
	@Test
	public void testFilterConstructs() {
		PersonsFilter filter = new PersonsFilter();
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertEquals("{\"organizationId\":null,\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(null, null, null), filter);
		Assertions.assertEquals(new PersonsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = filter.withOrganizationId(new Identifier("G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());		
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), null, null), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), null, null).hashCode(), filter.hashCode());
		
		filter = filter.withDisplayName("display");
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());
				
		filter = filter.withStatus(Status.ACTIVE);
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE).hashCode(), filter.hashCode());
	}
	
	@Test
	public void testFilterMapConstructions() {
		PersonsFilter filter = new PersonsFilter(Map.of());
		Assertions.assertNull(filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":null,\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(null, null, null), filter);
		Assertions.assertEquals(new PersonsFilter(null, null, null).hashCode(), filter.hashCode());
		
		filter = new PersonsFilter(Map.of("organizationId", "G1"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertNull(filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":null,\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), null, null), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), null, null).hashCode(), filter.hashCode());
				
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());		
						
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertNull(filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":null}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", null).hashCode(), filter.hashCode());
		
		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		Assertions.assertEquals(new Identifier("G1"), filter.getOrganizationId());
		Assertions.assertEquals("display", filter.getDisplayName());
		Assertions.assertEquals(Status.ACTIVE, filter.getStatus());
		Assertions.assertEquals("{\"organizationId\":\"G1\",\"displayName\":\"display\",\"status\":\"ACTIVE\"}", filter.toString());
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE), filter);
		Assertions.assertEquals(new PersonsFilter(new Identifier("G1"), "display", Status.ACTIVE).hashCode(), filter.hashCode());
		
		try {
			new PersonsFilter(Map.of("organizationId", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new PersonsFilter(Map.of("displayName", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new PersonsFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assertions.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new PersonsFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assertions.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		PersonSummary person = new PersonSummary(new Identifier("ABC"), new Identifier("G1"), Status.ACTIVE, "Bobby Thomson");
		/* default filter should match */
		Assertions.assertTrue(new PersonsFilter().apply(person));
		
		/* test organization match */
		Assertions.assertTrue(new PersonsFilter().withOrganizationId(new Identifier("G1")).apply(person));
		Assertions.assertFalse(new PersonsFilter().withOrganizationId(new Identifier("G2")).apply(person));
		
		/* test display name match */
		Assertions.assertTrue(new PersonsFilter().withDisplayName("BOBBY").apply(person));
		Assertions.assertTrue(new PersonsFilter().withDisplayName("Bobby Thomson").apply(person));
		Assertions.assertFalse(new PersonsFilter().withDisplayName("Robby").apply(person));
		
		/* test status match */
		Assertions.assertTrue(new PersonsFilter().withStatus(Status.ACTIVE).apply(person));
		Assertions.assertFalse(new PersonsFilter().withStatus(Status.INACTIVE).apply(person));
		
	}
}
