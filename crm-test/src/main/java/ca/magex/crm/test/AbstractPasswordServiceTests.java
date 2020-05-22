package ca.magex.crm.test;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmInitializationService;

public abstract class AbstractPasswordServiceTests {

	public abstract CrmInitializationService getInitializationService();

	public abstract CrmPasswordService getPasswordService();
	
	public abstract PasswordEncoder getPasswordEncoder();

	@Before
	public void setup() {
		getInitializationService().reset();
	}
	
	@After
	public void resetExpiry() {
		/* update expiration time to be 0, so everything is expired */
		ReflectionTestUtils.setField(getPasswordService(), "expiration", TimeUnit.DAYS.toMillis(365));		
	}

	@Test
	public void testPasswords() {
		/* generate temporary password */
		String tempPassword = getPasswordService().generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotNull(tempPassword);
		Assert.assertTrue(getPasswordService().isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(getPasswordService().isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* generate a new temporary password, and ensure it's different */
		String tempPassword2 = getPasswordService().generateTemporaryPassword("BoatyMcBoatFace");
		Assert.assertNotEquals(tempPassword, tempPassword2);
		Assert.assertTrue(getPasswordService().isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(getPasswordService().isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword));
		
		/* update our password to be a non temporary */
		getPasswordService().updatePassword("BoatyMcBoatFace", getPasswordEncoder().encode("MonkeyBrains4Lunch"));
		Assert.assertTrue(getPasswordService().verifyPassword("BoatyMcBoatFace", "MonkeyBrains4Lunch"));
		Assert.assertFalse(getPasswordService().isTempPassword("BoatyMcBoatFace"));
		Assert.assertFalse(getPasswordService().isExpiredPassword("BoatyMcBoatFace"));
		
		/* ensure the previous one is no longer verifiable */
		Assert.assertFalse(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword));
		Assert.assertFalse(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword2));
		
		Assert.assertTrue(getPasswordEncoder().matches("MonkeyBrains4Lunch", getPasswordService().getEncodedPassword("BoatyMcBoatFace")));
		
		/* update expiration time to be 0, so everything is expired */
		ReflectionTestUtils.setField(getPasswordService(), "expiration", 0L);
		String tempPassword3 = getPasswordService().generateTemporaryPassword("BoatyMcBoatFace");
		try {
			Thread.sleep(50L);
		}
		catch(InterruptedException ie) {}
		Assert.assertNotEquals(tempPassword2, tempPassword3);
		Assert.assertNotEquals(tempPassword, tempPassword3);
		Assert.assertNotEquals("BoatyMcBoatFace", tempPassword3);
		Assert.assertTrue(getPasswordService().isTempPassword("BoatyMcBoatFace"));
		Assert.assertTrue(getPasswordService().isExpiredPassword("BoatyMcBoatFace"));
		Assert.assertTrue(getPasswordService().verifyPassword("BoatyMcBoatFace", tempPassword3)); // should still be verified even if expired (Requirement to change an expired password)
	}
	
	@Test
	public void testInvalidUserName() {
		try {
			getPasswordService().updatePassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			getPasswordService().verifyPassword("abc", "hello");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			getPasswordService().isTempPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			getPasswordService().isExpiredPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
		
		try {
			getPasswordService().getEncodedPassword("abc");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Username 'abc'", e.getMessage());
		}
	}
}
