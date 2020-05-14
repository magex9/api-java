package ca.magex.crm.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public abstract class AbstractUserServiceTests {

	public abstract CrmUserService getUserService();
	
	public abstract PersonDetails getAdam();
	
	public abstract PersonDetails getBob();

	@Test
	public void testUsers() {
		User u1 = getUserService().createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		Assert.assertEquals(getAdam(), u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, getUserService().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, getUserService().findUser(u1.getUserId()));

		User u2 = getUserService().createUser(new Identifier("Adam"), "adam-admin", List.of("ADM"));
		Assert.assertEquals(getAdam(), u2.getPerson());
		Assert.assertEquals(List.of("ADM"), u2.getRoles());
		Assert.assertEquals(Status.ACTIVE, u2.getStatus());
		Assert.assertEquals("adam-admin", u2.getUsername());
		Assert.assertEquals(u2, getUserService().findUserByUsername(u2.getUsername()));
		Assert.assertEquals(u2, getUserService().findUser(u2.getUserId()));

		User u3 = getUserService().createUser(new Identifier("Bob"), "bob-uber", List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(getBob(), u3.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u3.getRoles());
		Assert.assertEquals(Status.ACTIVE, u3.getStatus());
		Assert.assertEquals("bob-uber", u3.getUsername());
		Assert.assertEquals(u3, getUserService().findUserByUsername(u3.getUsername()));
		Assert.assertEquals(u3, getUserService().findUser(u3.getUserId()));

		/* update user */
		u1 = getUserService().updateUserRoles(u1.getUserId(), List.of("USR"));
		Assert.assertEquals(getAdam(), u1.getPerson());
		Assert.assertEquals(List.of("USR"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, getUserService().findUser(u1.getUserId()));
		Assert.assertEquals(u1, getUserService().updateUserRoles(u1.getUserId(), List.of("USR")));

		u1 = getUserService().updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM"));
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(u1, getUserService().findUser(u1.getUserId()));
		Assert.assertEquals(u1, getUserService().updateUserRoles(u1.getUserId(), List.of("USR", "PPL", "ADM")));

		/* disable user */
		u1 = getUserService().disableUser(u1.getUserId());
		Assert.assertEquals(getAdam(), u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.INACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, getUserService().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, getUserService().findUser(u1.getUserId()));
		Assert.assertEquals(u1, getUserService().disableUser(u1.getUserId()));

		/* enable user */
		u1 = getUserService().enableUser(u1.getUserId());
		Assert.assertEquals(getAdam(), u1.getPerson());
		Assert.assertEquals(List.of("USR", "PPL", "ADM"), u1.getRoles());
		Assert.assertEquals(Status.ACTIVE, u1.getStatus());
		Assert.assertEquals("adam21", u1.getUsername());
		Assert.assertEquals(u1, getUserService().findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, getUserService().findUser(u1.getUserId()));
		Assert.assertEquals(u1, getUserService().enableUser(u1.getUserId()));

		/* count users */
		Assert.assertEquals(3, getUserService().countUsers(new UsersFilter(null, null, null, null, null)));
		Assert.assertEquals(1, getUserService().countUsers(new UsersFilter(null, null, null, "adam21", null)));
		Assert.assertEquals(2, getUserService().countUsers(new UsersFilter(null, null, null, null, "PPL")));
		Assert.assertEquals(3, getUserService().countUsers(new UsersFilter(null, null, null, null, "ADM")));
		Assert.assertEquals(2, getUserService().countUsers(new UsersFilter(null, getAdam().getPersonId(), null, null, null)));
		Assert.assertEquals(1, getUserService().countUsers(new UsersFilter(null, getBob().getPersonId(), null, null, null)));
		Assert.assertEquals(3, getUserService().countUsers(new UsersFilter(new Identifier("DC"), null, null, null, null)));
		Assert.assertEquals(0, getUserService().countUsers(new UsersFilter(new Identifier("AB"), null, null, null, null)));
		Assert.assertEquals(3, getUserService().countUsers(new UsersFilter(null, null, Status.ACTIVE, null, null)));
		Assert.assertEquals(0, getUserService().countUsers(new UsersFilter(null, null, Status.INACTIVE, null, null)));

		/* find users */
		Page<User> usersPage = getUserService().findUsers(
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

		usersPage = getUserService().findUsers(
				new UsersFilter(null, null, null, "adam21", null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u1, usersPage.getContent().get(0));

		usersPage = getUserService().findUsers(
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

		usersPage = getUserService().findUsers(
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

		usersPage = getUserService().findUsers(
				new UsersFilter(null, getAdam().getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(2, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(2, usersPage.getTotalElements());
		Assert.assertEquals(2, usersPage.getContent().size());
		Assert.assertEquals(u2, usersPage.getContent().get(0));
		Assert.assertEquals(u1, usersPage.getContent().get(1));

		usersPage = getUserService().findUsers(
				new UsersFilter(null, getBob().getPersonId(), null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(1, usersPage.getNumberOfElements());
		Assert.assertEquals(1, usersPage.getTotalPages());
		Assert.assertEquals(1, usersPage.getTotalElements());
		Assert.assertEquals(1, usersPage.getContent().size());
		Assert.assertEquals(u3, usersPage.getContent().get(0));

		usersPage = getUserService().findUsers(
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

		usersPage = getUserService().findUsers(
				new UsersFilter(new Identifier("AB"), null, null, null, null),
				new Paging(1, 5, Sort.by("username")));
		Assert.assertEquals(1, usersPage.getNumber());
		Assert.assertEquals(5, usersPage.getSize());
		Assert.assertEquals(0, usersPage.getNumberOfElements());
		Assert.assertEquals(0, usersPage.getTotalPages());
		Assert.assertEquals(0, usersPage.getTotalElements());
		Assert.assertEquals(0, usersPage.getContent().size());

		usersPage = getUserService().findUsers(
				new UsersFilter(null, null, Status.ACTIVE, null, null),
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

		usersPage = getUserService().findUsers(
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
		getUserService().createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		try {
			getUserService().createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		} catch (DuplicateItemFoundException e) {
			Assert.assertEquals("Duplicate item found found: Username 'adam21'", e.getMessage());
		}
	}

	@Test
	public void testInvalidUserId() {
		try {
			getUserService().findUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			getUserService().findUserByUsername("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}

		try {
			getUserService().updateUserRoles(new Identifier("abc"), List.of(""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Role Code ''", e.getMessage());
		}

		try {
			getUserService().enableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}

		try {
			getUserService().disableUser(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: User ID 'abc'", e.getMessage());
		}
	}
}
