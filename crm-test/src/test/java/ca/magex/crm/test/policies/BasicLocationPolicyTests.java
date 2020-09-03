package ca.magex.crm.test.policies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.basic.BasicLocationPolicy;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.OrganizationIdentifier;

public class BasicLocationPolicyTests {

	private CrmOrganizationService organizationService;
	private CrmLocationService locationService;
	private BasicLocationPolicy policy;

	@Before
	public void initializeMocks() {
		organizationService = Mockito.mock(CrmOrganizationService.class);
		locationService = Mockito.mock(CrmLocationService.class);
		policy = new BasicLocationPolicy(organizationService, locationService);
	}

	@Test
	public void testCanCreateLocationForOrganization() {
		OrganizationIdentifier orgId = new OrganizationIdentifier("O1");
		OrganizationIdentifier orgId2 = new OrganizationIdentifier("O2");
		OrganizationIdentifier orgId3 = new OrganizationIdentifier("O3");
		/* throw item not found for any identifier */
		BDDMockito.willThrow(new ItemNotFoundException("")).given(organizationService).findOrganizationSummary(Mockito.any());
		/* return an Active Org Summary for our specific identifier */
		BDDMockito.willReturn(new OrganizationSummary(orgId, Status.ACTIVE, "Org 1", null)).given(organizationService).findOrganizationSummary(orgId);
		/* return an Active Org Summary for our specific identifier */
		BDDMockito.willReturn(new OrganizationSummary(orgId2, Status.INACTIVE, "Org 2", null)).given(organizationService).findOrganizationSummary(orgId2);
		/* should be able to create a location for an active org */
		Assert.assertTrue(policy.canCreateLocationForOrganization(orgId));
		/* should not be able to create a location for an inactive org */
		Assert.assertFalse(policy.canCreateLocationForOrganization(orgId2));
		/* should not be able to create a location for an org that doesn't exist */
		try {
			policy.canCreateLocationForOrganization(orgId3);
			Assert.fail("Item was not found");
		} catch (ItemNotFoundException expected) { } 
	}

	@Test
	public void testCanViewLocation() {
		// TODO
	}

	@Test
	public void testCanUpdateLocation() {
		// TODO
	}

	@Test
	public void testCanEnableLocation() {
		// TODO
	}

	@Test
	public void testCanDisableLocation() {
		// TODO
	}
}
