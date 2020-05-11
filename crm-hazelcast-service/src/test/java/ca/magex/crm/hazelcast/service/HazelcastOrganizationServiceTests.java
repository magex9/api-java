package ca.magex.crm.hazelcast.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)

public class HazelcastOrganizationServiceTests {

	@Autowired private HazelcastOrganizationService hzOrganizationService;
	@Autowired private HazelcastInstance hzInstance;
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
	}
	
	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = hzOrganizationService.createOrganization("Maple Leafs");		
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroupIds().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = hzOrganizationService.createOrganization("Senators");		
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroupIds().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = hzOrganizationService.createOrganization("Canadiens");		
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroupIds().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));
		
		/* update */
		o1 = hzOrganizationService.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroupIds().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		o1 = hzOrganizationService.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = hzOrganizationService.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroupIds().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		o2 = hzOrganizationService.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = hzOrganizationService.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroupIds().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));
		o3 = hzOrganizationService.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
	}
	
	@Test
	public void testInvalidOrgId() {
		try {
			hzOrganizationService.findOrganizationDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
		
		try {
			hzOrganizationService.findOrganizationSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
		
		try {
			hzOrganizationService.updateOrganizationDisplayName(new Identifier("abc"), "Edmonton Oilers");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
	}
}
