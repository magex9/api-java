package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.OptionsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public abstract class AbstractLookupServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected BasicAuthenticationService auth;
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth.logout();
	}

	@Test
	public void testLookups() {
		/* create */
		Lookup g1 = crm.createLookup(new Localized("A", "first", "premier"), null);
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findLookup(g1.getLookupId()));
		Assert.assertEquals(g1, crm.findLookupByCode("A"));
		Lookup g2 = crm.createLookup(new Localized("B", "second", "deuxieme"), null);
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findLookup(g2.getLookupId()));
		Assert.assertEquals(g2, crm.findLookupByCode("B"));
		Lookup g3 = crm.createLookup(new Localized("C", "third", "troisieme"), null);
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findLookup(g3.getLookupId()));
		Assert.assertEquals(g3, crm.findLookupByCode("C"));

		/* update */
		g1 = crm.updateLookupName(g1.getLookupId(), new Localized(g1.getCode(), "one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findLookup(g1.getLookupId()));
		g1 = crm.updateLookupName(g1.getLookupId(), g1.getName());
		g2 = crm.updateLookupName(g2.getLookupId(), new Localized(g2.getCode(), "two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findLookup(g2.getLookupId()));
		g2 = crm.updateLookupName(g2.getLookupId(), g2.getName());
		g3 = crm.updateLookupName(g3.getLookupId(), new Localized(g3.getCode(), "three", "trois"));
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findLookup(g3.getLookupId()));
		g3 = crm.updateLookupName(g3.getLookupId(), g3.getName());

		/* disable */
		g1 = crm.disableLookup(g1.getLookupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findLookup(g1.getLookupId()));
		Assert.assertEquals(g1, crm.disableLookup(g1.getLookupId()));
		g2 = crm.disableLookup(g2.getLookupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findLookup(g2.getLookupId()));
		Assert.assertEquals(g2, crm.disableLookup(g2.getLookupId()));
		g3 = crm.disableLookup(g3.getLookupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findLookup(g3.getLookupId()));
		Assert.assertEquals(g3, crm.disableLookup(g3.getLookupId()));

		/* enable */
		g1 = crm.enableLookup(g1.getLookupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findLookup(g1.getLookupId()));
		g1 = crm.enableLookup(g1.getLookupId());
		g2 = crm.enableLookup(g2.getLookupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findLookup(g2.getLookupId()));
		g2 = crm.enableLookup(g2.getLookupId());
		g3 = crm.enableLookup(g3.getLookupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findLookup(g3.getLookupId()));
		g3 = crm.enableLookup(g3.getLookupId());

		/* paging */
		Page<Lookup> page = crm.findLookups(new LookupsFilter(), new Paging(1, 100, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(15, page.getNumberOfElements());
		Assert.assertEquals(100, page.getSize());
		Assert.assertEquals(15, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(10));
		Assert.assertEquals(g2, page.getContent().get(14));
		Assert.assertEquals(g3, page.getContent().get(13));

		page = crm.findLookups(new LookupsFilter(), new Paging(1, 100, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(15, page.getNumberOfElements());
		Assert.assertEquals(100, page.getSize());
		Assert.assertEquals(15, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g1, page.getContent().get(12));
		Assert.assertEquals(g2, page.getContent().get(2));
		Assert.assertEquals(g3, page.getContent().get(11));

		page = crm.findLookups(new LookupsFilter(), new Paging(1, 100, Sort.by("name:" + Lang.ROOT)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(15, page.getNumberOfElements());
		Assert.assertEquals(100, page.getSize());
		Assert.assertEquals(15, page.getContent().size());
		/* order should be 2, 3 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g2, page.getContent().get(1));
		Assert.assertEquals(g3, page.getContent().get(6));
	}

	@Test
	public void testInvalidLookupId() {
		try {
			crm.findLookup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Lookup ID 'abc'", e.getMessage());
		}
		
		try {
			crm.findLookupByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Lookup Code 'abc'", e.getMessage());
		}

		try {
			crm.updateLookupName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Lookup ID 'abc'", e.getMessage());
		}

		try {
			crm.disableLookup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Lookup ID 'abc'", e.getMessage());
		}

		try {
			crm.enableLookup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Lookup ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testOptions() {
		/* create groups first */
		Lookup g1 = crm.createLookup(new Localized("A", "first", "premier"), null);
		Lookup g2 = crm.createLookup(new Localized("B", "second", "deuxieme"), null);

		Option r1 = crm.createOption(g1.getLookupId(), new Localized("ADM", "administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findOption(r1.getOptionId()));
		Assert.assertEquals(r1, crm.findOptionByCode(g1.getLookupId(), r1.getCode()));
		Option r2 = crm.createOption(g1.getLookupId(), new Localized("MGR", "manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findOption(r2.getOptionId()));
		Assert.assertEquals(r2, crm.findOptionByCode(g1.getLookupId(), r2.getCode()));
		Option r3 = crm.createOption(g1.getLookupId(), new Localized("USR", "user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findOption(r3.getOptionId()));
		Assert.assertEquals(r3, crm.findOptionByCode(g1.getLookupId(), r3.getCode()));
		
		/* add a couple extra roles for the other groups */
		crm.createOption(g2.getLookupId(), new Localized("OTH", "OTHER", "OTHER"));
		try {
			crm.createOption(g2.getLookupId(), new Localized("OTH", "OTHER", "OTHER"));
			fail("Cannot create duplicate roles");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another option: .*");
		}
				
		/* update */
		r1 = crm.updateOptionName(r1.getOptionId(), new Localized(r1.getCode(), "one", "un"));
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findOption(r1.getOptionId()));
		r1 = crm.updateOptionName(r1.getOptionId(), r1.getName());
		r2 = crm.updateOptionName(r2.getOptionId(), new Localized(r2.getCode(), "two", "deux"));
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findOption(r2.getOptionId()));
		r2 = crm.updateOptionName(r2.getOptionId(), r2.getName());
		r3 = crm.updateOptionName(r3.getOptionId(), new Localized(r3.getCode(), "three", "trois"));
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findOption(r3.getOptionId()));
		r3 = crm.updateOptionName(r3.getOptionId(), r3.getName());
		
		/* disable */
		r1 = crm.disableOption(r1.getOptionId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findOption(r1.getOptionId()));
		r1 = crm.disableOption(r1.getOptionId());
		r2 = crm.disableOption(r2.getOptionId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findOption(r2.getOptionId()));
		r2 = crm.disableOption(r2.getOptionId());
		r3 = crm.disableOption(r3.getOptionId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findOption(r3.getOptionId()));
		r3 = crm.disableOption(r3.getOptionId());
		
		/* enable */
		r1 = crm.enableOption(r1.getOptionId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findOption(r1.getOptionId()));
		r1 = crm.enableOption(r1.getOptionId());
		r2 = crm.enableOption(r2.getOptionId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findOption(r2.getOptionId()));
		r2 = crm.enableOption(r2.getOptionId());
		r3 = crm.enableOption(r3.getOptionId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findOption(r3.getOptionId()));
		r3 = crm.enableOption(r3.getOptionId());
		
		/* find roles for group */
		Page<Option> page = crm.findOptions(new OptionsFilter(g1.getLookupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(r1, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r2, page.getContent().get(2));
		
		page = crm.findOptions(new OptionsFilter(g1.getLookupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(r2, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r1, page.getContent().get(2));

		page = crm.findOptions(new OptionsFilter(g1.getLookupId(), null, null, null, null), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(2, page.getNumberOfElements());
		Assert.assertEquals(2, page.getSize());
		Assert.assertEquals(2, page.getContent().size());
		/* order should be 2, 3 */
		Assert.assertEquals(r2, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
	}
	
	@Test
	public void testInvalidOptionId() {
		try {
			crm.findOption(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Option ID 'abc'", e.getMessage());
		}
		
		try {
			crm.findOptionByCode(new Identifier("l1"), "abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Option Code 'abc'", e.getMessage());
		}

		try {
			crm.updateOptionName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Option ID 'abc'", e.getMessage());
		}

		try {
			crm.disableOption(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Option ID 'abc'", e.getMessage());
		}

		try {
			crm.enableOption(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Option ID 'abc'", e.getMessage());
		}
	}
		
}
