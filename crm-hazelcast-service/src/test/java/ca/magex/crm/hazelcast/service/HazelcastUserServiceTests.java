package ca.magex.crm.hazelcast.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
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
		Assert.assertEquals(u1, hzUserService.findUserByUsername(u1.getUsername()));
		Assert.assertEquals(u1, hzUserService.findUser(u1.getUserId()));
		
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
	}
}
