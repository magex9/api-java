package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ADMIN;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.assertEquals;
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
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractGroupServiceTests {

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
	public void testGroups() {
		/* create */
		Group g1 = crm.createGroup(new Localized("A", "first", "premier"));
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findGroup(g1.getGroupId()));
		Assert.assertEquals(g1, crm.findGroupByCode("A"));
		Group g2 = crm.createGroup(new Localized("B", "second", "deuxieme"));
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findGroup(g2.getGroupId()));
		Assert.assertEquals(g2, crm.findGroupByCode("B"));
		Group g3 = crm.createGroup(new Localized("C", "third", "troisieme"));
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findGroup(g3.getGroupId()));
		Assert.assertEquals(g3, crm.findGroupByCode("C"));

		/* update */
		g1 = crm.updateGroupName(g1.getGroupId(), new Localized(g1.getCode(), "one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findGroup(g1.getGroupId()));
		g1 = crm.updateGroupName(g1.getGroupId(), g1.getName());
		g2 = crm.updateGroupName(g2.getGroupId(), new Localized(g2.getCode(), "two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findGroup(g2.getGroupId()));
		g2 = crm.updateGroupName(g2.getGroupId(), g2.getName());
		g3 = crm.updateGroupName(g3.getGroupId(), new Localized(g3.getCode(), "three", "trois"));
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findGroup(g3.getGroupId()));
		g3 = crm.updateGroupName(g3.getGroupId(), g3.getName());

		/* disable */
		g1 = crm.disableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findGroup(g1.getGroupId()));
		Assert.assertEquals(g1, crm.disableGroup(g1.getGroupId()));
		g2 = crm.disableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findGroup(g2.getGroupId()));
		Assert.assertEquals(g2, crm.disableGroup(g2.getGroupId()));
		g3 = crm.disableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findGroup(g3.getGroupId()));
		Assert.assertEquals(g3, crm.disableGroup(g3.getGroupId()));

		/* enable */
		g1 = crm.enableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, crm.findGroup(g1.getGroupId()));
		g1 = crm.enableGroup(g1.getGroupId());
		g2 = crm.enableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, crm.findGroup(g2.getGroupId()));
		g2 = crm.enableGroup(g2.getGroupId());
		g3 = crm.enableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, crm.findGroup(g3.getGroupId()));
		g3 = crm.enableGroup(g3.getGroupId());

		/* paging */
		Page<Group> page = crm.findGroups(new GroupsFilter(), new Paging(1, 10, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(7, page.getNumberOfElements());
		Assert.assertEquals(10, page.getSize());
		Assert.assertEquals(7, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(2));
		Assert.assertEquals(g2, page.getContent().get(6));
		Assert.assertEquals(g3, page.getContent().get(5));

		page = crm.findGroups(new GroupsFilter(), new Paging(1, 10, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(7, page.getNumberOfElements());
		Assert.assertEquals(10, page.getSize());
		Assert.assertEquals(7, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g1, page.getContent().get(6));
		Assert.assertEquals(g2, page.getContent().get(1));
		Assert.assertEquals(g3, page.getContent().get(5));

		page = crm.findGroups(new GroupsFilter(), new Paging(1, 10, Sort.by("name:" + Lang.ROOT)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(7, page.getNumberOfElements());
		Assert.assertEquals(10, page.getSize());
		Assert.assertEquals(7, page.getContent().size());
		/* order should be 2, 3 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g2, page.getContent().get(2));
		Assert.assertEquals(g3, page.getContent().get(3));
	}

	@Test
	public void testInvalidGroupId() {
		try {
			crm.findGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
		
		try {
			crm.findGroupByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group Code 'abc'", e.getMessage());
		}

		try {
			crm.updateGroupName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			crm.disableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			crm.enableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testRoles() {
		/* create groups first */
		Group g1 = crm.createGroup(new Localized("A", "first", "premier"));
		Group g2 = crm.createGroup(new Localized("B", "second", "deuxieme"));
		Group g3 = crm.createGroup(new Localized("C", "third", "troisieme"));

		Role r1 = crm.createRole(g1.getGroupId(), new Localized("ADM", "administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findRole(r1.getRoleId()));
		Assert.assertEquals(r1, crm.findRoleByCode(r1.getCode()));
		Role r2 = crm.createRole(g1.getGroupId(), new Localized("MGR", "manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findRole(r2.getRoleId()));
		Assert.assertEquals(r2, crm.findRoleByCode(r2.getCode()));
		Role r3 = crm.createRole(g1.getGroupId(), new Localized("USR", "user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findRole(r3.getRoleId()));
		Assert.assertEquals(r3, crm.findRoleByCode(r3.getCode()));
		
		/* add a couple extra roles for the other groups */
		crm.createRole(g2.getGroupId(), new Localized("OTH", "OTHER", "OTHER"));
		try {
			crm.createRole(g3.getGroupId(), new Localized("OTH", "OTHER", "OTHER"));
			fail("Cannot create duplicate roles");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
				
		/* update */
		r1 = crm.updateRoleName(r1.getRoleId(), new Localized(r1.getCode(), "one", "un"));
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findRole(r1.getRoleId()));
		r1 = crm.updateRoleName(r1.getRoleId(), r1.getName());
		r2 = crm.updateRoleName(r2.getRoleId(), new Localized(r2.getCode(), "two", "deux"));
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findRole(r2.getRoleId()));
		r2 = crm.updateRoleName(r2.getRoleId(), r2.getName());
		r3 = crm.updateRoleName(r3.getRoleId(), new Localized(r3.getCode(), "three", "trois"));
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findRole(r3.getRoleId()));
		r3 = crm.updateRoleName(r3.getRoleId(), r3.getName());
		
		/* disable */
		r1 = crm.disableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findRole(r1.getRoleId()));
		r1 = crm.disableRole(r1.getRoleId());
		r2 = crm.disableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findRole(r2.getRoleId()));
		r2 = crm.disableRole(r2.getRoleId());
		r3 = crm.disableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findRole(r3.getRoleId()));
		r3 = crm.disableRole(r3.getRoleId());
		
		/* enable */
		r1 = crm.enableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, crm.findRole(r1.getRoleId()));
		r1 = crm.enableRole(r1.getRoleId());
		r2 = crm.enableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, crm.findRole(r2.getRoleId()));
		r2 = crm.enableRole(r2.getRoleId());
		r3 = crm.enableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, crm.findRole(r3.getRoleId()));
		r3 = crm.enableRole(r3.getRoleId());
		
		/* find roles for group */
		Page<Role> page = crm.findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(r1, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r2, page.getContent().get(2));
		
		page = crm.findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(r2, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r1, page.getContent().get(2));

		page = crm.findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
	public void testInvalidRoleId() {
		try {
			crm.findRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
		
		try {
			crm.findRoleByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role Code 'abc'", e.getMessage());
		}

		try {
			crm.updateRoleName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			crm.disableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			crm.enableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
	}
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		Identifier roleId = crm.createRole(groupId, ADMIN).getRoleId();
		assertEquals("Group", crm.findGroupByCode("GRP").getName(Lang.ENGLISH));
		try {
			crm.findGroup(roleId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }

		assertEquals("Admin", crm.findRoleByCode("ADM").getName(Lang.ENGLISH));
		try {
			crm.findRole(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}
	
	@Test
	public void testGroupWithInvalidCodes() throws Exception {
		
		crm.createGroup(new Localized("A", "English", "French"));
		try {
			crm.createGroup(new Localized(null, "English", "French"));
			fail("Invalid group code");
		} catch (IllegalArgumentException expected) { }
		try {
			crm.createGroup(new Localized("", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must not be blank");
		}
		try {
			crm.createGroup(new Localized("b", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must match: .*");
		}
		try {
			crm.createGroup(new Localized("$", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must match: .*");
		}
	}	
	
	@Test
	public void testCreatingGroupWithoutStatuses() throws Exception {
		try {
			crm.validate(new Group(new Identifier("group"), null, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("group"), "error", "status", "Status is mandatory for a group");
		}
	}
	
	@Test
	public void testCreatingDuplicateGroups() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		try {
			crm.createGroup(GROUP);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another group: .*");
		}
		crm.disableGroup(groupId);
		try {
			crm.createGroup(GROUP);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another group: .*");
		}
	}
	
	@Test
	public void testCreatingGroupWithBlankNamesGivesMultipleErrors() throws Exception {
		try {
			crm.createGroup(new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Group code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
		try {
			crm.createGroup(new Localized(" ", " ", "\t"));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Group code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
	}
			
}