package ca.magex.crm.test;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractOrganizationServiceTests {

	public abstract CrmOrganizationService getOrganizationService();
	
	public abstract CrmLocationService getLocationService();
	
	public abstract CrmPersonService getPersonService();
	
	public abstract CrmPermissionService getPermissionService();

	public abstract void reset();
	
	private Identifier nhlId;

	private Identifier torontoId;

	private Identifier ottawaId;

	private Identifier montrealId;
	
	private Identifier freddyId;
	
	private Identifier craigId;
	
	private Identifier careyId;

	@Before
	public void setup() {
		reset();
		getPermissionService().createGroup("NHL", new Localized("NHL", "LNH"));
		getPermissionService().createGroup("PLAYOFFS", new Localized("Playoffs", "Playoffs"));
		getPermissionService().createGroup("ONTARIO", new Localized("Ontario", "Ontario"));
		getPermissionService().createGroup("QUEBEC", new Localized("Quebec", "Québec"));
		nhlId = getOrganizationService().createOrganization("National Hockey League", List.of("NHL")).getOrganizationId();
		torontoId = getLocationService().createLocation(nhlId, "Toronto", "TORONTO", null).getLocationId();
		ottawaId = getLocationService().createLocation(nhlId, "Toronto", "TORONTO", null).getLocationId();
		montrealId = getLocationService().createLocation(nhlId, "Toronto", "TORONTO", null).getLocationId();
		freddyId = getPersonService().createPerson(nhlId, new PersonName("Mr.", "Freddy", "R", "Davis"), null, null, null).getPersonId();
		craigId = getPersonService().createPerson(nhlId, new PersonName("Mr.", "Craig", null, "Phillips"), null, null, null).getPersonId();
		careyId = getPersonService().createPerson(nhlId, new PersonName(null, "Carey", null, "Thomas"), null, null, null).getPersonId();
	}
	
	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = getOrganizationService().createOrganization("Maple Leafs", Collections.emptyList());
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = getOrganizationService().createOrganization("Senators", Collections.emptyList());
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = getOrganizationService().createOrganization("Canadiens", Collections.emptyList());
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));

		/* update display name */
		o1 = getOrganizationService().updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = getOrganizationService().updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = getOrganizationService().updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));
		o3 = getOrganizationService().updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");

		/* update main location */
		o1 = getOrganizationService().updateOrganizationMainLocation(o1.getOrganizationId(), torontoId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertEquals(torontoId, o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationMainLocation(o1.getOrganizationId(), torontoId); // set to duplicate value
		
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertEquals(ottawaId, o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);

		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertEquals(montrealId, o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));
		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);

		/* update main contact */
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // set to duplicate value
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // reset to original value

		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);

		o3 = getOrganizationService().updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertEquals(careyId, o3.getMainContactId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));
		o3 = getOrganizationService().updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		o3 = getOrganizationService().updateOrganizationMainContact(o3.getOrganizationId(), careyId);

		/* update groups */
		o1 = getOrganizationService().updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		Assert.assertEquals(o1, getOrganizationService().updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO")));
		o1 = getOrganizationService().updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o1.getGroups());
		o1 = getOrganizationService().updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());

		o2 = getOrganizationService().updateOrganizationGroups(o2.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o2.getGroups());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));

		o3 = getOrganizationService().updateOrganizationGroups(o3.getOrganizationId(), List.of("NHL", "QUEBEC"));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(List.of("NHL", "QUEBEC"), o3.getGroups());
		Assert.assertEquals(careyId, o3.getMainContactId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));

		/* disable */
		OrganizationSummary os1 = getOrganizationService().disableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, os1.getStatus());
		Assert.assertEquals(os1, getOrganizationService().disableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, getOrganizationService().findOrganizationSummary(os1.getOrganizationId()));

		/* enable */
		os1 = getOrganizationService().enableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, os1.getStatus());
		Assert.assertEquals(os1, getOrganizationService().enableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, getOrganizationService().findOrganizationSummary(os1.getOrganizationId()));
		
		/* count organizations */
		Assert.assertEquals(1, getOrganizationService().countOrganizations(new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE)));
		Assert.assertEquals(4, getOrganizationService().countOrganizations(new OrganizationsFilter(null, Status.ACTIVE)));
		Assert.assertEquals(0, getOrganizationService().countOrganizations(new OrganizationsFilter(null, Status.INACTIVE)));
		Assert.assertEquals(0, getOrganizationService().countOrganizations(new OrganizationsFilter("Edmonton Oilers", null)));
		Assert.assertEquals(1, getOrganizationService().countOrganizations(new OrganizationsFilter("Ottawa Senators", null)));
		
		/* find pages of organization details */
		Page<OrganizationDetails> detailsPage = getOrganizationService().findOrganizationDetails(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = getOrganizationService().findOrganizationDetails(
				new OrganizationsFilter(null, Status.ACTIVE), 
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(2,  detailsPage.getSize());
		Assert.assertEquals(2, detailsPage.getNumberOfElements());		
		Assert.assertEquals(2, detailsPage.getTotalPages());
		Assert.assertEquals(4, detailsPage.getTotalElements());
		
		detailsPage = getOrganizationService().findOrganizationDetails(
				new OrganizationsFilter(null, Status.INACTIVE), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = getOrganizationService().findOrganizationDetails(
				new OrganizationsFilter("Edmonton Oilers", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = getOrganizationService().findOrganizationDetails(
				new OrganizationsFilter("Ottawa Senators", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		Page<OrganizationSummary> summariesPage = getOrganizationService().findOrganizationSummaries(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = getOrganizationService().findOrganizationSummaries(
				new OrganizationsFilter(null, Status.ACTIVE), 
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(2,  summariesPage.getSize());
		Assert.assertEquals(2, summariesPage.getNumberOfElements());		
		Assert.assertEquals(2, summariesPage.getTotalPages());
		Assert.assertEquals(4, summariesPage.getTotalElements());
		
		summariesPage = getOrganizationService().findOrganizationSummaries(
				new OrganizationsFilter(null, Status.INACTIVE), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = getOrganizationService().findOrganizationSummaries(
				new OrganizationsFilter("Edmonton Oilers", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = getOrganizationService().findOrganizationSummaries(
				new OrganizationsFilter("Ottawa Senators", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
	}

	@Test
	public void testInvalidOrgId() {
		try {
			getOrganizationService().findOrganizationDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().findOrganizationSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().updateOrganizationDisplayName(new Identifier("abc"), "Oilers");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().updateOrganizationMainLocation(new Identifier("abc"), new Identifier("Edmonton"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().updateOrganizationMainContact(new Identifier("abc"), new Identifier("Mikko"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().updateOrganizationGroups(new Identifier("abc"), Collections.emptyList());
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().disableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			getOrganizationService().enableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
	}
}
