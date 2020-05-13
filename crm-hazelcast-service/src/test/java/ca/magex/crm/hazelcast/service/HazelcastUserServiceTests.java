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
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserServiceTests {

	@Autowired private CrmUserService hzUserService;
	@Autowired private HazelcastInstance hzInstance;
	
	@MockBean private CrmOrganizationService organizationService;
	@MockBean private CrmPersonService personService;
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastUserService.HZ_USER_KEY).clear();
		PersonSummary adam = new PersonSummary(new Identifier("Adam"), new Identifier("DC"), Status.ACTIVE, "Adam");
		Mockito.when(personService.findPersonSummary(new Identifier("Adam"))).thenReturn(adam);
	}
	
	@Test
	public void testUsers() {		
		User u1 = hzUserService.createUser(new Identifier("Adam"), "adam21", List.of("USR", "PPL"));
		
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
