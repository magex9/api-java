package ca.magex.crm.test.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class OrganizationsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = OrganizationsFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			if (field.getName().equals("groupId")) {
				continue;
			}
			Assert.assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assert.assertTrue(OrganizationsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be displayName ascending */
		Assert.assertEquals(Sort.by(Order.asc("displayName")), OrganizationsFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assert.assertEquals(OrganizationsFilter.getDefaultSort(), OrganizationsFilter.getDefaultPaging().getSort());
	}

	private void assertFilterEquals(OrganizationsFilter expected, OrganizationsFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	public void testFilterConstructs() {
		OrganizationsFilter filter = new OrganizationsFilter();
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OrganizationsFilter(null, null, null), filter);

		filter = filter.withDisplayName("display");
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OrganizationsFilter("display", null, null), filter);

		filter = filter.withStatus(Status.ACTIVE);
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new OrganizationsFilter("display", Status.ACTIVE, null), filter);
	}

	@Test
	public void testFilterMapConstructions() {
		OrganizationsFilter filter = new OrganizationsFilter(Map.of());
		Assert.assertNull(filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OrganizationsFilter(null, null, null), filter);

		filter = new OrganizationsFilter(Map.of("displayName", "display"));
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OrganizationsFilter("display", null, null), filter);

		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", ""));
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OrganizationsFilter("display", null, null), filter);

		filter = new OrganizationsFilter(Map.of("displayName", "display", "status", "active"));
		Assert.assertEquals("display", filter.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new OrganizationsFilter("display", Status.ACTIVE, null), filter);

		try {
			new OrganizationsFilter(Map.of("displayName", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new OrganizationsFilter(Map.of("status", 1));
		} catch (ApiException apie) {
			Assert.assertEquals("Unable to instantiate organizations filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}

		try {
			new OrganizationsFilter(Map.of("status", "dormant"));
		} catch (ApiException apie) {
			Assert.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}
	}

	@Test
	public void testApplyFilter() {
		OrganizationDetails organization = new OrganizationDetails(new OrganizationIdentifier("ABC"), Status.ACTIVE, "Road and Track", null, null, List.of(new AuthenticationGroupIdentifier("ORG")));
		/* default filter should match */
		Assert.assertTrue(new OrganizationsFilter().apply(organization));

		/* test display match */
		Assert.assertTrue(new OrganizationsFilter().withDisplayName("ROAD").apply(organization));
		Assert.assertTrue(new OrganizationsFilter().withDisplayName("Road and Track").apply(organization));
		Assert.assertFalse(new OrganizationsFilter().withDisplayName("bobby").apply(organization));

		/* test status match */
		Assert.assertTrue(new OrganizationsFilter().withStatus(Status.ACTIVE).apply(organization));
		Assert.assertFalse(new OrganizationsFilter().withStatus(Status.INACTIVE).apply(organization));

	}
}
