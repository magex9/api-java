package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.*;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.ORG;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.ORG_ASSISTANT;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.*;

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
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.StructureValidationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractPermissionServiceTests {

	public abstract CrmInitializationService getInitializationService();
	
	public abstract CrmPermissionService getPermissionService();
	
	public abstract CrmLookupService getLookupService();
	
	public abstract CrmOrganizationService getOrganizationService();
	
	public abstract CrmLocationService getLocationService();
	
	public abstract CrmPersonService getPersonService();
	
	@Before
	public void setup() {
		getInitializationService().reset();
	}

	@Test
	public void testGroups() {
		/* create */
		Group g1 = getPermissionService().createGroup(new Localized("A", "first", "premier"));
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		Assert.assertEquals(g1, getPermissionService().findGroupByCode("A"));
		Group g2 = getPermissionService().createGroup(new Localized("B", "second", "deuxieme"));
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		Assert.assertEquals(g2, getPermissionService().findGroupByCode("B"));
		Group g3 = getPermissionService().createGroup(new Localized("C", "third", "troisieme"));
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		Assert.assertEquals(g3, getPermissionService().findGroupByCode("C"));

		/* update */
		g1 = getPermissionService().updateGroupName(g1.getGroupId(), new Localized(g1.getCode(), "one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		g1 = getPermissionService().updateGroupName(g1.getGroupId(), g1.getName());
		g2 = getPermissionService().updateGroupName(g2.getGroupId(), new Localized(g2.getCode(), "two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		g2 = getPermissionService().updateGroupName(g2.getGroupId(), g2.getName());
		g3 = getPermissionService().updateGroupName(g3.getGroupId(), new Localized(g3.getCode(), "three", "trois"));
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		g3 = getPermissionService().updateGroupName(g3.getGroupId(), g3.getName());

		/* disable */
		g1 = getPermissionService().disableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		try {
			g1 = getPermissionService().disableGroup(g1.getGroupId());
			fail("Cant disable a disabled group");
		} catch (BadRequestException expected) { }
		g2 = getPermissionService().disableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		try {
			g2 = getPermissionService().disableGroup(g2.getGroupId());
			fail("Cant disable a disabled group");
		} catch (BadRequestException expected) { }
		g3 = getPermissionService().disableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		try {
			g3 = getPermissionService().disableGroup(g3.getGroupId());
			fail("Cant disable a disabled group");
		} catch (BadRequestException expected) { }

		/* enable */
		g1 = getPermissionService().enableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		g1 = getPermissionService().enableGroup(g1.getGroupId());
		g2 = getPermissionService().enableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		g2 = getPermissionService().enableGroup(g2.getGroupId());
		g3 = getPermissionService().enableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		g3 = getPermissionService().enableGroup(g3.getGroupId());

		/* paging */
		Page<Group> page = getPermissionService().findGroups(new GroupsFilter(), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g2, page.getContent().get(2));

		page = getPermissionService().findGroups(new GroupsFilter(), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g2, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g1, page.getContent().get(2));

		page = getPermissionService().findGroups(new GroupsFilter(), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
			getPermissionService().findGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
		
		try {
			getPermissionService().findGroupByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group Code 'abc'", e.getMessage());
		}

		try {
			getPermissionService().updateGroupName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			getPermissionService().disableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			getPermissionService().enableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testRoles() {
		/* create groups first */
		Group g1 = getPermissionService().createGroup(new Localized("A", "first", "premier"));
		Group g2 = getPermissionService().createGroup(new Localized("B", "second", "deuxieme"));
		Group g3 = getPermissionService().createGroup(new Localized("C", "third", "troisieme"));

		Role r1 = getPermissionService().createRole(g1.getGroupId(), new Localized("ADM", "administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		Assert.assertEquals(r1, getPermissionService().findRoleByCode(r1.getCode()));
		Role r2 = getPermissionService().createRole(g1.getGroupId(), new Localized("MGR", "manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		Assert.assertEquals(r2, getPermissionService().findRoleByCode(r2.getCode()));
		Role r3 = getPermissionService().createRole(g1.getGroupId(), new Localized("USR", "user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, getPermissionService().findRole(r3.getRoleId()));
		Assert.assertEquals(r3, getPermissionService().findRoleByCode(r3.getCode()));
		
		/* add a couple extra roles for the other groups */
		getPermissionService().createRole(g2.getGroupId(), new Localized("OTH", "OTHER", "OTHER"));
		try {
			getPermissionService().createRole(g3.getGroupId(), new Localized("OTH", "OTHER", "OTHER"));
			fail("Cannot create duplicate roles");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
				
		/* update */
		r1 = getPermissionService().updateRoleName(r1.getRoleId(), new Localized(r1.getCode(), "one", "un"));
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		r1 = getPermissionService().updateRoleName(r1.getRoleId(), r1.getName());
		r2 = getPermissionService().updateRoleName(r2.getRoleId(), new Localized(r2.getCode(), "two", "deux"));
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		r2 = getPermissionService().updateRoleName(r2.getRoleId(), r2.getName());
		r3 = getPermissionService().updateRoleName(r3.getRoleId(), new Localized(r3.getCode(), "three", "trois"));
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, getPermissionService().findRole(r3.getRoleId()));
		r3 = getPermissionService().updateRoleName(r3.getRoleId(), r3.getName());
		
		/* disable */
		r1 = getPermissionService().disableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		r1 = getPermissionService().disableRole(r1.getRoleId());
		r2 = getPermissionService().disableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		r2 = getPermissionService().disableRole(r2.getRoleId());
		r3 = getPermissionService().disableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r3.getStatus());
		Assert.assertEquals(r3, getPermissionService().findRole(r3.getRoleId()));
		r3 = getPermissionService().disableRole(r3.getRoleId());
		
		/* enable */
		r1 = getPermissionService().enableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		r1 = getPermissionService().enableRole(r1.getRoleId());
		r2 = getPermissionService().enableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		r2 = getPermissionService().enableRole(r2.getRoleId());
		r3 = getPermissionService().enableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, getPermissionService().findRole(r3.getRoleId()));
		r3 = getPermissionService().enableRole(r3.getRoleId());
		
		/* find roles for group */
		Page<Role> page = getPermissionService().findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(r1, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r2, page.getContent().get(2));
		
		page = getPermissionService().findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(r2, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r1, page.getContent().get(2));

		page = getPermissionService().findRoles(new RolesFilter(g1.getGroupId(), null, null, null, null), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
			getPermissionService().findRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
		
		try {
			getPermissionService().findRoleByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role Code 'abc'", e.getMessage());
		}

		try {
			getPermissionService().updateRoleName(new Identifier("abc"), new Localized("4", "four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			getPermissionService().disableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			getPermissionService().enableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
	}
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		Identifier roleId = getPermissionService().createRole(groupId, ADMIN).getRoleId();
		assertEquals("Group", getPermissionService().findGroupByCode("GRP").getName(Lang.ENGLISH));
		try {
			getPermissionService().findGroup(roleId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }

		assertEquals("Admin", getPermissionService().findRoleByCode("ADM").getName(Lang.ENGLISH));
		try {
			getPermissionService().findRole(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}
	
	@Test
	public void testGroupWithInvalidCodes() throws Exception {
		
		getPermissionService().createGroup(new Localized("A", "English", "French"));
		try {
			getPermissionService().createGroup(new Localized(null, "English", "French"));
			fail("Invalid group code");
		} catch (IllegalArgumentException expected) { }
		try {
			getPermissionService().createGroup(new Localized("", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must not be blank");
		}
		try {
			getPermissionService().createGroup(new Localized("b", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must match: .*");
		}
		try {
			getPermissionService().createGroup(new Localized("$", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Group code must match: .*");
		}
	}	
	
	@Test
	public void testCreatingGroupWithInvalidStatuses() throws Exception {
		StructureValidationService validation = new StructureValidationService(getLookupService(), getPermissionService(), getOrganizationService(), getLocationService(), getPersonService());
		try {
			validation.validate(new Group(new Identifier("group"), Status.INACTIVE, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("group"), "error", "status", "Cannot create a new group that is inactive");
		}
		try {
			validation.validate(new Group(new Identifier("group"), null, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("group"), "error", "status", "Status is mandatory for a group");
		}
	}
	
	@Test
	public void testCreatingDuplicateGroups() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getPermissionService().createGroup(GROUP);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another group: .*");
		}
		getPermissionService().disableGroup(groupId);
		try {
			getPermissionService().createGroup(GROUP);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another group: .*");
		}
	}
	
	@Test
	public void testCreatingGroupWithBlankNamesGivesMultipleErrors() throws Exception {
		try {
			getPermissionService().createGroup(new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Group code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
		try {
			getPermissionService().createGroup(new Localized(" ", " ", "\t"));
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
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getPermissionService().createRole(groupId, new Localized(null, "English", "French"));
			fail("Invalid group code");
		} catch (IllegalArgumentException expected) { }
		try {
			getPermissionService().createRole(groupId, new Localized("", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must not be blank");
		}
		try {
			getPermissionService().createRole(groupId, new Localized("a", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
		try {
			getPermissionService().createRole(groupId, new Localized("$", "English", "French"));
			fail("Invalid group code");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Role code must match: .*");
		}
	}	
	
	@Test
	public void testCreatingRoleWithInvalidStatuses() throws Exception {
		StructureValidationService validation = new StructureValidationService(getLookupService(), getPermissionService(), getOrganizationService(), getLocationService(), getPersonService());
		try {
			validation.validate(new Role(new Identifier("role"), new Identifier("group"), Status.INACTIVE, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("group"), "error", "status", "Cannot create a new role that is inactive");
		}
		try {
			validation.validate(new Role(new Identifier("role"), new Identifier("group"), null, GROUP));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("group"), "error", "status", "Status is mandatory for a role");
		}
	}
	
	@Test
	public void testCreatingDuplicateRoles() throws Exception {
		Identifier groupId = getPermissionService().createGroup(SYS).getGroupId();
		Identifier roleId = getPermissionService().createRole(groupId, SYS_ADMIN).getRoleId();
		try {
			getPermissionService().createRole(groupId, SYS_ADMIN).getRoleId();
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
		getPermissionService().disableRole(roleId);
		try {
			getPermissionService().createRole(groupId, SYS_ADMIN);
			fail("Cannot create duplicate groups");
		} catch (BadRequestException e) { 
			assertBadRequestMessage(e, null, "error", "code", "Duplicate code found in another role: .*");
		}
	}
	
	@Test
	public void testCreateRoleForDisabledGroup() throws Exception {
		Identifier groupId = getPermissionService().createGroup(ORG).getGroupId();
		getPermissionService().createRole(groupId, ORG_ADMIN).getRoleId();
		getPermissionService().disableGroup(groupId);
		try {
			getPermissionService().createRole(groupId, ORG_ASSISTANT).getRoleId();
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "groupId", "Cannot create role for disabled group");
		}
	}
	
	@Test
	public void testCreatingRoleWithBlankNamesGivesMultipleErrors() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getPermissionService().createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
		try {
			getPermissionService().createRole(groupId, new Localized("", "", ""));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertEquals(3, e.getMessages().size());
			assertMessage(e.getMessages().get(0), null, "error", "code", "Role code must not be blank");
			assertMessage(e.getMessages().get(1), null, "error", "englishName", "An English description is required");
			assertMessage(e.getMessages().get(2), null, "error", "frenchName", "An French description is required");
		}
	}

	@Test
	public void testGroupSorting() throws Exception {
		for (Localized name : LOCALIZED_SORTING_OPTIONS) {
			getPermissionService().createGroup(name);
		}
		getPermissionService().disableGroup(getPermissionService().findGroupByCode("E").getGroupId());
		getPermissionService().disableGroup(getPermissionService().findGroupByCode("F").getGroupId());
		getPermissionService().disableGroup(getPermissionService().findGroupByCode("H").getGroupId());
		
		GroupsFilter filter = getPermissionService().defaultGroupsFilter();
		
		printList(LOCALIZED_SORTED_ENGLISH_ASC, String.class);
		printList(getPermissionService().findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
				.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()), String.class);
		
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC,
			getPermissionService().findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
			
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, 
			getPermissionService().findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("englishName"))))
					.getContent().stream().map(g -> g.getName(Lang.ENGLISH)).collect(Collectors.toList()));
				
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, 
			getPermissionService().findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.asc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
					
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, 
			getPermissionService().findGroups(filter, 
				GroupsFilter.getDefaultPaging().allItems().withSort(Sort.by(Order.desc("frenchName"))))
					.getContent().stream().map(g -> g.getName(Lang.FRENCH)).collect(Collectors.toList()));
	}
	
	@Test
	public void testGroupFilters() throws Exception {
		getPermissionService().createGroup(GROUP);
		getPermissionService().createGroup(SYS);
		getPermissionService().createGroup(ADMIN);
		getPermissionService().disableGroup(getPermissionService().createGroup(ENGLISH).getGroupId());
		getPermissionService().disableGroup(getPermissionService().createGroup(FRENCH).getGroupId());
		
		Paging englishSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("englishName")));
		Paging frenchSort = GroupsFilter.getDefaultPaging().withSort(Sort.by(Order.asc("frenchName")));
		
		assertEquals(List.of(ENGLISH, FRENCH, SYS),
			getPermissionService().findGroups(getPermissionService().defaultGroupsFilter()
				.withEnglishName("e"), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
			
		assertEquals(List.of(GROUP, SYS),
			getPermissionService().findGroups(getPermissionService().defaultGroupsFilter()
				.withFrenchName("e"), frenchSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
		
		assertEquals(List.of(ADMIN, GROUP, SYS),
			getPermissionService().findGroups(getPermissionService().defaultGroupsFilter()
				.withStatus(Status.ACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
		assertEquals(List.of(ENGLISH, FRENCH),
			getPermissionService().findGroups(getPermissionService().defaultGroupsFilter()
				.withStatus(Status.INACTIVE), englishSort).stream().map(g -> g.getName()).collect(Collectors.toList()));
				
	}
		
}