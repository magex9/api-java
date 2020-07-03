package ca.magex.crm.test.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

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
			Assert.assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assert.assertTrue(LocationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be display name ascending */
		Assert.assertEquals(Sort.by(Order.asc("displayName")), LocationsFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assert.assertEquals(LocationsFilter.getDefaultSort(), LocationsFilter.getDefaultPaging().getSort());
	}

	private void assertFilterEquals(LocationsFilter expected, LocationsFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	public void testFilterConstructs() {
		LocationsFilter filter = new LocationsFilter();
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(null, null, null, null), filter);

		filter = filter.withOrganizationId(new OrganizationIdentifier("G1"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), null, null, null), filter);

		filter = filter.withDisplayName("display");
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", null, null), filter);

		filter = filter.withReference("ref");
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals("ref", filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", "ref", null), filter);

		filter = filter.withStatus(Status.ACTIVE);
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals("ref", filter.getReference());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", "ref", Status.ACTIVE), filter);
	}

	@Test
	public void testFilterMapConstructions() {
		LocationsFilter filter = new LocationsFilter(Map.of());
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(null, null, null, null), filter);

		filter = new LocationsFilter(Map.of("organizationId", "G1"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), null, null, null), filter);

		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", null, null), filter);

		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals("ref", filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", "ref", null), filter);

		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals("ref", filter.getReference());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", "ref", null), filter);

		filter = new LocationsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals("ref", filter.getReference());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new LocationsFilter(new OrganizationIdentifier("G1"), "display", "ref", Status.ACTIVE), filter);

		try {
			new LocationsFilter(Map.of("organizationId", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new LocationsFilter(Map.of("displayName", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new LocationsFilter(Map.of("reference", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new LocationsFilter(Map.of("status", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate locations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new LocationsFilter(Map.of("status", "dormant"));
		} catch (ApiException apie) {
			Assert.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}
	}

	@Test
	public void testApplyFilter() {
		LocationSummary location = new LocationSummary(new LocationIdentifier("ABC"), new OrganizationIdentifier("G1"), Status.ACTIVE, "RT", "Road and Track");
		/* default filter should match */
		Assert.assertTrue(new LocationsFilter().apply(location));

		/* test organization match */
		Assert.assertTrue(new LocationsFilter().withOrganizationId(new OrganizationIdentifier("G1")).apply(location));
		Assert.assertFalse(new LocationsFilter().withOrganizationId(new OrganizationIdentifier("G2")).apply(location));

		/* test display name match */
		Assert.assertTrue(new LocationsFilter().withDisplayName("ROAD").apply(location));
		Assert.assertTrue(new LocationsFilter().withDisplayName("Road and Track").apply(location));
		Assert.assertFalse(new LocationsFilter().withDisplayName("bobby").apply(location));

		/* test reference match */
		Assert.assertTrue(new LocationsFilter().withReference("R").apply(location));
		Assert.assertTrue(new LocationsFilter().withReference("rt").apply(location));
		Assert.assertFalse(new LocationsFilter().withReference("bobby").apply(location));

		/* test status match */
		Assert.assertTrue(new LocationsFilter().withStatus(Status.ACTIVE).apply(location));
		Assert.assertFalse(new LocationsFilter().withStatus(Status.INACTIVE).apply(location));

	}
}
