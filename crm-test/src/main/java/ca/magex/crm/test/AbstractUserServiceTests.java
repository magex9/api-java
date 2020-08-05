package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ADAM;
import static ca.magex.crm.test.CrmAsserts.BOB;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ORG;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

@Transactional
public abstract class AbstractUserServiceTests {

	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract Crm config();			
	
	/**
	 * Authentication service used to allow an authenticated test
	 * @return
	 */
	protected abstract CrmAuthenticationService auth();
	
	/**
	 * The CRM Services to be tested
	 * @return
	 */
	protected abstract CrmUserService users();

	@Before
	public void setup() {
		config().reset();
		config().initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth().login("admin", "admin");
	}

	@After
	public void cleanup() {
		auth().logout();
	}
	
	@Test
	public void testUsers() {		
		OrganizationDetails tAndA = config().createOrganization("T&A", List.of(CrmAsserts.ORG, CrmAsserts.CRM, CrmAsserts.SYS), List.of(new BusinessGroupIdentifier("ORG")));

		PersonDetails adam = config().createPerson(
				tAndA.getOrganizationId(),
				CrmAsserts.displayName(ADAM),
				ADAM,
				MAILING_ADDRESS,
				WORK_COMMUNICATIONS,
				List.of(CrmAsserts.QA_TEAMLEAD));

		PersonDetails bob = config().createPerson(
				tAndA.getOrganizationId(),
				CrmAsserts.displayName(BOB),
				BOB,
				MAILING_ADDRESS,
				WORK_COMMUNICATIONS,
				List.of(CrmAsserts.DEV_TEAMLEAD));
		
		UserDetails u1 = users().createUser(adam.getPersonId(), "adam21", List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN));
		Assert.assertEquals(adam.getPersonId(), u1.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN), u1.getAuthenticationRoleIds());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, users().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, users().findUserDetails(u1.getUserId()));

		UserDetails u2 = users().createUser(adam.getPersonId(), "adamadmin", List.of(CrmAsserts.SYS_ADMIN));
		Assert.assertEquals(adam.getPersonId(), u2.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.SYS_ADMIN), u2.getAuthenticationRoleIds());
		Assert.assertEquals(Status.ACTIVE, u2.getStatus());
		Assert.assertEquals("adamadmin", u2.getUsername());
		Assert.assertEquals(u2, users().findUserByUsername(u2.getUsername()));
		Assert.assertEquals(u2, users().findUserDetails(u2.getUserId()));

		UserDetails u3 = users().createUser(bob.getPersonId(), "bobuber", List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN));
		Assert.assertEquals(bob.getPersonId(), u3.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN), u3.getAuthenticationRoleIds());
		Assert.assertEquals(Status.ACTIVE, u3.getStatus());
		Assert.assertEquals("bobuber", u3.getUsername());
		Assert.assertEquals(u3, users().findUserByUsername(u3.getUsername()));
		Assert.assertEquals(u3, users().findUserDetails(u3.getUserId()));

		/* update user */
		u1 = users().updateUserRoles(u1.getUserId(), List.of(CrmAsserts.ORG_ADMIN));
		Assert.assertEquals(adam.getPersonId(), u1.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN), u1.getAuthenticationRoleIds());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, users().findUserDetails(u1.getUserId()));
		Assert.assertEquals(u1, users().updateUserRoles(u1.getUserId(), List.of(CrmAsserts.ORG_ADMIN)));

		u1 = users().updateUserRoles(u1.getUserId(), List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN));
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN), u1.getAuthenticationRoleIds());
		Assert.assertEquals(u1, users().findUserDetails(u1.getUserId()));
		Assert.assertEquals(u1, users().updateUserRoles(u1.getUserId(), List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN)));

		/* disable user */
		u1 = users().findUserDetails(users().disableUser(u1.getUserId()).getUserId());
		Assert.assertEquals(adam.getPersonId(), u1.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN), u1.getAuthenticationRoleIds());
		Assert.assertEquals(Status.INACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, users().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, users().findUserDetails(u1.getUserId()));
		Assert.assertEquals(u1.asSummary(), users().disableUser(u1.getUserId()));

		/* enable user */
		u1 = users().findUserDetails(users().enableUser(u1.getUserId()).getUserId());
		Assert.assertEquals(adam.getPersonId(), u1.getPersonId());
		Assert.assertEquals(List.of(CrmAsserts.ORG_ADMIN, CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN), u1.getAuthenticationRoleIds());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, users().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, users().findUserDetails(u1.getUserId()));
		Assert.assertEquals(u1.asSummary(), users().enableUser(u1.getUserId()));

		/* count users */
		Assert.assertEquals(4, users().countUsers(new UsersFilter(null, null, null, null, null)));
		Assert.assertEquals(1, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), null, null, "adam21", null)));
		Assert.assertEquals(2, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), null, null, null, CrmAsserts.ORG_ADMIN)));
		Assert.assertEquals(2, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), null, null, null, CrmAsserts.CRM_ADMIN)));
		Assert.assertEquals(2, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), adam.getPersonId(), null, null, null)));
		Assert.assertEquals(1, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), bob.getPersonId(), null, null, null)));
		Assert.assertEquals(3, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), null, Status.ACTIVE, null, null)));
		Assert.assertEquals(0, users().countUsers(new UsersFilter(tAndA.getOrganizationId(), null, Status.INACTIVE, null, null)));

		/* find users */
		Page<UserDetails> usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(3, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(3, usersPage.getTotalElements());
		Assert.assertEquals(3, usersPage.getContent().size());
		
		System.out.println(usersPage.getContent().get(0).getUsername() + "," + usersPage.getContent().get(1).getUsername() + "," + usersPage.getContent().get(2).getUsername());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));
		Assert.assertEquals(u3, usersPage.getContent().get(2));

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, null, "adam21", null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, null, null, CrmAsserts.ORG_ADMIN),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u3, usersPage.getContent().get(1));

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, null, null, CrmAsserts.CRM_ADMIN),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u3, usersPage.getContent().get(1));

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), adam.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));
		Assert.assertEquals(u2, usersPage.getContent().get(1));

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), bob.getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u3, usersPage.getContent().get(0));
		
		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, Status.ACTIVE, null, null),
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

		usersPage = users().findUserDetails(
				new UsersFilter(tAndA.getOrganizationId(), null, Status.INACTIVE, null, null),
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
		OrganizationDetails tAndA = config().createOrganization("T&A", List.of(CrmAsserts.ORG, CrmAsserts.CRM, CrmAsserts.SYS), List.of(new BusinessGroupIdentifier("ORG")));

		PersonDetails adam = config().createPerson(
				tAndA.getOrganizationId(),
				CrmAsserts.displayName(ADAM),
				ADAM,
				MAILING_ADDRESS,
				WORK_COMMUNICATIONS,
				List.of(CrmAsserts.QA_TEAMLEAD));
		
		users().createUser(adam.getPersonId(), "adam21", List.of(CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN));
		try {
			users().createUser(adam.getPersonId(), "adam21", List.of(CrmAsserts.CRM_ADMIN, CrmAsserts.SYS_ADMIN));
		} catch (DuplicateItemFoundException e) {
			Assert.assertEquals("Duplicate item found: Username 'adam21'", e.getMessage());
		}
	}

	@Test
	public void testInvalidUserId() {
		try {
			users().findUserDetails(new UserIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID '/users/abc'", e.getMessage());
		}

		try {
			users().findUserByUsername("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}

		try {
			users().updateUserRoles(new UserIdentifier("abc"), List.of());
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID '/users/abc'", e.getMessage());
		}

		try {
			users().enableUser(new UserIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID '/users/abc'", e.getMessage());
		}

		try {
			users().disableUser(new UserIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID '/users/abc'", e.getMessage());
		}
	}
	

	@Test
	public void testResetPassword() throws Exception {
		OrganizationIdentifier organizationId = config().createOrganization("Org Name", List.of(ORG), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		PersonIdentifier personId = config().createPerson(organizationId, CrmAsserts.displayName(BOB), CrmAsserts.BOB, CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, List.of(CrmAsserts.CEO)).getPersonId();
		UserIdentifier userId = users().createUser(personId, "user", List.of(ORG_ADMIN)).getUserId();

		try {
			users().resetPassword(new UserIdentifier("55"));
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) {
		}

		String temp = users().resetPassword(userId);
		assertTrue(temp.matches("[A-Za-z0-9]+"));
	}

	@Test
	public void testChangePassword() throws Exception {
		OrganizationIdentifier organizationId = config().createOrganization("Org Name", List.of(ORG), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		PersonIdentifier personId = config().createPerson(organizationId, CrmAsserts.displayName(BOB), CrmAsserts.BOB, CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, List.of(CrmAsserts.CEO)).getPersonId();
		UserIdentifier userId = users().createUser(personId, "user", List.of(ORG_ADMIN)).getUserId();

		assertTrue(users().changePassword(userId, users().resetPassword(userId), "pass1"));
		assertTrue(users().changePassword(userId, "pass1", "pass2"));
		assertTrue(users().changePassword(userId, "pass2", "pass3"));
		assertFalse(users().changePassword(userId, "pass2", "pass4"));
		assertFalse(users().changePassword(userId, users().resetPassword(userId), ""));
	}
}