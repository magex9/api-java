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
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPermissionServiceTests {

	@Autowired private HazelcastPermissionService hazelcastPermissionService;
	@Autowired private HazelcastInstance hzInstance;

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastPermissionService.HZ_PERMISSION_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_ROLE_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_GROUP_KEY).clear();
	}

	@Test
	public void testGroups() {
		/* create */
		Group g1 = hazelcastPermissionService.createGroup(new Localized("first", "premier"));
		Assert.assertEquals("first", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("premier", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hazelcastPermissionService.findGroup(g1.getGroupId()));
		Group g2 = hazelcastPermissionService.createGroup(new Localized("second", "deuxieme"));
		Assert.assertEquals("second", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deuxieme", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hazelcastPermissionService.findGroup(g2.getGroupId()));
		Group g3 = hazelcastPermissionService.createGroup(new Localized("third", "troisieme"));
		Assert.assertEquals("third", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("troisieme", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hazelcastPermissionService.findGroup(g3.getGroupId()));

		/* update */
		g1 = hazelcastPermissionService.updateGroupName(g1.getGroupId(), new Localized("one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hazelcastPermissionService.findGroup(g1.getGroupId()));
		g1 = hazelcastPermissionService.updateGroupName(g1.getGroupId(), g1.getName());
		g2 = hazelcastPermissionService.updateGroupName(g2.getGroupId(), new Localized("two", "deux"));
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hazelcastPermissionService.findGroup(g2.getGroupId()));
		g2 = hazelcastPermissionService.updateGroupName(g2.getGroupId(), g2.getName());
		g3 = hazelcastPermissionService.updateGroupName(g3.getGroupId(), new Localized("three", "trois"));
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hazelcastPermissionService.findGroup(g3.getGroupId()));
		g3 = hazelcastPermissionService.updateGroupName(g3.getGroupId(), g3.getName());

		/* disable */
		g1 = hazelcastPermissionService.disableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hazelcastPermissionService.findGroup(g1.getGroupId()));
		g1 = hazelcastPermissionService.disableGroup(g1.getGroupId());
		g2 = hazelcastPermissionService.disableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hazelcastPermissionService.findGroup(g2.getGroupId()));
		g2 = hazelcastPermissionService.disableGroup(g2.getGroupId());
		g3 = hazelcastPermissionService.disableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.INACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hazelcastPermissionService.findGroup(g3.getGroupId()));
		g3 = hazelcastPermissionService.disableGroup(g3.getGroupId());

		/* enable */
		g1 = hazelcastPermissionService.enableGroup(g1.getGroupId());
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hazelcastPermissionService.findGroup(g1.getGroupId()));
		g1 = hazelcastPermissionService.enableGroup(g1.getGroupId());
		g2 = hazelcastPermissionService.enableGroup(g2.getGroupId());
		Assert.assertEquals("two", g2.getName(Lang.ENGLISH));
		Assert.assertEquals("deux", g2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g2.getStatus());
		Assert.assertEquals(g2, hazelcastPermissionService.findGroup(g2.getGroupId()));
		g2 = hazelcastPermissionService.enableGroup(g2.getGroupId());
		g3 = hazelcastPermissionService.enableGroup(g3.getGroupId());
		Assert.assertEquals("three", g3.getName(Lang.ENGLISH));
		Assert.assertEquals("trois", g3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g3.getStatus());
		Assert.assertEquals(g3, hazelcastPermissionService.findGroup(g3.getGroupId()));
		g3 = hazelcastPermissionService.enableGroup(g3.getGroupId());

		/* paging */
		Page<Group> page = hazelcastPermissionService.findGroups(new Paging(1, 5, Sort.by("name:" + Lang.ENGLISH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 1, 3, 2 */
		Assert.assertEquals(g1, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g2, page.getContent().get(2));

		page = hazelcastPermissionService.findGroups(new Paging(1, 5, Sort.by("name:" + Lang.FRENCH)));
		Assert.assertEquals(1, page.getNumber());
		Assert.assertEquals(1, page.getTotalPages());
		Assert.assertEquals(3, page.getNumberOfElements());
		Assert.assertEquals(5, page.getSize());
		Assert.assertEquals(3, page.getContent().size());
		/* order should be 2, 3, 1 */
		Assert.assertEquals(g2, page.getContent().get(0));
		Assert.assertEquals(g3, page.getContent().get(1));
		Assert.assertEquals(g1, page.getContent().get(2));

		page = hazelcastPermissionService.findGroups(new Paging(1, 2, Sort.by("name:" + Lang.FRENCH)));
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
	public void testInvalidId() {
		try {
			hazelcastPermissionService.findGroup(new Identifier("abc"));
			Assert.fail("should faile if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			hazelcastPermissionService.updateGroupName(new Identifier("abc"), new Localized("four", "quatre"));
			Assert.fail("should faile if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			hazelcastPermissionService.disableGroup(new Identifier("abc"));
			Assert.fail("should faile if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}

		try {
			hazelcastPermissionService.enableGroup(new Identifier("abc"));
			Assert.fail("should faile if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Group ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testRoles() {
		/* create groups first */
		Group g1 = hazelcastPermissionService.createGroup(new Localized("first", "premier"));
		Group g2 = hazelcastPermissionService.createGroup(new Localized("second", "deuxieme"));
		Group g3 = hazelcastPermissionService.createGroup(new Localized("third", "troisieme"));

		Role r1 = hazelcastPermissionService.createRole(g1.getGroupId(), "ADM", new Localized("administrator", "administrateur"));
		Assert.assertEquals("ADM", r1.getCode());
		Assert.assertEquals("administrator", r1.getName(Lang.ENGLISH));
		Assert.assertEquals("administrateur", r1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r1.getStatus());
		Assert.assertEquals(r1, hazelcastPermissionService.findRole(r1.getRoleId()));
		Role r2 = hazelcastPermissionService.createRole(g1.getGroupId(), "MGR", new Localized("manager", "gestionaire"));
		Assert.assertEquals("MGR", r2.getCode());
		Assert.assertEquals("manager", r2.getName(Lang.ENGLISH));
		Assert.assertEquals("gestionaire", r2.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r2.getStatus());
		Assert.assertEquals(r2, hazelcastPermissionService.findRole(r2.getRoleId()));
		Role r3 = hazelcastPermissionService.createRole(g1.getGroupId(), "USR", new Localized("user", "utilisateur"));
		Assert.assertEquals("USR", r3.getCode());
		Assert.assertEquals("user", r3.getName(Lang.ENGLISH));
		Assert.assertEquals("utilisateur", r3.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, r3.getStatus());
		Assert.assertEquals(r3, hazelcastPermissionService.findRole(r3.getRoleId()));
		
		/* update */
		g1 = hazelcastPermissionService.updateGroupName(g1.getGroupId(), new Localized("one", "un"));
		Assert.assertEquals("one", g1.getName(Lang.ENGLISH));
		Assert.assertEquals("un", g1.getName(Lang.FRENCH));
		Assert.assertEquals(Status.ACTIVE, g1.getStatus());
		Assert.assertEquals(g1, hazelcastPermissionService.findGroup(g1.getGroupId()));
		g1 = hazelcastPermissionService.updateGroupName(g1.getGroupId(), g1.getName());
	}
}