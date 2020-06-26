package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractUserServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected CrmAuthenticationService auth;
	
	private PersonDetails adam;

	private PersonDetails bob;

	private OrganizationDetails tAndA;

	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
		Identifier aaId = crm.createGroup(new Localized("AA", "Army Ants", "French Army Ants")).getGroupId();
		crm.createRole(aaId, new Localized("ADM", "ADM", "ADM"));

		Identifier zzId = crm.createGroup(new Localized("ZZ", "Ziggity Zaggity", "French Ziggity Zaggity")).getGroupId();
		crm.createRole(zzId, new Localized("USR", "USR", "USR"));
		crm.createRole(zzId, new Localized("PPL", "PPL", "PPL"));

		tAndA = crm.createOrganization("T&A", List.of("AA", "ZZ"));

		adam = crm.createPerson(
				tAndA.getOrganizationId(),
				ADAM,
				MAILING_ADDRESS,
				WORK_COMMUNICATIONS,
				BUSINESS_POSITION);

		bob = crm.createPerson(
				tAndA.getOrganizationId(),
				BOB,
				MAILING_ADDRESS,
				WORK_COMMUNICATIONS,
				BUSINESS_POSITION);
	}

	@After
	public void cleanup() {
		auth.logout();
	}
	
	@Test
	public void testUsers() {
		User u1 = crm.createUser(adam.getPersonId(), "adam21", List.of("USR", "PPL"));
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, crm.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, crm.findUser(u1.getUserId()));

		User u2 = crm.createUser(adam.getPersonId(), "adam-admin", List.of("ADM"));
		Assert.assertEquals(adam, u2.getPerson());
		Assert.assertEquals(List.of("ADM"), u2.getRoles());
		Assert.assertEquals(Status.ACTIVE, u2.getStatus());
		Assert.assertEquals("adam-admin", u2.getUsername());
		Assert.assertEquals(u2, crm.findUserByUsername(u2.getUsername()));
		Assert.assertEquals(u2, crm.findUser(u2.getUserId()));

		User u3 = crm.createUser(bob.getPersonId(), "bob-uber", List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(bob, u3.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u3.getRoles());
		Assert.assertEquals(Status.ACTIVE, u3.getStatus());
		Assert.assertEquals("bob-uber", u3.getUsername());
		Assert.assertEquals(u3, crm.findUserByUsername(u3.getUsername()));
		Assert.assertEquals(u3, crm.findUser(u3.getUserId()));

		/* update user */
		u1 = crm.updateUserRoles(u1.getUserId(), List.of("USR"));
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, crm.findUser(u1.getUserId()));
		Assert.assertEquals(u1, crm.updateUserRoles(u1.getUserId(), List.of("USR")));

		u1 = crm.updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(u1, crm.findUser(u1.getUserId()));
		Assert.assertEquals(u1, crm.updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM")));

		/* disable user */
		u1 = crm.disableUser(u1.getUserId());
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.INACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, crm.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, crm.findUser(u1.getUserId()));
		Assert.assertEquals(u1, crm.disableUser(u1.getUserId()));

		/* enable user */
		u1 = crm.enableUser(u1.getUserId());
		Assert.assertEquals(adam, u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, crm.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, crm.findUser(u1.getUserId()));
		Assert.assertEquals(u1, crm.enableUser(u1.getUserId()));

		/* count users */
		Assert.assertEquals(4, crm.countUsers(new UsersFilter(null, null, null, null, null)));
		Assert.assertEquals(1, crm.countUsers(new UsersFilter(null, null, null, "adam21", null)));
		Assert.assertEquals(2, crm.countUsers(new UsersFilter(null, null, null, null, "PPL")));
		Assert.assertEquals(3, crm.countUsers(new UsersFilter(null, null, null, null, "ADM")));
		Assert.assertEquals(2, crm.countUsers(new UsersFilter(null, adam.getPersonId(), null, null, null)));
		Assert.assertEquals(1, crm.countUsers(new UsersFilter(null, bob.getPersonId(), null, null, null)));
		Assert.assertEquals(3, crm.countUsers(new UsersFilter(tAndA.getOrganizationId(), null, null, null, null)));
		Assert.assertEquals(0, crm.countUsers(new UsersFilter(new Identifier("AB"), null, null, null, null)));
		Assert.assertEquals(4, crm.countUsers(new UsersFilter(null, null, Status.ACTIVE, null, null)));
		Assert.assertEquals(0, crm.countUsers(new UsersFilter(null, null, Status.INACTIVE, null, null)));

		/* find users */
		Page<User> usersPage = crm.findUsers(
				new UsersFilter(null, null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(4, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(4, usersPage.getTotalElements());
		Assert.assertEquals(4, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(3));

		usersPage = crm.findUsers(
				new UsersFilter(null, null, null, "adam21", null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));

		usersPage = crm.findUsers(
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

		usersPage = crm.findUsers(
				new UsersFilter(null, null, null, null, "ADM"),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = crm.findUsers(
				new UsersFilter(null, adam.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));

		usersPage = crm.findUsers(
				new UsersFilter(null, bob.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u3, usersPage.getContent().get(0));

		usersPage = crm.findUsers(
				new UsersFilter(tAndA.getOrganizationId(), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = crm.findUsers(
				new UsersFilter(new Identifier("AB"), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(0, usersPage.getNumberOfElements());
		Assert.assertEquals(0, usersPage.getTotalPages());
		Assert.assertEquals(0, usersPage.getTotalElements());
		Assert.assertEquals(0, usersPage.getContent().size());

		usersPage = crm.findUsers(
				new UsersFilter(null, null, Status.ACTIVE, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(4, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(4, usersPage.getTotalElements());
		Assert.assertEquals(4, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(3));

		usersPage = crm.findUsers(
				new UsersFilter(null, null, Status.INACTIVE, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(0, usersPage.getNumberOfElements());
		Assert.assertEquals(0, usersPage.getTotalPages());
		Assert.assertEquals(0, usersPage.getTotalElements());
		Assert.assertEquals(0, usersPage.getContent().size());
	}

	@Test
	public void testDuplicateUsername() {
		crm.createUser(adam.getPersonId(), "adam21", List.of("USR", "PPL"));
		try {
			crm.createUser(adam.getPersonId(), "adam21", List.of("USR", "PPL"));
		} catch (DuplicateItemFoundException e) {
			Assert.assertEquals("Duplicate item found found: Username 'adam21'", e.getMessage());
		}
	}

	@Test
	public void testInvalidUserId() {
		try {
			crm.findUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			crm.findUserByUsername("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}

		try {
			crm.updateUserRoles(new Identifier("abc"), List.of(""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			crm.enableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			crm.disableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier organizationId = crm.createOrganization("Org Name", List.of(ORG.getCode())).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, new PersonName("1", "Chris", "P", "Bacon"), CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, CrmAsserts.BUSINESS_POSITION).getPersonId();
		Identifier userId = crm.createUser(personId, "user", List.of(ORG_ADMIN.getCode())).getUserId();

		assertEquals(userId, crm.findUser(userId).getUserId());
		assertEquals(userId, crm.findUserByUsername("user").getUserId());
		try {
			crm.findUser(new Identifier("55"));
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) {
		}
	}

	@Test
	public void testResetPassword() throws Exception {
		Identifier organizationId = crm.createOrganization("Org Name", List.of(ORG.getCode())).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, new PersonName("1", "Chris", "P", "Bacon"), CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, CrmAsserts.BUSINESS_POSITION).getPersonId();
		Identifier userId = crm.createUser(personId, "user", List.of(ORG_ADMIN.getCode())).getUserId();

		try {
			crm.resetPassword(new Identifier("55"));
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) {
		}

		String temp = crm.resetPassword(userId);
		assertTrue(temp.matches("[A-Za-z0-9]+"));
	}

	@Test
	public void testChangePassword() throws Exception {
		Identifier organizationId = crm.createOrganization("Org Name", List.of(ORG.getCode())).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, new PersonName("1", "Chris", "P", "Bacon"), CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, CrmAsserts.BUSINESS_POSITION).getPersonId();
		Identifier userId = crm.createUser(personId, "user", List.of(ORG_ADMIN.getCode())).getUserId();

		assertTrue(crm.changePassword(userId, crm.resetPassword(userId), "pass1"));
		assertTrue(crm.changePassword(userId, "pass1", "pass2"));
		assertTrue(crm.changePassword(userId, "pass2", "pass3"));
		assertFalse(crm.changePassword(userId, "pass2", "pass4"));
		assertFalse(crm.changePassword(userId, crm.resetPassword(userId), ""));
	}

}
