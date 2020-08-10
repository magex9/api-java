package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.OrganizationIdentifier;

/**
 * Tests for the Organization Identifier
 * 
 * @author Jonny
 */
public class OrganizationIdentifierTests {
	
	@Test
	public void testContext() {
		OrganizationIdentifier orgId = new OrganizationIdentifier("A");
		Assert.assertEquals("/organizations/", orgId.getContext());
	}

	@Test
	public void testCreateOrganizationIdentifier() {
		OrganizationIdentifier orgId = new OrganizationIdentifier("ABC");
		Assert.assertEquals("/organizations/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadOrganizationIdentifier() {
		OrganizationIdentifier orgId = new OrganizationIdentifier("/organizations/ABC");
		Assert.assertEquals("/organizations/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new OrganizationIdentifier("/locations/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/locations/ABC' must match the pattern /organizations/[A-Za-z0-9/]+",  iae.getMessage());
		}
	}
}
