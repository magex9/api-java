package ca.magex.crm.test;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.exceptions.ItemNotFoundException;

public abstract class AbstractPasswordServiceTests {

	@Autowired
	protected Crm crm;

	@Autowired
	protected CrmPasswordService passwords;
	
	@Before
	public void setup() {
		crm.reset();
	}
	
	@After
	public void resetExpiry() {
		/* update expiration time to be 0, so everything is expired */
		ReflectionTestUtils.setField(passwords, "expiration", TimeUnit.DAYS.toMillis(365));		
	}

	@Test
	public void testPasswords() {
		/* generate temporary password */
		String tempPassword = passwords.generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotNull(tempPassword);
		Assert.assertTrue(passwords.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(passwords.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(passwords.verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* generate a new temporary password, and ensure it's different */
		String tempPassword2 = passwords.generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotEquals(tempPassword, tempPassword2);
		Assert.assertTrue(passwords.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(passwords.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(passwords.verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(passwords.verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* update our password to be a non temporary */
		passwords.updatePassword("BoatyMcBoatFace", passwords.encodePassword("MonkeyBrains4Lunch"));
		Assert.assertTrue(passwords.verifyPassword("BoatyMcBoatFace", "MonkeyBrains4Lunch"));
		Assert.assertFalse(passwords.isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(passwords.isExpiredPassword("BoatyMcBoatFace"));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(passwords.verifyPassword("BoatyMcBoatFace", tempPassword));
		Assert.assertFalse(passwords.verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		//Assert.assertTrue(passwords.matches("MonkeyBrains4Lunch", passwords.getEncodedPassword("BoatyMcBoatFace")));
		
		/* update expiration time to be 0, so everything is expired */
		ReflectionTestUtils.setField(passwords, "expiration", 0L);
		String tempPassword3 = passwords.generateTemporaryPassword("BoatyMcBoatFace");
		try {
			Thread.sleep(50L);
		}
		catch(InterruptedException ie) {}
		Assert.assertNotEquals(tempPassword2, tempPassword3);
		Assert.assertNotEquals(tempPassword, tempPassword3);
		Assert.assertNotEquals("BoatyMcBoatFace", tempPassword3);
		Assert.assertTrue(passwords.isTempPassword("BoatyMcBoatFace"));
		Assert.assertTrue(passwords.isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(passwords.verifyPassword("BoatyMcBoatFace", tempPassword3)); // should still be verified even if expired (Requirement to change an expired password)
	}
	
	@Test
	public void testInvalidUserName() {
		try {
			passwords.updatePassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			passwords.verifyPassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			passwords.isTempPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			passwords.isExpiredPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			passwords.getEncodedPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
	}
}
