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
		AuthenticationRoleIdentifier orgId = new AuthenticationRoleIdentifier("A");
		Assert.assertEquals("/options/authenticationRoles/", orgId.getContext());
	}

	@Test
	public void testCreateAuthenticationRoleIdentifier() {
		AuthenticationRoleIdentifier orgId = new AuthenticationRoleIdentifier("ABC");
		Assert.assertEquals("/options/authenticationRoles/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadAuthenticationRoleIdentifier() {
		AuthenticationRoleIdentifier orgId = new AuthenticationRoleIdentifier("/options/authenticationRoles/ABC");
		Assert.assertEquals("/options/authenticationRoles/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new AuthenticationRoleIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /options/authenticationRoles/[A-Za-z0-9]+",  iae.getMessage());
		}
	}
}
