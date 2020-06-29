package ca.magex.crm.test.id;

import org.junit.Assert;
import org.junit.Test;

import ca.magex.crm.api.system.id.PersonIdentifier;

/**
 * Tests for the Person Identifier
 * 
 * @author Jonny
 */
public class PersonIdentifierTests {
	
	@Test
	public void testContext() {
		PersonIdentifier orgId = new PersonIdentifier("A");
		Assert.assertEquals("/persons/", orgId.getContext());
	}

	@Test
	public void testCreatePersonIdentifier() {
		PersonIdentifier orgId = new PersonIdentifier("ABC");
		Assert.assertEquals("/persons/ABC", orgId.toString());
	}
	
	@Test
	public void testLoadPersonIdentifier() {
		PersonIdentifier orgId = new PersonIdentifier("/persons/ABC");
		Assert.assertEquals("/persons/ABC", orgId.toString());
	}
	
	@Test
	public void testBadIdentifier() {
		try {
			new PersonIdentifier("/locations/ABC");
			Assert.fail("should have failed on invalid identifier");
		}
		catch(IllegalArgumentException iae) {
			Assert.assertEquals("Id '/locations/ABC' must match the pattern /persons/[A-Za-z0-9]+",  iae.getMessage());
		}
	}
}
