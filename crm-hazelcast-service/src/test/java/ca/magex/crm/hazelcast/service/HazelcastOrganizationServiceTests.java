package ca.magex.crm.hazelcast.service;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.LocationSummary;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastOrganizationServiceTests {

	@Autowired private CrmOrganizationService hzOrganizationService;
	@Autowired private HazelcastInstance hzInstance;

	@MockBean private CrmPersonService personService;
	@MockBean private CrmLocationService locationService;
	@MockBean private CrmPermissionService permissionService;

	@Before
	public void reset() {
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
		Mockito.when(locationService.findLocationSummary(new Identifier("Toronto"))).thenReturn(Mockito.mock(LocationSummary.class));
		Mockito.when(locationService.findLocationSummary(new Identifier("Ottawa"))).thenReturn(Mockito.mock(LocationSummary.class));
		Mockito.when(locationService.findLocationSummary(new Identifier("Montreal"))).thenReturn(Mockito.mock(LocationSummary.class));
		Mockito.when(personService.findPersonSummary(new Identifier("Freddy"))).thenReturn(Mockito.mock(PersonSummary.class));
		Mockito.when(personService.findPersonSummary(new Identifier("Craig"))).thenReturn(Mockito.mock(PersonSummary.class));
		Mockito.when(personService.findPersonSummary(new Identifier("Carey"))).thenReturn(Mockito.mock(PersonSummary.class));
		Mockito.when(permissionService.findGroupByCode("NHL")).thenReturn(Mockito.mock(Group.class));
		Mockito.when(permissionService.findGroupByCode("PLAYOFFS")).thenReturn(Mockito.mock(Group.class));
		Mockito.when(permissionService.findGroupByCode("ONTARIO")).thenReturn(Mockito.mock(Group.class));
		Mockito.when(permissionService.findGroupByCode("QUEBEC")).thenReturn(Mockito.mock(Group.class));
	}

	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = hzOrganizationService.createOrganization("Maple Leafs", Collections.emptyList());
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = hzOrganizationService.createOrganization("Senators", Collections.emptyList());
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = hzOrganizationService.createOrganization("Canadiens", Collections.emptyList());
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));

		/* update display name */
		o1 = hzOrganizationService.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		o1 = hzOrganizationService.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = hzOrganizationService.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		o2 = hzOrganizationService.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = hzOrganizationService.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));
		o3 = hzOrganizationService.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");

		/* update main location */
		o1 = hzOrganizationService.updateOrganizationMainLocation(o1.getOrganizationId(), new Identifier("Toronto"));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertEquals(new Identifier("Toronto"), o1.getMainLocationId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		o1 = hzOrganizationService.updateOrganizationMainLocation(o1.getOrganizationId(), new Identifier("Toronto")); // set to duplicate value
		
		o2 = hzOrganizationService.updateOrganizationMainLocation(o2.getOrganizationId(), new Identifier("Ottawa"));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertEquals(new Identifier("Ottawa"), o2.getMainLocationId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		o2 = hzOrganizationService.updateOrganizationMainLocation(o2.getOrganizationId(), new Identifier("Ottawa"));
		o2 = hzOrganizationService.updateOrganizationMainLocation(o2.getOrganizationId(), new Identifier("Ottawa"));

		o3 = hzOrganizationService.updateOrganizationMainLocation(o3.getOrganizationId(), new Identifier("Montreal"));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertEquals(new Identifier("Montreal"), o3.getMainLocationId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));
		o3 = hzOrganizationService.updateOrganizationMainLocation(o3.getOrganizationId(), new Identifier("Montreal"));
		o3 = hzOrganizationService.updateOrganizationMainLocation(o3.getOrganizationId(), new Identifier("Montreal"));

		/* update main contact */
		o1 = hzOrganizationService.updateOrganizationMainContact(o1.getOrganizationId(), new Identifier("Freddy"));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(0, o1.getGroups().size());
		Assert.assertEquals(new Identifier("Freddy"), o1.getMainContactId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		o1 = hzOrganizationService.updateOrganizationMainContact(o1.getOrganizationId(), new Identifier("Freddy")); // set to duplicate value
		o1 = hzOrganizationService.updateOrganizationMainContact(o1.getOrganizationId(), new Identifier("Freddy")); // reset to original value

		o2 = hzOrganizationService.updateOrganizationMainContact(o2.getOrganizationId(), new Identifier("Craig"));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(0, o2.getGroups().size());
		Assert.assertEquals(new Identifier("Craig"), o2.getMainContactId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));
		o2 = hzOrganizationService.updateOrganizationMainContact(o2.getOrganizationId(), new Identifier("Craig"));
		o2 = hzOrganizationService.updateOrganizationMainContact(o2.getOrganizationId(), new Identifier("Craig"));

		o3 = hzOrganizationService.updateOrganizationMainContact(o3.getOrganizationId(), new Identifier("Carey"));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(0, o3.getGroups().size());
		Assert.assertEquals(new Identifier("Carey"), o3.getMainContactId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));
		o3 = hzOrganizationService.updateOrganizationMainContact(o3.getOrganizationId(), new Identifier("Carey"));
		o3 = hzOrganizationService.updateOrganizationMainContact(o3.getOrganizationId(), new Identifier("Carey"));

		/* update groups */
		o1 = hzOrganizationService.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());
		Assert.assertEquals(new Identifier("Freddy"), o1.getMainContactId());
		Assert.assertEquals(o1, hzOrganizationService.findOrganizationDetails(o1.getOrganizationId()));
		Assert.assertEquals(o1, hzOrganizationService.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO")));
		o1 = hzOrganizationService.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o1.getGroups());
		o1 = hzOrganizationService.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());

		o2 = hzOrganizationService.updateOrganizationGroups(o2.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o2.getGroups());
		Assert.assertEquals(new Identifier("Craig"), o2.getMainContactId());
		Assert.assertEquals(o2, hzOrganizationService.findOrganizationDetails(o2.getOrganizationId()));

		o3 = hzOrganizationService.updateOrganizationGroups(o3.getOrganizationId(), List.of("NHL", "QUEBEC"));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(List.of("NHL", "QUEBEC"), o3.getGroups());
		Assert.assertEquals(new Identifier("Carey"), o3.getMainContactId());
		Assert.assertEquals(o3, hzOrganizationService.findOrganizationDetails(o3.getOrganizationId()));

		/* disable */
		OrganizationSummary os1 = hzOrganizationService.disableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, os1.getStatus());
		Assert.assertEquals(os1, hzOrganizationService.disableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, hzOrganizationService.findOrganizationSummary(os1.getOrganizationId()));

		/* enable */
		os1 = hzOrganizationService.enableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, os1.getStatus());
		Assert.assertEquals(os1, hzOrganizationService.enableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, hzOrganizationService.findOrganizationSummary(os1.getOrganizationId()));
		
		/* count organizations */
		Assert.assertEquals(1, hzOrganizationService.countOrganizations(new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE)));
		Assert.assertEquals(3, hzOrganizationService.countOrganizations(new OrganizationsFilter(null, Status.ACTIVE)));
		Assert.assertEquals(0, hzOrganizationService.countOrganizations(new OrganizationsFilter(null, Status.INACTIVE)));
		Assert.assertEquals(0, hzOrganizationService.countOrganizations(new OrganizationsFilter("Edmonton Oilers", null)));
		Assert.assertEquals(1, hzOrganizationService.countOrganizations(new OrganizationsFilter("Ottawa Senators", null)));
		
		/* find pages of organization details */
		Page<OrganizationDetails> detailsPage = hzOrganizationService.findOrganizationDetails(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = hzOrganizationService.findOrganizationDetails(
				new OrganizationsFilter(null, Status.ACTIVE), 
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(2,  detailsPage.getSize());
		Assert.assertEquals(2, detailsPage.getNumberOfElements());		
		Assert.assertEquals(2, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = hzOrganizationService.findOrganizationDetails(
				new OrganizationsFilter(null, Status.INACTIVE), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = hzOrganizationService.findOrganizationDetails(
				new OrganizationsFilter("Edmonton Oilers", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = hzOrganizationService.findOrganizationDetails(
				new OrganizationsFilter("Ottawa Senators", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10,  detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		Page<OrganizationSummary> summariesPage = hzOrganizationService.findOrganizationSummaries(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5,  summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = hzOrganizationService.findOrganizationSummaries(
				new OrganizationsFilter(null, Status.ACTIVE), 
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(2,  summariesPage.getSize());
		Assert.assertEquals(2, summariesPage.getNumberOfElements());		
		Assert.assertEquals(2, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = hzOrganizationService.findOrganizationSummaries(
				new OrganizationsFilter(null, Status.INACTIVE), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = hzOrganizationService.findOrganizationSummaries(
				new OrganizationsFilter("Edmonton Oilers", null), 
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10,  summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = hzOrganizationService.findOrganizationSummaries(
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
			hzOrganizationService.updateOrganizationDisplayName(new Identifier("abc"), "Oilers");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			hzOrganizationService.updateOrganizationMainLocation(new Identifier("abc"), new Identifier("Edmonton"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			hzOrganizationService.updateOrganizationMainContact(new Identifier("abc"), new Identifier("Mikko"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			hzOrganizationService.updateOrganizationGroups(new Identifier("abc"), Collections.emptyList());
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			hzOrganizationService.disableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			hzOrganizationService.enableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
	}
}
