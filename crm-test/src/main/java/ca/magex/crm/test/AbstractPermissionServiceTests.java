package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ADMIN;
import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.FRENCH;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_ENGLISH_DESC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_ASC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTED_FRENCH_DESC;
import static ca.magex.crm.test.CrmAsserts.LOCALIZED_SORTING_OPTIONS;
import static ca.magex.crm.test.CrmAsserts.ORG;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.ORG_ASSISTANT;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractPermissionServiceTests {

	protected Crm crm;
	
	protected AbstractPermissionServiceTests() {}
	
	public AbstractPermissionServiceTests(Crm crm) {
		this.crm = crm;
	}
	
	@Before
	public void setup() {
		crm.reset();
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
		Page<Group> page = crm.findGroups(new GroupsFilter(), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g2, page.getContent().get(2));

		page = crm.findGroups(new GroupsFilter(), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g2, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g1, page.getContent().get(2));

		page = crm.findGroups(new GroupsFilter(), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(2, page.getTotalPages());
		Assert.assertEquals(2, page.getNumberOfElements());
		Assert.assertEquals(2, page.getSize());
		Assert.assertEquals(2, page.getContent().size());
		/* order should be 2, 3 */
		Assert.assertEquals(g2, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
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
	
	@Test
	public void testRolesWithInvalidCodes() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		try {
			crm.createRole(groupId, new Localized(null, "English", "French"));
			fail("Invalid group code");
		} catch (IllegalArgumentException expected) { }
		try {
			crm.createRole(groupId, new Localized("", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must not be blank");
		}
		try {
			crm.createRole(groupId, new Localized("a", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
		try {
			crm.createRole(groupId, new Localized("$", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
	}	
	
	@Test
	public void testCreatingRoleWithoutStatus() throws Exception {
		try {
			crm.validate(new Role(new Identifier("role"), new Identifier("group"), null, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("role"), "error", "status", "Status is mandatory for a role");
		}
	}
	
	@Test
	public void testCreatingDuplicateRoles() throws Exception {
		Identifier groupId = crm.createGroup(SYS).getGroupId();
		Identifier roleId = crm.createRole(groupId, SYS_ADMIN).getRoleId();
		try {
			crm.createRole(groupId, SYS_ADMIN).getRoleId();
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
		crm.disableRole(roleId);
		try {
			crm.createRole(groupId, SYS_ADMIN);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
	}
	
	@Test
	public void testCreateRoleForDisabledGroup() throws Exception {
		Identifier groupId = crm.createGroup(ORG).getGroupId();
		crm.createRole(groupId, ORG_ADMIN).getRoleId();
		crm.disableGroup(groupId);
		try {
			crm.createRole(groupId, ORG_ASSISTANT).getRoleId();
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "groupId", "Cannot create role for disabled group");
		}
	}
	
	@Test
	public void testCreatingRoleWithBlankNamesGivesMultipleErrors() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		try {
			crm.createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
		try {
			crm.createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
	}
	
	@Test
	public void testGroupPaging() throws Exception {
		for (Localized name : LOCALIZED_SORTING_OPTIONS) {
			crm.createGroup(name);
		}
		GroupsFilter filter = crm.defaultGroupsFilter();
		Page<Group> page1 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName"))));
		assertEquals(1, page1.getNumber());
		assertEquals(false, page1.hasPrevious());
		assertEquals(true, page1.hasNext());
		assertEquals(10, page1.getContent().size());
		
		Page<Group> page2 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName"))).withPageNumber(2));
		assertEquals(2, page2.getNumber());
		assertEquals(true, page2.hasPrevious());
		assertEquals(true, page2.hasNext());
		assertEquals(10, page2.getContent().size());
		
		Page<Group> page3 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("code"))).withPageNumber(3));
		assertEquals(3, page3.getNumber());		
		assertEquals(true, page3.hasPrevious());
		assertEquals(true, page3.hasNext());
		assertEquals(10, page3.getContent().size());
		
		Page<Group> page4 = crm.findGroups(filter, GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName"))).withPageNumber(4));
		assertEquals(4, page4.getNumber());
		assertEquals(true, page4.hasPrevious());
		assertEquals(false, page4.hasNext());
		assertEquals(2, page4.getContent().size());
	}

	@Test
	public void testGroupSorting() throws Exception {
		for (Localized name : LOCALIZED_SORTING_OPTIONS) {
			crm.createGroup(name);
		}		
		crm.disableGroup(crm.findGroupByCode("E").getGroupId());
		crm.disableGroup(crm.findGroupByCode("F").getGroupId());
		crm.disableGroup(crm.findGroupByCode("H").getGroupId());
		
		GroupsFilter filter = crm.defaultGroupsFilter();
		
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC,
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
			
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
				
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
					
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, 
			crm.findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
	}
	
	@Test
	public void testGroupFilters() throws Exception {
		crm.createGroup(GROUP);
		crm.createGroup(SYS);
		crm.createGroup(ADMIN);
		crm.disableGroup(crm.createGroup(ENGLISH).getGroupId());
		crm.disableGroup(crm.createGroup(FRENCH).getGroupId());
		
		Paging englishSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName")));
		Paging frenchSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName")));
		
		assertEquals(List.of(ENGLISH, FRENCH, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withEnglishName("e"), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
			
		assertEquals(List.of(GROUP, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withFrenchName("e"), frenchSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
		
		assertEquals(List.of(ADMIN, GROUP, SYS),
			crm.findGroups(crm.defaultGroupsFilter()
				.withStatus(Status.ACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
		assertEquals(List.of(ENGLISH, FRENCH),
			crm.findGroups(crm.defaultGroupsFilter()
				.withStatus(Status.INACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
	}

	@Test
	public void testRoleSorting() throws Exception {
		Identifier group1 = crm.createGroup(SYS).getGroupId();
		Identifier group2 = crm.createGroup(ADMIN).getGroupId();
		for (int i = 0; i < LOCALIZED_SORTING_OPTIONS.size(); i++) {
			if (i < 6) {
				crm.createRole(group1, LOCALIZED_SORTING_OPTIONS.get(i));
			} else {
				crm.createRole(group2, LOCALIZED_SORTING_OPTIONS.get(i));
			}
		}
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(3).getCode()).getRoleId());
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(8).getCode()).getRoleId());
		crm.disableRole(crm.findRoleByCode(LOCALIZED_SORTING_OPTIONS.get(11).getCode()).getRoleId());
		
		RolesFilter filter = crm.defaultRolesFilter();
		
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC,
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
			
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
				
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
					
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, 
			crm.findRoles(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
		
		assertEquals(List.of(LOCALIZED_SORTING_OPTIONS.get(8).getEnglishName(), LOCALIZED_SORTING_OPTIONS.get(11).getEnglishName()),
			crm.findRoles(filter.withGroupId(group2).withStatus(Status.INACTIVE), 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
		
	}
	
	@Test
	public void testRolesFilters() throws Exception {
		Identifier groupId = crm.createGroup(ORG).getGroupId();
		crm.createRole(groupId, GROUP);
		crm.createRole(groupId, SYS);
		crm.createRole(groupId, ADMIN);
		crm.disableRole(crm.createRole(groupId, ENGLISH).getRoleId());
		crm.disableRole(crm.createRole(groupId, FRENCH).getRoleId());
		
		Paging englishSort = RolesFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName")));
		Paging frenchSort = RolesFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName")));
		
		assertEquals(List.of(ENGLISH, FRENCH, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withEnglishName("e"), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
			
		assertEquals(List.of(GROUP, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withFrenchName("e"), frenchSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
		
		assertEquals(List.of(ADMIN, GROUP, SYS),
			crm.findRoles(crm.defaultRolesFilter()
				.withStatus(Status.ACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
		assertEquals(List.of(ENGLISH, FRENCH),
			crm.findRoles(crm.defaultRolesFilter()
				.withStatus(Status.INACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
	}
		
}