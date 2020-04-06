package ca.magex.crm.amnesia.services;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.LocationsFilter;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmServicesTestSuite {
	
	@Autowired private CrmOrganizationService organizationService;
	
	@Autowired private CrmLocationService locationService;

	public void runAllTests() {
		Identifier orgIdentifier = runNewOrgTests();
		runNewLocationTests(orgIdentifier);
	}

	/**
	 * Runs a series of tests for creating a new organization and manipulating the organization
	 * 
	 * @return
	 */
	private Identifier runNewOrgTests() {
		/* get initial org count */
		long orgCount = organizationService.countOrganizations(new OrganizationsFilter());		
		
		/* create and verify new organization */
		OrganizationDetails orgDetails = organizationService.createOrganization("ABC");
		Identifier orgId = orgDetails.getOrganizationId();
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", null);		
		
		/* verify that we our org count incremented by 1 */
		Assert.assertEquals(orgCount + 1, organizationService.countOrganizations(new OrganizationsFilter()));
		
		/* create and verify new location for organization */
		MailingAddress address = new MailingAddress("54 fifth street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");
		long locCount = locationService.countLocations(new LocationsFilter());
		LocationDetails locDetails = locationService.createLocation(
				orgId, 
				"HeadQuarters", 
				"HQ", 
				address);
		Assert.assertEquals(locCount + 1, locationService.countLocations(new LocationsFilter()));
		Identifier locId = locDetails.getLocationId();
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, "HQ", "HeadQuarters", address);		
		
		/* set and verify organization main location */
		orgDetails = organizationService.updateOrganizationMainLocation(orgDetails.getOrganizationId(), locDetails.getLocationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, "ABC", locId);		
		
		/* update and verify organization name */ 
		String newName = "ABC" + System.currentTimeMillis();
		orgDetails = organizationService.updateOrganizationName(orgDetails.getOrganizationId(), newName);		
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);		
		
		/* disable and verify organization */
		OrganizationSummary orgSummary = organizationService.disableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.INACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.INACTIVE, newName, locId);
		
		/* enable and verify organization */
		orgSummary = organizationService.enableOrganization(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgSummary = organizationService.findOrganizationSummary(orgDetails.getOrganizationId());
		verifyOrgSummary(orgSummary, orgId, Status.ACTIVE, newName);
		
		orgDetails = organizationService.findOrganizationDetails(orgDetails.getOrganizationId());
		verifyOrgDetails(orgDetails, orgId, Status.ACTIVE, newName, locId);
		
		/* validate details paging with 1 match on name filter */
		Page<OrganizationDetails> orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(1, orgDetailsPage.getTotalPages());
		Assert.assertEquals(1, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(1, orgDetailsPage.getTotalElements());
		Assert.assertEquals(1, orgDetailsPage.getContent().size());
		Assert.assertEquals(orgDetails, orgDetailsPage.getContent().get(0));
		
		/* validate details paging with no match on name filter */
		orgDetailsPage = organizationService.findOrganizationDetails(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgDetailsPage.getNumber());
		Assert.assertEquals(0, orgDetailsPage.getTotalPages());
		Assert.assertEquals(0, orgDetailsPage.getNumberOfElements());
		Assert.assertEquals(0, orgDetailsPage.getTotalElements());
		Assert.assertEquals(0, orgDetailsPage.getContent().size());
		
		/* validate summary paging with 1 match on name filter */
		Page<OrganizationSummary> orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName, Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(1, orgSummaryPage.getTotalPages());
		Assert.assertEquals(1, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(1, orgSummaryPage.getTotalElements());
		Assert.assertEquals(1, orgSummaryPage.getContent().size());
		Assert.assertEquals(orgSummary, orgSummaryPage.getContent().get(0));
		
		/* validate summary paging with no match on name filter */
		orgSummaryPage = organizationService.findOrganizationSummaries(new OrganizationsFilter(newName + "00", Status.ACTIVE), new Paging(1, 5, Sort.by(Direction.ASC, "displayName")));
		Assert.assertEquals(1, orgSummaryPage.getNumber());
		Assert.assertEquals(0, orgSummaryPage.getTotalPages());
		Assert.assertEquals(0, orgSummaryPage.getNumberOfElements());
		Assert.assertEquals(0, orgSummaryPage.getTotalElements());
		Assert.assertEquals(0, orgSummaryPage.getContent().size());
		
		return orgDetails.getOrganizationId();
	}

	/**
	 * runs a series of tests for manipulating a location
	 * 
	 * @param orgId
	 */
	private void runNewLocationTests(Identifier orgId) {
		OrganizationDetails orgDetails = organizationService.findOrganizationDetails(orgId);
		Identifier locId = orgDetails.getMainLocationId();
		
		/* retrieve the location details */
		final LocationDetails originalLocationDetails = locationService.findLocationDetails(locId);
		
		/* disable location and verify the result */
		LocationSummary locSummary = locationService.disableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		LocationDetails locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.INACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
				
		/* enable location and verify the result */
		locSummary = locationService.enableLocation(orgDetails.getMainLocationId());
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName());
		
		locDetails = locationService.findLocationDetails(locId);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), originalLocationDetails.getDisplayName(), originalLocationDetails.getAddress());
		
		/* update and verify the location name */
		String newName = originalLocationDetails.getDisplayName() + "XXX";
		locDetails = locationService.updateLocationName(locId, newName);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, originalLocationDetails.getAddress());
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
		
		/* update and verify the location address */
		MailingAddress newAddress = new MailingAddress("55 second street", "Toronto", "ON", new Country("CA", "Canada", "Canada"), "T5R5X3");
		locDetails = locationService.updateLocationAddress(locId, newAddress);
		verifyLocationDetails(locDetails, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName, newAddress);
		
		locSummary = locationService.findLocationSummary(locId);
		verifyLocationSummary(locSummary, orgId, locId, Status.ACTIVE, originalLocationDetails.getReference(), newName);
	}

	
	private void verifyOrgDetails(OrganizationDetails orgDetails, Identifier orgId, Status status, String displayName, Identifier mainLocationIdentifier) {
		Assert.assertNotNull(orgDetails.getOrganizationId());
		Assert.assertEquals(orgId, orgDetails.getOrganizationId());
		Assert.assertEquals(status, orgDetails.getStatus());
		Assert.assertEquals(displayName, orgDetails.getDisplayName());
		Assert.assertEquals(mainLocationIdentifier, orgDetails.getMainLocationId());
	}
	
	private void verifyOrgSummary(OrganizationSummary orgSummary, Identifier orgId, Status status, String displayName) {
		Assert.assertNotNull(orgSummary.getOrganizationId());
		Assert.assertEquals(orgId, orgSummary.getOrganizationId());
		Assert.assertEquals(status, orgSummary.getStatus());
		Assert.assertEquals(displayName, orgSummary.getDisplayName());	
	}
	
	private void verifyLocationDetails(LocationDetails locDetails, Identifier orgId, Identifier locId, Status status, String reference, String displayName, MailingAddress address) {
		Assert.assertNotNull(locDetails.getLocationId());		
		Assert.assertEquals(orgId, locDetails.getOrganizationId());
		Assert.assertEquals(locId, locDetails.getLocationId());
		Assert.assertEquals(reference, locDetails.getReference());
		Assert.assertEquals(displayName, locDetails.getDisplayName());
		Assert.assertEquals(address, locDetails.getAddress());
		Assert.assertEquals(status, locDetails.getStatus());
	}
	
	private void verifyLocationSummary(LocationSummary locSummary, Identifier orgId, Identifier locId, Status status, String reference, String displayName) {
		Assert.assertNotNull(locSummary.getLocationId());		
		Assert.assertEquals(orgId, locSummary.getOrganizationId());
		Assert.assertEquals(locId, locSummary.getLocationId());
		Assert.assertEquals(reference, locSummary.getReference());
		Assert.assertEquals(displayName, locSummary.getDisplayName());
		Assert.assertEquals(status, locSummary.getStatus());
	}
}
