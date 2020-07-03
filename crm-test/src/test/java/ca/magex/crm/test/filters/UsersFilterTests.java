package ca.magex.crm.test.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class UsersFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = UsersFilter.class.getDeclaredFields();
		for (Field field : fields) {
			/* ignore static fields */
			if ((field.getModifiers() & Modifier.STATIC) > 0) {
				continue;
			}
			/* ignore Identifiers fields */
			if (Identifier.class.isAssignableFrom(field.getType())) {
				continue;
			}
			/* ignore role since you can't sort by a list */
			if (field.getName().contentEquals("role")) {
				continue;
			}
			Assert.assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assert.assertTrue(UsersFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be username ascending */
		Assert.assertEquals(Sort.by(Order.asc("username")), UsersFilter.getDefaultSort());
		
		/* default paging, should use default sort */
		Assert.assertEquals(UsersFilter.getDefaultSort(), UsersFilter.getDefaultPaging().getSort());
	}
	
	private void assertFilterEquals(UsersFilter expected, UsersFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}
	
	@Test
	public void testFilterConstructs() {
		UsersFilter filter = new UsersFilter();
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());		
		assertFilterEquals(new UsersFilter(null, null, null, null, null), filter);		
				
		filter = filter.withOrganizationId(new OrganizationIdentifier("O1"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertNull(filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), null, null, null, null), filter);
		
		filter = filter.withPersonId(new PersonIdentifier("P1"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, null, null), filter);
		
		filter = filter.withUsername("Admin");
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, "Admin", null), filter);		
		
		filter = filter.withRoleId(new AuthenticationRoleIdentifier("USR"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);		
				
		filter = filter.withStatus(Status.ACTIVE);
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());		
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")), filter);		
	}
	
	@Test
	public void testFilterMapConstructions() {
		UsersFilter filter = new UsersFilter(Map.of());
		Assert.assertNull(filter.getOrganizationId());
		Assert.assertNull(filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());		
		assertFilterEquals(new UsersFilter(null, null, null, null, null), filter);		
		
		filter = new UsersFilter(Map.of("organizationId", "O1"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertNull(filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), null, null, null, null), filter);
		
		filter = new UsersFilter(Map.of("organizationId", "O1", "personId", "P1"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertNull(filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, null, null), filter);		
		
		filter = new UsersFilter(Map.of("organizationId", "O1", "personId", "P1", "username", "Admin"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertNull(filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, "Admin", null), filter);
				
		filter = new UsersFilter(Map.of("organizationId", "O1", "personId", "P1", "username", "Admin", "roleId", "USR"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);		
		
		filter = new UsersFilter(Map.of("organizationId", "O1", "personId", "P1", "username", "Admin", "roleId", "USR", "status", ""));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), null, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
				
		filter = new UsersFilter(Map.of("organizationId", "O1", "personId", "P1", "username", "Admin", "roleId", "USR", "status", "active"));
		Assert.assertEquals(new OrganizationIdentifier("O1"), filter.getOrganizationId());
		Assert.assertEquals(new PersonIdentifier("P1"), filter.getPersonId());
		Assert.assertEquals("Admin", filter.getUsername());
		Assert.assertEquals(new AuthenticationRoleIdentifier("USR"), filter.getRoleId());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new UsersFilter(new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), Status.ACTIVE, "Admin", new AuthenticationRoleIdentifier("USR")), filter);
		
		try {
			new UsersFilter(Map.of("personId", 1));
		}
		catch(ApiException apie) {
			Assert.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("username", 1));
		}
		catch(ApiException apie) {
			Assert.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("role", 1));
		}
		catch(ApiException apie) {
			Assert.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}	
		
		try {
			new UsersFilter(Map.of("status", 1));
		}
		catch(ApiException apie) {
			Assert.assertEquals("Unable to instantiate users filter", apie.getMessage());
			Assert.assertTrue(apie.getCause() instanceof ClassCastException);
		}
		
		try {
			new UsersFilter(Map.of("status", "dormant"));
		}
		catch(ApiException apie) {
			Assert.assertEquals("Invalid status value 'dormant' expected one of {ACTIVE,INACTIVE,PENDING}", apie.getMessage());
		}		
	}
	
	@Test
	public void testApplyFilter() {
		User user = new User(new UserIdentifier("DEF"), new OrganizationIdentifier("O1"), new PersonIdentifier("P1"), "Admin", Status.ACTIVE, List.of(new AuthenticationRoleIdentifier("USR"), new AuthenticationRoleIdentifier("ADM")));
		/* default filter should match */
		Assert.assertTrue(new UsersFilter().apply(user));
		
		/* test organization match */
		Assert.assertTrue(new UsersFilter().withPersonId(new PersonIdentifier("P1")).apply(user));
		Assert.assertFalse(new UsersFilter().withPersonId(new PersonIdentifier("P2")).apply(user));
		
		/* test person match */
		Assert.assertTrue(new UsersFilter().withOrganizationId(new OrganizationIdentifier("O1")).apply(user));
		Assert.assertFalse(new UsersFilter().withOrganizationId(new OrganizationIdentifier("O2")).apply(user));
		
		/* test username match */
		Assert.assertTrue(new UsersFilter().withUsername("AD").apply(user));
		Assert.assertTrue(new UsersFilter().withUsername("Admin").apply(user));
		Assert.assertFalse(new UsersFilter().withUsername("User").apply(user));
		
		/* test role match */
		Assert.assertTrue(new UsersFilter().withRoleId(new AuthenticationRoleIdentifier("ADM")).apply(user));
		Assert.assertFalse(new UsersFilter().withRoleId(new AuthenticationRoleIdentifier("BDG")).apply(user));
		
		/* test status match */
		Assert.assertTrue(new UsersFilter().withStatus(Status.ACTIVE).apply(user));
		Assert.assertFalse(new UsersFilter().withStatus(Status.INACTIVE).apply(user));
		
	}
}
