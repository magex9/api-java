package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Tests for the User Identifier
 * 
 * @author Jonny
 */
public class UserIdentifierTests {
	
	@Test
	public void testContext() {
		UserIdentifier orgId = new UserIdentifier("A");
		Assert.assertEquals("/users/", orgId.getContext());
	}

	@Test
	public void testCreateUserIdentifier() {
		UserIdentifier orgId = new UserIdentifier("ABC");
		Assert.assertEquals("/users/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadUserIdentifier() {
		UserIdentifier orgId = new UserIdentifier("/users/ABC");
		Assert.assertEquals("/users/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new UserIdentifier("/locations/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/locations/ABC' must match the pattern /users/[A-Za-z0-9]+",  iae.getMessage());
		}
	}
}
