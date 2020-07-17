package ca.magex.crm.test.filters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;

public class OptionsFilterTests {

	@Test
	public void testSortOptions() {
		Field[] fields = OptionsFilter.class.getDeclaredFields();
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
			if (field.getName().contentEquals("name")) {
				continue;
			}
			/* ignore role since you can't sort by a list */
			if (field.getName().contentEquals("type")) {
				continue;
			}
			Assert.assertTrue(field.getName() + " missing from options", OptionsFilter.getSortOptions().contains(Sort.by(Order.asc(field.getName()))));
			Assert.assertTrue(OptionsFilter.getSortOptions().contains(Sort.by(Order.desc(field.getName()))));
		}
		/* default sort should be username ascending */
		Assert.assertEquals(Sort.by(Order.asc("type"), Order.asc("code")), OptionsFilter.getDefaultSort());

		/* default paging, should use default sort */
		Assert.assertEquals(OptionsFilter.getDefaultSort(), OptionsFilter.getDefaultPaging().getSort());
	}
	
	private void assertFilterEquals(OptionsFilter expected, OptionsFilter actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.toString(), actual.toString());
		Assert.assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	public void testFilterConstructs() {
		OptionsFilter filter = new OptionsFilter();
		Assert.assertNull(filter.getName());
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(null, null, null, null), filter);

		filter = filter.withName(new Localized(Lang.ROOT, "ADM"));
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized(Lang.ROOT, "ADM"), null, null, null), filter);

		filter = filter.withName(new Localized(Lang.ROOT, "ADM"));
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized(Lang.ROOT, "ADM"), null, null, null), filter);

		filter = filter.withName(new Localized(Lang.ROOT, "ADM")).withParentId(new AuthenticationGroupIdentifier("DEV"));
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), null, null), filter);

		filter = filter.withName(new Localized(Lang.ROOT, "ADM")).withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE);
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null), filter);

		filter = filter.withName(new Localized(Lang.ROOT, "ADM")).withParentId(new AuthenticationGroupIdentifier("DEV")).withType(Type.AUTHENTICATION_ROLE).withStatus(Status.ACTIVE);
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized(Lang.ROOT, "ADM"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE), filter);
	}
	
	@Test
	public void testFilterMapConstructions() {
		OptionsFilter filter = new OptionsFilter(Map.of());
		Assert.assertNull(filter.getName());
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(null, null, null, null), filter);

		filter = new OptionsFilter(Map.of("code", "ADM", "type", "", "status", ""));
		Assert.assertEquals("ADM", filter.getName().get(Lang.ROOT));
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("ADM", "", ""), null, null, null), filter);
		
		filter = new OptionsFilter(Map.of("english", "Admin"));
		Assert.assertEquals("Admin", filter.getName().get(Lang.ENGLISH));
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("", "Admin", ""), null, null, null), filter);
		
		filter = new OptionsFilter(Map.of("french", "Admin"));
		Assert.assertEquals("Admin", filter.getName().get(Lang.FRENCH));
		Assert.assertNull(filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("", "", "Admin"), null, null, null), filter);

		filter = new OptionsFilter(Map.of("french", "Admin", "parentId", new AuthenticationGroupIdentifier("DEV").toString()));		
		Assert.assertEquals("Admin", filter.getName().get(Lang.FRENCH));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertNull(filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("", "", "Admin"), new AuthenticationGroupIdentifier("DEV"), null, null), filter);

		filter = new OptionsFilter(Map.of("french", "Admin", "parentId", new AuthenticationGroupIdentifier("DEV").toString(), "type", Type.AUTHENTICATION_ROLE.getCode()));
		Assert.assertEquals("Admin", filter.getName().get(Lang.FRENCH));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		Assert.assertNull(filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("", "", "Admin"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, null), filter);

		filter = new OptionsFilter(Map.of("french", "Admin", "parentId", new AuthenticationGroupIdentifier("DEV").toString(), "type", Type.AUTHENTICATION_ROLE.getCode(), "status", Status.ACTIVE.name()));
		Assert.assertEquals("Admin", filter.getName().get(Lang.FRENCH));
		Assert.assertEquals(new AuthenticationGroupIdentifier("DEV"), filter.getParentId());
		Assert.assertEquals(Type.AUTHENTICATION_ROLE, filter.getType());
		Assert.assertEquals(Status.ACTIVE, filter.getStatus());
		assertFilterEquals(new OptionsFilter(new Localized("", "", "Admin"), new AuthenticationGroupIdentifier("DEV"), Type.AUTHENTICATION_ROLE, Status.ACTIVE), filter);
		
		try {
			new OptionsFilter(Map.of("type", "Canada"));
			Assert.fail("should have failed on invalid type");
		}
		catch(ApiException api) {
			Assert.assertEquals("Invalid type value 'Canada' expected one of {STATUS,LOCALE,DICTIONARY,PHRASE,MESSAGE_TYPE,AUTHENTICATION_GROUP,AUTHENTICATION_ROLE,SALUTATION,LANGUAGE,COUNTRY,PROVINCE,BUSINESS_GROUP,BUSINESS_ROLE}", api.getMessage());
		}
		
		try {
			new OptionsFilter(Map.of("status", "Canada"));
			Assert.fail("should have failed on invalid status");
		}
		catch(ApiException api) {
			Assert.assertEquals("Invalid status value 'Canada' expected one of {ACTIVE,INACTIVE,PENDING}", api.getMessage());
		}
		
		try {
			new OptionsFilter(Map.of("status", Status.ACTIVE));
			Assert.fail("should have failed on invalid status");
		}
		catch(ApiException api) {
			Assert.assertEquals("Unable to instantiate roles filter", api.getMessage());
		}
	}

	@Test
	public void testApplyFilter() {
		Option option = new Option(new ProvinceIdentifier("ON"), new CountryIdentifier("CA"), Type.PROVINCE, Status.ACTIVE, Option.MUTABLE, new Localized("ON", "Ontario", "L'Ontario")); 
		/* default filter should match */
		Assert.assertTrue(new OptionsFilter().apply(option));
		
		/* test parent match */
		Assert.assertTrue(new OptionsFilter().withParentId(new CountryIdentifier("CA")).apply(option));
		Assert.assertFalse(new OptionsFilter().withParentId(new CountryIdentifier("US")).apply(option));
		
		/* test type match */
		Assert.assertTrue(new OptionsFilter().withType(Type.PROVINCE).apply(option));
		Assert.assertFalse(new OptionsFilter().withType(Type.LANGUAGE).apply(option));
		
		/* test code match */
		Assert.assertTrue(new OptionsFilter().withName(new Localized(Lang.ROOT, "ON")).apply(option));
		Assert.assertFalse(new OptionsFilter().withName(new Localized(Lang.ROOT, "QC")).apply(option));
		
		/* test english name */
		Assert.assertTrue(new OptionsFilter().withName(new Localized(Lang.ENGLISH, "Ontario")).apply(option));
		Assert.assertFalse(new OptionsFilter().withName(new Localized(Lang.ENGLISH, "Quebec")).apply(option));
		
		/* test french name */
		Assert.assertTrue(new OptionsFilter().withName(new Localized(Lang.FRENCH, "L'Ontario")).apply(option));
		Assert.assertFalse(new OptionsFilter().withName(new Localized(Lang.FRENCH, "Quebec")).apply(option));
		
		/* test status match */
		Assert.assertTrue(new OptionsFilter().withStatus(Status.ACTIVE).apply(option));
		Assert.assertFalse(new OptionsFilter().withStatus(Status.INACTIVE).apply(option));
		
		/* test nested code */
		option = new Option(new ProvinceIdentifier("CA/ON"), new CountryIdentifier("CA"), Type.PROVINCE, Status.ACTIVE, Option.MUTABLE, new Localized("CA/ON", "Ontario", "L'Ontario"));
		Assert.assertTrue(new OptionsFilter().withName(new Localized(Lang.ROOT, "ON")).apply(option));
		Assert.assertFalse(new OptionsFilter().withName(new Localized(Lang.ROOT, "QC")).apply(option));
	}
}
