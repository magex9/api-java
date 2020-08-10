package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.LocationIdentifier;

/**
 * Tests for the Location Identifier
 * 
 * @author Jonny
 */
public class LocationIdentifierTests {
	
	@Test
	public void testContext() {
		LocationIdentifier orgId = new LocationIdentifier("A");
		Assert.assertEquals("/locations/", orgId.getContext());
	}

	@Test
	public void testCreateLocationIdentifier() {
		LocationIdentifier orgId = new LocationIdentifier("ABC");
		Assert.assertEquals("/locations/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadLocationIdentifier() {
		LocationIdentifier orgId = new LocationIdentifier("/locations/ABC");
		Assert.assertEquals("/locations/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new LocationIdentifier("/persons/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/persons/ABC' must match the pattern /locations/[A-Za-z0-9/]+",  iae.getMessage());
		}
	}
}
