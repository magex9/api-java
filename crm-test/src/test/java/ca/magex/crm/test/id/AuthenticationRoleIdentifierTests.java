package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.AuthenticationRoleIdentifier;

/**
 * Tests for the Authentication Role Identifier
 * 
 * @author Jonny
 */
public class AuthenticationRoleIdentifierTests {
	
	@Test
	public void testContext() {
		AuthenticationRoleIdentifier authRoleId = new AuthenticationRoleIdentifier("A");
		Assert.assertEquals(AuthenticationRoleIdentifier.CONTEXT, authRoleId.getContext());
	}

	@Test
	public void testCreateAuthenticationRoleIdentifier() {
		AuthenticationRoleIdentifier authRoleId = new AuthenticationRoleIdentifier("ABC");
		Assert.assertEquals(AuthenticationRoleIdentifier.CONTEXT + "ABC", authRoleId.toString());
	}
	
	@Test
	public void testCreateNestedAuthenticationRoleIdentifier() {
		AuthenticationRoleIdentifier authRoleId = new AuthenticationRoleIdentifier("SYS/ABC");
		Assert.assertEquals(AuthenticationRoleIdentifier.CONTEXT + "SYS/ABC", authRoleId.toString());
	}
	
	@Test
	public void testLoadAuthenticationRoleIdentifier() {
		AuthenticationRoleIdentifier authRoleId = new AuthenticationRoleIdentifier("/options/authentication-roles/ABC");
		Assert.assertEquals(AuthenticationRoleIdentifier.CONTEXT + "ABC", authRoleId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new AuthenticationRoleIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /options/authentication-roles/[A-Za-z0-9/]+",  iae.getMessage());
		}
	}
}
