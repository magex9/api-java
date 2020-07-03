package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.BusinessRoleIdentifier;

/**
 * Tests for the Business Role Identifier
 * 
 * @author Jonny
 */
public class BusinessRoleIdentifierTests {
	
	@Test
	public void testContext() {
		BusinessRoleIdentifier orgId = new BusinessRoleIdentifier("A");
		Assert.assertEquals("/options/business-roles/", orgId.getContext());
	}

	@Test
	public void testCreateBusinessRoleIdentifier() {
		BusinessRoleIdentifier orgId = new BusinessRoleIdentifier("ABC");
		Assert.assertEquals("/options/business-roles/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadBusinessRoleIdentifier() {
		BusinessRoleIdentifier orgId = new BusinessRoleIdentifier("/options/business-roles/ABC");
		Assert.assertEquals("/options/business-roles/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new BusinessRoleIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /options/business-roles/[A-Za-z0-9/]+",  iae.getMessage());
		}
	}
}
