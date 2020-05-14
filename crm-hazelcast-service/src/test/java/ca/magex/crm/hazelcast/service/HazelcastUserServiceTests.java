package ca.magex.crm.hazelcast.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserServiceTests {

	@Autowired private CrmUserService hzUserService;
	@Autowired private HazelcastInstance hzInstance;

	@MockBean private CrmOrganizationService organizationService;
	@MockBean private CrmPermissionService permissionService;
	@MockBean private CrmPersonService personService;

	private PersonSummary adam = new PersonSummary(new Identifier("Adam"), new Identifier("DC"), Status.ACTIVE, "Adam");
	private PersonSummary bob = new PersonSummary(new Identifier("Bob"), new Identifier("DC"), Status.ACTIVE, "Bob");

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastUserService.HZ_USER_KEY).clear();
		Mockito.when(personService.findPersonSummary(new Identifier("Adam"))).thenReturn(adam);
		Mockito.when(personService.findPersonSummary(new Identifier("Bob"))).thenReturn(bob);
		Mockito.when(permissionService.findRoleByCode("ADM")).thenReturn(new Role(new Identifier("ADM"), new Identifier("AA"), "ADM", Status.ACTIVE, new Localized("ADM")));
		Mockito.when(permissionService.findRoleByCode("USR")).thenReturn(new Role(new Identifier("USR"), new Identifier("ZZ"), "USR", Status.ACTIVE, new Localized("USR")));
		Mockito.when(permissionService.findRoleByCode("PPL")).thenReturn(new Role(new Identifier("PPL"), new Identifier("ZZ"), "PPL", Status.ACTIVE, new Localized("PPL")));
	}

	@Test
	public void testUsers() {
		User u1 = hzUserService.createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, hzUserService.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));

		User u2 = hzUserService.createUser(new Identifier("Adam"), "adam-admin", List.of("ADM"));
		Assert.assertEquals(adam, u2.getPerson());
		Assert.assertEquals(List.of("ADM"), u2.getRoles());
		Assert.assertEquals(Status.ACTIVE, u2.getStatus());
		Assert.assertEquals("adam-admin", u2.getUsername());
		Assert.assertEquals(u2, hzUserService.findUserByUsername(u2.getUsername()));
		Assert.assertEquals(u2, hzUserService.findUser(u2.getUserId()));

		User u3 = hzUserService.createUser(new Identifier("Bob"), "bob-uber", List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(bob, u3.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u3.getRoles());
		Assert.assertEquals(Status.ACTIVE, u3.getStatus());
		Assert.assertEquals("bob-uber", u3.getUsername());
		Assert.assertEquals(u3, hzUserService.findUserByUsername(u3.getUsername()));
		Assert.assertEquals(u3, hzUserService.findUser(u3.getUserId()));

		/* update user */
		u1 = hzUserService.updateUserRoles(u1.getUserId(), List.of("USR"));
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));
		Assert.assertEquals(u1, hzUserService.updateUserRoles(u1.getUserId(), List.of("USR")));

		u1 = hzUserService.updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));
		Assert.assertEquals(u1, hzUserService.updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM")));

		/* disable user */
		u1 = hzUserService.disableUser(u1.getUserId());
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.INACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, hzUserService.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));
		Assert.assertEquals(u1, hzUserService.disableUser(u1.getUserId()));

		/* enable user */
		u1 = hzUserService.enableUser(u1.getUserId());
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, hzUserService.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));
		Assert.assertEquals(u1, hzUserService.enableUser(u1.getUserId()));

		/* count users */
		Assert.assertEquals(3, hzUserService.countUsers(new UsersFilter(null, null, null, null, null)));
		Assert.assertEquals(1, hzUserService.countUsers(new UsersFilter(null, null, null, "adam21", null)));
		Assert.assertEquals(2, hzUserService.countUsers(new UsersFilter(null, null, null, null, "PPL")));
		Assert.assertEquals(3, hzUserService.countUsers(new UsersFilter(null, null, null, null, "ADM")));
		Assert.assertEquals(2, hzUserService.countUsers(new UsersFilter(null, adam.getPersonId(), null, null, null)));
		Assert.assertEquals(1, hzUserService.countUsers(new UsersFilter(null, bob.getPersonId(), null, null, null)));
		Assert.assertEquals(3, hzUserService.countUsers(new UsersFilter(new Identifier("DC"), null, null, null, null)));
		Assert.assertEquals(0, hzUserService.countUsers(new UsersFilter(new Identifier("AB"), null, null, null, null)));
		Assert.assertEquals(3, hzUserService.countUsers(new UsersFilter(null, null, Status.ACTIVE, null, null)));
		Assert.assertEquals(0, hzUserService.countUsers(new UsersFilter(null, null, Status.INACTIVE, null, null)));

		/* find users */
		Page<User> usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, null, "adam21", null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, null, null, "PPL"),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u3, usersPage.getContent().get(1));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, null, null, "ADM"),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, adam.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, bob.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u3, usersPage.getContent().get(0));

		usersPage = hzUserService.findUsers(
				new UsersFilter(new Identifier("DC"), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = hzUserService.findUsers(
				new UsersFilter(new Identifier("AB"), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(0, usersPage.getNumberOfElements());
		Assert.assertEquals(0, usersPage.getTotalPages());
		Assert.assertEquals(0, usersPage.getTotalElements());
		Assert.assertEquals(0, usersPage.getContent().size());

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, Status.ACTIVE, null, null),
				new Paging(1, 5, Sort.by("person.displayName", "username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = hzUserService.findUsers(
				new UsersFilter(null, null, Status.INACTIVE, null, null),
				new Paging(1, 5, Sort.by("person.displayName")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(0, usersPage.getNumberOfElements());
		Assert.assertEquals(0, usersPage.getTotalPages());
		Assert.assertEquals(0, usersPage.getTotalElements());
		Assert.assertEquals(0, usersPage.getContent().size());
	}

	@Test
	public void testDuplicateUsername() {
		hzUserService.createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		try {
			hzUserService.createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		} catch (DuplicateItemFoundException e) {
			Assert.assertEquals("Duplicate item found found: Username 'adam21'", e.getMessage());
		}
	}

	@Test
	public void testInvalidUserId() {
		try {
			hzUserService.findUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			hzUserService.findUserByUsername("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}

		try {
			hzUserService.updateUserRoles(new Identifier("abc"), List.of(""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			hzUserService.enableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			hzUserService.disableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}
	}
}
