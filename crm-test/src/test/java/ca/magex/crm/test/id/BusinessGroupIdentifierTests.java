package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.BusinessGroupIdentifier;

/**
 * Tests for the Business Group Identifier
 * 
 * @author Jonny
 */
public class BusinessGroupIdentifierTests {
	
	@Test
	public void testContext() {
		BusinessGroupIdentifier orgId = new BusinessGroupIdentifier("A");
		Assert.assertEquals("/options/business-groups/", orgId.getContext());
	}

	@Test
	public void testCreateBusinessGroupIdentifier() {
		BusinessGroupIdentifier orgId = new BusinessGroupIdentifier("ABC");
		Assert.assertEquals("/options/business-groups/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadBusinessGroupIdentifier() {
		BusinessGroupIdentifier orgId = new BusinessGroupIdentifier("/options/business-groups/ABC");
		Assert.assertEquals("/options/business-groups/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new BusinessGroupIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /options/business-groups/[A-Za-z0-9/]+",  iae.getMessage());
		}
	}
}
