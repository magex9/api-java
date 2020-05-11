package ca.magex.crm.hazelcast.service;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserServiceTests {

	@Autowired private HazelcastUserService hzUserService;
	@Autowired private HazelcastInstance hzInstance;
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastUserService.HZ_USER_KEY).clear();
		/* setup some data to use for testing */
	}
	
	@Test
	public void testUsers() {		
//		hzUserService.createUser(new Identifier("P1"), "userA", Arrays.asList("", "", "", ""));
	}
}
