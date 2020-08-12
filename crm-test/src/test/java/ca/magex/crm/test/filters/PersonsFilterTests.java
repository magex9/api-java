package ca.magex.crm.test.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

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
			Assert.assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assert.assertTrue(PersonsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be display name ascending */
		Assert.assertEquals(Sort.by(Order.asc("displayName")), PersonsFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assert.assertEquals(PersonsFilter.getDefaultSort(), PersonsFilter.getDefaultPaging().getSort());
	}

	private void assertFilterEquals(PersonsFilter expected, PersonsFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	public void testFilterConstructs() {
		PersonsFilter filter = new PersonsFilter();
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		assertFilterEquals(new PersonsFilter(null, null, null), filter);

		filter = filter.withOrganizationId(new OrganizationIdentifier("G1"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), null, null), filter);

		filter = filter.withDisplayName("display");
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), "display", null), filter);

		filter = filter.withStatus(Status.ACTIVE);
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), "display", Status.ACTIVE), filter);
	}

	@Test
	public void testFilterMapConstructions() {
		PersonsFilter filter = new PersonsFilter(Map.of());
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(null, null, null), filter);

		filter = new PersonsFilter(Map.of("organizationId", "G1"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), null, null), filter);

		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), "display", null), filter);		

		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", ""));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), "display", null), filter);

		filter = new PersonsFilter(Map.of("organizationId", "G1", "displayName", "display", "reference", "ref", "status", "active"));
		Assert.assertEquals(new OrganizationIdentifier("G1"), filter.getOrganizationId());
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new PersonsFilter(new OrganizationIdentifier("G1"), "display", Status.ACTIVE), filter);

		try {
			new PersonsFilter(Map.of("organizationId", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new PersonsFilter(Map.of("displayName", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new PersonsFilter(Map.of("status", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate persons filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new PersonsFilter(Map.of("status", "dormant"));
		} catch (ApiException apie) {
			Assert.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}
	}

	@Test
	public void testApplyFilter() {
		PersonSummary person = new PersonSummary(new PersonIdentifier("ABC"), new OrganizationIdentifier("G1"), Status.ACTIVE, "Bobby Thomson", null);
		/* default filter should match */
		Assert.assertTrue(new PersonsFilter().apply(person));

		/* test organization match */
		Assert.assertTrue(new PersonsFilter().withOrganizationId(new OrganizationIdentifier("G1")).apply(person));
		Assert.assertFalse(new PersonsFilter().withOrganizationId(new OrganizationIdentifier("G2")).apply(person));

		/* test display name match */
		Assert.assertTrue(new PersonsFilter().withDisplayName("BOBBY").apply(person));
		Assert.assertTrue(new PersonsFilter().withDisplayName("Bobby Thomson").apply(person));
		Assert.assertFalse(new PersonsFilter().withDisplayName("Robby").apply(person));

		/* test status match */
		Assert.assertTrue(new PersonsFilter().withStatus(Status.ACTIVE).apply(person));
		Assert.assertFalse(new PersonsFilter().withStatus(Status.INACTIVE).apply(person));

	}
}
