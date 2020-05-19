package ca.magex.crm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractPermissionServiceTests {

	public abstract CrmPermissionService getPermissionService();

	public abstract void reset();
		
	@Before
	public void setup() {
		reset();
	}

	@Test
	public void testGroups() {
		/* create */
		Group g1 = getPermissionService().createGroup("A", new Localized("first", "premier"));
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		Assert.assertEquals(g1, getPermissionService().findGroupByCode("A"));
		Group g2 = getPermissionService().createGroup("B", new Localized("second", "deuxieme"));
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		Assert.assertEquals(g2, getPermissionService().findGroupByCode("B"));
		Group g3 = getPermissionService().createGroup("C", new Localized("third", "troisieme"));
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		Assert.assertEquals(g3, getPermissionService().findGroupByCode("C"));

		/* update */
		g1 = getPermissionService().updateGroupName(g1.getGroupId(), new Localized("one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, getPermissionService().findGroup(g1.getGroupId()));
		g1 = getPermissionService().updateGroupName(g1.getGroupId(), g1.getName());
		g2 = getPermissionService().updateGroupName(g2.getGroupId(), new Localized("two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		g2 = getPermissionService().updateGroupName(g2.getGroupId(), g2.getName());
		g3 = getPermissionService().updateGroupName(g3.getGroupId(), new Localized("three", "trois"));
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
		g1 = getPermissionService().disableGroup(g1.getGroupId());
		g2 = getPermissionService().disableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, getPermissionService().findGroup(g2.getGroupId()));
		g2 = getPermissionService().disableGroup(g2.getGroupId());
		g3 = getPermissionService().disableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, getPermissionService().findGroup(g3.getGroupId()));
		g3 = getPermissionService().disableGroup(g3.getGroupId());

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
			getPermissionService().updateGroupName(new Identifier("abc"), new Localized("four", "quatre"));
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
		Group g1 = getPermissionService().createGroup("A", new Localized("first", "premier"));
		Group g2 = getPermissionService().createGroup("B", new Localized("second", "deuxieme"));
		Group g3 = getPermissionService().createGroup("C", new Localized("third", "troisieme"));

		Role r1 = getPermissionService().createRole(g1.getGroupId(), "ADM", new Localized("administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		Assert.assertEquals(r1, getPermissionService().findRoleByCode(r1.getCode()));
		Role r2 = getPermissionService().createRole(g1.getGroupId(), "MGR", new Localized("manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		Assert.assertEquals(r2, getPermissionService().findRoleByCode(r2.getCode()));
		Role r3 = getPermissionService().createRole(g1.getGroupId(), "USR", new Localized("user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, getPermissionService().findRole(r3.getRoleId()));
		Assert.assertEquals(r3, getPermissionService().findRoleByCode(r3.getCode()));
		
		/* add a couple extra roles for the other groups */
		getPermissionService().createRole(g2.getGroupId(), "OTH", new Localized("OTHER", "OTHER"));
		getPermissionService().createRole(g3.getGroupId(), "OTH", new Localized("OTHER", "OTHER"));
				
		
		/* update */
		r1 = getPermissionService().updateRoleName(r1.getRoleId(), new Localized("one", "un"));
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, getPermissionService().findRole(r1.getRoleId()));
		r1 = getPermissionService().updateRoleName(r1.getRoleId(), r1.getName());
		r2 = getPermissionService().updateRoleName(r2.getRoleId(), new Localized("two", "deux"));
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, getPermissionService().findRole(r2.getRoleId()));
		r2 = getPermissionService().updateRoleName(r2.getRoleId(), r2.getName());
		r3 = getPermissionService().updateRoleName(r3.getRoleId(), new Localized("three", "trois"));
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
			getPermissionService().updateRoleName(new Identifier("abc"), new Localized("four", "quatre"));
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
		Identifier groupId = getPermissionService().createGroup("GRP", new Localized("Group")).getGroupId();
		Identifier roleId = getPermissionService().createRole(groupId, "ADMIN", new Localized("Admin")).getRoleId();
		assertEquals("Group", getPermissionService().findGroupByCode("GRP").getName(Lang.ENGLISH));
		try {
			getPermissionService().findGroup(roleId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }

		assertEquals("Admin", getPermissionService().findRoleByCode("ADMIN").getName(Lang.ENGLISH));
		try {
			getPermissionService().findRole(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}
	
}