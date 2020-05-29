package ca.magex.crm.api.policies.basic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@TestInstance(Lifecycle.PER_CLASS)
public class BasicLocationPolicyTests {

	private CrmOrganizationService organizationService;
	private CrmLocationService locationService;	
	private BasicLocationPolicy policy;
	
	@BeforeAll
	public void initializeMocks() {	
		organizationService = Mockito.mock(CrmOrganizationService.class);
		locationService = Mockito.mock(CrmLocationService.class);
		policy = new BasicLocationPolicy(organizationService, locationService);
	}
	
	@BeforeEach
	public void resetMocks() {
		Mockito.reset(organizationService, locationService);
	}
	
	@Test
	public void testCanCreateLocationForOrganization() {
		Identifier orgId = new Identifier("O1");
		Identifier orgId2 = new Identifier("O2");
		Identifier orgId3 = new Identifier("O3");
		/* throw item not found for any identifier */
		BDDMockito.willThrow(new ItemNotFoundException("")).given(organizationService).findOrganizationSummary(Mockito.any());
		/* return an Active Org Summary for our specific identifier */
		BDDMockito.willReturn(new OrganizationSummary(orgId, Status.ACTIVE, "Org 1")).given(organizationService).findOrganizationSummary(orgId);
		/* return an Active Org Summary for our specific identifier */
		BDDMockito.willReturn(new OrganizationSummary(orgId2, Status.INACTIVE, "Org 2")).given(organizationService).findOrganizationSummary(orgId2);
		/* should be able to create a location for an active org */
		Assertions.assertTrue(policy.canCreateLocationForOrganization(orgId));
		/* should not be able to create a location for an inactive org */
		Assertions.assertFalse(policy.canCreateLocationForOrganization(orgId2));
		/* should not be able to create a location for an org that doesn't exist */
		Assertions.assertFalse(policy.canCreateLocationForOrganization(orgId3));
	}	
}
