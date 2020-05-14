package ca.magex.crm.hazelcast.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPasswordServiceTests {

	@Autowired private CrmPasswordService hzPasswordService;
	@Autowired private HazelcastInstance hzInstance;
	@Autowired private PasswordEncoder passwordEncoder;

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastPasswordService.HZ_PASSWORDS_KEY).clear();
	}

	@Test
	public void testPasswords() {
		/* generate temporary password */
		String tempPassword = hzPasswordService.generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotNull(tempPassword);
		Assert.assertTrue(hzPasswordService.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(hzPasswordService.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* generate a new temporary password, and ensure it's different */
		String tempPassword2 = hzPasswordService.generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotEquals(tempPassword, tempPassword2);
		Assert.assertTrue(hzPasswordService.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(hzPasswordService.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* update our password to be a non temporary */
		hzPasswordService.updatePassword("BoatyMcBoatFace", passwordEncoder.encode("MonkeyBrains4Lunch"));
		Assert.assertTrue(hzPasswordService.verifyPassword("BoatyMcBoatFace", "MonkeyBrains4Lunch"));
		Assert.assertFalse(hzPasswordService.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(hzPasswordService.isExpiredPassword("BoatyMcBoatFace"));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword));
		Assert.assertFalse(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		Assert.assertTrue(passwordEncoder.matches("MonkeyBrains4Lunch", hzPasswordService.getEncodedPassword("BoatyMcBoatFace")));
		
		/* update expiration time to be 0, so everything is expired */
		ReflectionTestUtils.setField(hzPasswordService, "expiration", 0L);
		String tempPassword3 = hzPasswordService.generateTemporaryPassword("BoatyMcBoatFace");
		try {
			Thread.sleep(50L);
		}
		catch(InterruptedException ie) {}
		Assert.assertNotEquals(tempPassword2, tempPassword3);
		Assert.assertNotEquals(tempPassword, tempPassword3);
		Assert.assertNotEquals("BoatyMcBoatFace", tempPassword3);
		Assert.assertTrue(hzPasswordService.isTempPassword("BoatyMcBoatFace"));
		Assert.assertTrue(hzPasswordService.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertFalse(hzPasswordService.verifyPassword("BoatyMcBoatFace", tempPassword3)); // verified false because it's expired
	}
	
	@Test
	public void testInvalidUserName() {
		try {
			hzPasswordService.updatePassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			hzPasswordService.verifyPassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			hzPasswordService.isTempPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			hzPasswordService.isExpiredPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			hzPasswordService.getEncodedPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
	}
}
