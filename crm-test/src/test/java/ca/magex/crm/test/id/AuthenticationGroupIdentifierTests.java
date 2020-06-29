package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;

/**
 * Tests for the Authentication Group Identifier
 * 
 * @author Jonny
 */
public class AuthenticationGroupIdentifierTests {
	
	@Test
	public void testContext() {
		AuthenticationGroupIdentifier orgId = new AuthenticationGroupIdentifier("A");
		Assert.assertEquals("/options/authenticationGroups/", orgId.getContext());
	}

	@Test
	public void testCreateAuthenticationGroupIdentifier() {
		AuthenticationGroupIdentifier orgId = new AuthenticationGroupIdentifier("ABC");
		Assert.assertEquals("/options/authenticationGroups/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadAuthenticationGroupIdentifier() {
		AuthenticationGroupIdentifier orgId = new AuthenticationGroupIdentifier("/options/authenticationGroups/ABC");
		Assert.assertEquals("/options/authenticationGroups/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new AuthenticationGroupIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /options/authenticationGroups/[A-Za-z0-9]+",  iae.getMessage());
		}
	}
}
