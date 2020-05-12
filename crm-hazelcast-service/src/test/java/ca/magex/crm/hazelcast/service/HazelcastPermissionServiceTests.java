package ca.magex.crm.hazelcast.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPermissionServiceTests {

	@Autowired private CrmPermissionService hzPermissionService;
	@Autowired private HazelcastInstance hzInstance;

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastPermissionService.HZ_ROLE_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_GROUP_KEY).clear();
	}

	@Test
	public void testGroups() {
		/* create */
		Group g1 = hzPermissionService.createGroup("A", new Localized("first", "premier"));
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hzPermissionService.findGroup(g1.getGroupId()));
		Assert.assertEquals(g1, hzPermissionService.findGroupByCode("A"));
		Group g2 = hzPermissionService.createGroup("B", new Localized("second", "deuxieme"));
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hzPermissionService.findGroup(g2.getGroupId()));
		Assert.assertEquals(g2, hzPermissionService.findGroupByCode("B"));
		Group g3 = hzPermissionService.createGroup("C", new Localized("third", "troisieme"));
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hzPermissionService.findGroup(g3.getGroupId()));
		Assert.assertEquals(g3, hzPermissionService.findGroupByCode("C"));

		/* update */
		g1 = hzPermissionService.updateGroupName(g1.getGroupId(), new Localized("one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hzPermissionService.findGroup(g1.getGroupId()));
		g1 = hzPermissionService.updateGroupName(g1.getGroupId(), g1.getName());
		g2 = hzPermissionService.updateGroupName(g2.getGroupId(), new Localized("two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hzPermissionService.findGroup(g2.getGroupId()));
		g2 = hzPermissionService.updateGroupName(g2.getGroupId(), g2.getName());
		g3 = hzPermissionService.updateGroupName(g3.getGroupId(), new Localized("three", "trois"));
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hzPermissionService.findGroup(g3.getGroupId()));
		g3 = hzPermissionService.updateGroupName(g3.getGroupId(), g3.getName());

		/* disable */
		g1 = hzPermissionService.disableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hzPermissionService.findGroup(g1.getGroupId()));
		g1 = hzPermissionService.disableGroup(g1.getGroupId());
		g2 = hzPermissionService.disableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hzPermissionService.findGroup(g2.getGroupId()));
		g2 = hzPermissionService.disableGroup(g2.getGroupId());
		g3 = hzPermissionService.disableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hzPermissionService.findGroup(g3.getGroupId()));
		g3 = hzPermissionService.disableGroup(g3.getGroupId());

		/* enable */
		g1 = hzPermissionService.enableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hzPermissionService.findGroup(g1.getGroupId()));
		g1 = hzPermissionService.enableGroup(g1.getGroupId());
		g2 = hzPermissionService.enableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hzPermissionService.findGroup(g2.getGroupId()));
		g2 = hzPermissionService.enableGroup(g2.getGroupId());
		g3 = hzPermissionService.enableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hzPermissionService.findGroup(g3.getGroupId()));
		g3 = hzPermissionService.enableGroup(g3.getGroupId());

		/* paging */
		Page<Group> page = hzPermissionService.findGroups(new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g2, page.getContent().get(2));

		page = hzPermissionService.findGroups(new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g2, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g1, page.getContent().get(2));

		page = hzPermissionService.findGroups(new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
			hzPermissionService.findGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
		
		try {
			hzPermissionService.findGroupByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group Code 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.updateGroupName(new Identifier("abc"), new Localized("four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.disableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.enableGroup(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testRoles() {
		/* create groups first */
		Group g1 = hzPermissionService.createGroup("A", new Localized("first", "premier"));
		Group g2 = hzPermissionService.createGroup("B", new Localized("second", "deuxieme"));
		Group g3 = hzPermissionService.createGroup("C", new Localized("third", "troisieme"));

		Role r1 = hzPermissionService.createRole(g1.getGroupId(), "ADM", new Localized("administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, hzPermissionService.findRole(r1.getRoleId()));
		Assert.assertEquals(r1, hzPermissionService.findRoleByCode(r1.getCode()));
		Role r2 = hzPermissionService.createRole(g1.getGroupId(), "MGR", new Localized("manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, hzPermissionService.findRole(r2.getRoleId()));
		Assert.assertEquals(r2, hzPermissionService.findRoleByCode(r2.getCode()));
		Role r3 = hzPermissionService.createRole(g1.getGroupId(), "USR", new Localized("user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, hzPermissionService.findRole(r3.getRoleId()));
		Assert.assertEquals(r3, hzPermissionService.findRoleByCode(r3.getCode()));
		
		/* add a couple extra roles for the other groups */
		hzPermissionService.createRole(g2.getGroupId(), "OTH", new Localized("OTHER", "OTHER"));
		hzPermissionService.createRole(g3.getGroupId(), "OTH", new Localized("OTHER", "OTHER"));
				
		
		/* update */
		r1 = hzPermissionService.updateRoleName(r1.getRoleId(), new Localized("one", "un"));
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, hzPermissionService.findRole(r1.getRoleId()));
		r1 = hzPermissionService.updateRoleName(r1.getRoleId(), r1.getName());
		r2 = hzPermissionService.updateRoleName(r2.getRoleId(), new Localized("two", "deux"));
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, hzPermissionService.findRole(r2.getRoleId()));
		r2 = hzPermissionService.updateRoleName(r2.getRoleId(), r2.getName());
		r3 = hzPermissionService.updateRoleName(r3.getRoleId(), new Localized("three", "trois"));
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, hzPermissionService.findRole(r3.getRoleId()));
		r3 = hzPermissionService.updateRoleName(r3.getRoleId(), r3.getName());
		
		/* disable */
		r1 = hzPermissionService.disableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r1.getStatus());
		Assert.assertEquals(r1, hzPermissionService.findRole(r1.getRoleId()));
		r1 = hzPermissionService.disableRole(r1.getRoleId());
		r2 = hzPermissionService.disableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r2.getStatus());
		Assert.assertEquals(r2, hzPermissionService.findRole(r2.getRoleId()));
		r2 = hzPermissionService.disableRole(r2.getRoleId());
		r3 = hzPermissionService.disableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, r3.getStatus());
		Assert.assertEquals(r3, hzPermissionService.findRole(r3.getRoleId()));
		r3 = hzPermissionService.disableRole(r3.getRoleId());
		
		/* enable */
		r1 = hzPermissionService.enableRole(r1.getRoleId());
		Assert.assertEquals("one", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, hzPermissionService.findRole(r1.getRoleId()));
		r1 = hzPermissionService.enableRole(r1.getRoleId());
		r2 = hzPermissionService.enableRole(r2.getRoleId());
		Assert.assertEquals("two", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, hzPermissionService.findRole(r2.getRoleId()));
		r2 = hzPermissionService.enableRole(r2.getRoleId());
		r3 = hzPermissionService.enableRole(r3.getRoleId());
		Assert.assertEquals("three", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, hzPermissionService.findRole(r3.getRoleId()));
		r3 = hzPermissionService.enableRole(r3.getRoleId());
		
		/* find roles for group */
		Page<Role> page = hzPermissionService.findRoles(g1.getGroupId(), new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(r1, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r2, page.getContent().get(2));
		
		page = hzPermissionService.findRoles(g1.getGroupId(), new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(r2, page.getContent().get(0));
		Assert.assertEquals(r3, page.getContent().get(1));
		Assert.assertEquals(r1, page.getContent().get(2));

		page = hzPermissionService.findRoles(g1.getGroupId(), new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
			hzPermissionService.findRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
		
		try {
			hzPermissionService.findRoleByCode("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role Code 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.updateRoleName(new Identifier("abc"), new Localized("four", "quatre"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.disableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}

		try {
			hzPermissionService.enableRole(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role ID 'abc'", e.getMessage());
		}
	}
}