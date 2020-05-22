package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ONTARIO;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.QUEBEC;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.StructureValidationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public abstract class AbstractOrganizationServiceTests {

	public abstract CrmInitializationService getInitializationService();

	public abstract CrmLookupService getLookupService();
	
	public abstract CrmOrganizationService getOrganizationService();
	
	public abstract CrmLocationService getLocationService();
	
	public abstract CrmPersonService getPersonService();
	
	public abstract CrmPermissionService getPermissionService();

	@Before
	public void setup() {
		getInitializationService().reset();
		getPermissionService().createGroup(new Localized("NHL", "NHL", "LNH"));
		getPermissionService().createGroup(new Localized("PLAYOFFS", "Playoffs", "Playoffs"));
		getPermissionService().createGroup(new Localized("ONTARIO", "Ontario", "Ontario"));
		getPermissionService().createGroup(new Localized("QUEBEC", "Quebec", "Québec"));		
	}
	
	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = getOrganizationService().createOrganization("Maple Leafs", List.of("NHL"));
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = getOrganizationService().createOrganization("Senators", List.of("NHL"));
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = getOrganizationService().createOrganization("Canadiens", List.of("NHL"));
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));

		/* update display name */
		o1 = getOrganizationService().updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = getOrganizationService().updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = getOrganizationService().updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));
		o3 = getOrganizationService().updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");

		/* update main location */
		Identifier torontoId = getLocationService().createLocation(
				o1.getOrganizationId(), 
				"Toronto",
				"TORONTO", 
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2")).getLocationId();
		o1 = getOrganizationService().updateOrganizationMainLocation(o1.getOrganizationId(), torontoId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertEquals(torontoId, o1.getMainLocationId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationMainLocation(o1.getOrganizationId(), torontoId); // set to duplicate value
		
		Identifier ottawaId = getLocationService().createLocation(
				o2.getOrganizationId(), 
				"Ottawa", 
				"OTTAWA", 
				new MailingAddress("1000 Palladium Dr", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K2V 1A5")).getLocationId();
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertEquals(ottawaId, o2.getMainLocationId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		o2 = getOrganizationService().updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);

		Identifier montrealId = getLocationService().createLocation(
				o3.getOrganizationId(), 
				"Montreal", 
				"MONTREAL",
				new MailingAddress("1909 Avenue des Canadiens-de-Montréal", "Montreal", QUEBEC.getCode(), CANADA.getCode(), "H4B 5G0")).getLocationId();
		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertEquals(montrealId, o3.getMainLocationId());
		Assert.assertEquals(o3, getOrganizationService().findOrganizationDetails(o3.getOrganizationId()));
		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		o3 = getOrganizationService().updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);

		/* update main contact */
		Identifier freddyId = getPersonService().createPerson(
				o1.getOrganizationId(),
				new PersonName("Mr.", "Freddy", "R", "Davis"), 
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2"), 
				COMMUNICATIONS, 
				BUSINESS_POSITION).getPersonId();		
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, getOrganizationService().findOrganizationDetails(o1.getOrganizationId()));
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // set to duplicate value
		o1 = getOrganizationService().updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // reset to original value

		Identifier craigId = getPersonService().createPerson(
				o2.getOrganizationId(), 
				new PersonName("Mr.", "Craig", null, "Phillips"), 
				new MailingAddress("1000 Palladium Dr", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K2V 1A5"), 
				COMMUNICATIONS, 
				BUSINESS_POSITION).getPersonId();
		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, getOrganizationService().findOrganizationDetails(o2.getOrganizationId()));
		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		o2 = getOrganizationService().updateOrganizationMainContact(o2.getOrganizationId(), craigId);

		Identifier careyId = getPersonService().createPerson(
				o3.getOrganizationId(), 
				new PersonName(null, "Carey", null, "Thomas"), 
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2"), 
				COMMUNICATIONS, 
				BUSINESS_POSITION).getPersonId();
		o3 = getOrganizationService().updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
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
		try {
			Assert.assertEquals(os1, getOrganizationService().disableOrganization(os1.getOrganizationId()));
			Assert.fail("Cannot disable inactive orgs");
		} catch (BadRequestException expected) { }
		Assert.assertEquals(os1, getOrganizationService().findOrganizationSummary(os1.getOrganizationId()));

		/* enable */
		os1 = getOrganizationService().enableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, os1.getStatus());
		Assert.assertEquals(os1, getOrganizationService().enableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, getOrganizationService().findOrganizationSummary(os1.getOrganizationId()));
		
		/* count organizations */
		Assert.assertEquals(1, getOrganizationService().countOrganizations(new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE)));
		Assert.assertEquals(3, getOrganizationService().countOrganizations(new OrganizationsFilter(null, Status.ACTIVE)));
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
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
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
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
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
	
	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("Org Name", List.of("GRP")).getOrganizationId();

		assertEquals("Org Name", getOrganizationService().findOrganizationDetails(organizationId).getDisplayName());
		assertEquals("Org Name", getOrganizationService().findOrganizationSummary(organizationId).getDisplayName());
		assertEquals("Org Name", getOrganizationService().findOrganizationByDisplayName("Org Name").getDisplayName());
		try {
			getOrganizationService().findOrganizationDetails(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) { }
	}
	
	@Test
	public void testCreateOrgWithMissingGroup() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("GRP")).getOrganizationId();
		assertEquals(getPermissionService().findGroup(groupId).getCode(), getOrganizationService().findOrganizationDetails(organizationId).getGroups().get(0));
		try {
			getOrganizationService().createOrganization("INVALID", List.of("MISSING"));
			fail("Should have gotten bad request");
		} catch (BadRequestException e) {
			assertEquals("Bad Request: Organization has validation errors", e.getMessage());
			assertBadRequestMessage(e, null, "error", "groups[0]", "Group does not exist: MISSING");
		}
	}
	
	@Test
	public void testFindByIdentifierOtherType() throws Exception {
		Identifier groupId = getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("GRP")).getOrganizationId();
		assertEquals("ORG", getOrganizationService().findOrganizationDetails(organizationId).getDisplayName());
		try {
			getOrganizationService().findOrganizationDetails(groupId);
			fail("Requested the wrong type");
		} catch (ItemNotFoundException expected) { }
	}
	
	@Test
	public void testOrgWithNoName() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getOrganizationService().createOrganization("", List.of("GRP")).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "displayName", "Display name is mandatory for an organization");
		}
	}
	
	@Test
	public void testOrgWithLongName() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getOrganizationService().createOrganization("The organization can only have a name with a maximum or 60 characters", List.of("GRP")).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "displayName", "Display name must be 60 characters or less");
		}
	}
	
	@Test
	public void testOrgWithNoGroup() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		try {
			getOrganizationService().createOrganization("Org", List.of()).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "groups", "Organizations must have a permission group assigned to them");
		}
	}
	
	@Test
	public void testCannotUpdateDisabledOrganization() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("GRP")).getOrganizationId();
		Identifier locationId = getLocationService().createLocation(organizationId, "Location", "LOC", CrmAsserts.MAILING_ADDRESS).getLocationId();
		Identifier personId = getPersonService().createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		getOrganizationService().disableOrganization(organizationId);
		assertEquals(Status.INACTIVE, getOrganizationService().findOrganizationSummary(organizationId).getStatus());
		
		try {
			getOrganizationService().updateOrganizationDisplayName(organizationId, "New name");
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "status", "Cannot update an organziation that is already inactive");
		}
		
		try {
			getOrganizationService().updateOrganizationMainLocation(organizationId, locationId);
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "status", "Cannot update an organziation that is already inactive");
		}
		
		try {
			getOrganizationService().updateOrganizationMainContact(organizationId, personId);
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "status", "Cannot update an organziation that is already inactive");
		}
		
		try {
			getOrganizationService().updateOrganizationGroups(organizationId, List.of("GRP"));
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "status", "Cannot update an organziation that is already inactive");
		}
	}

	@Test
	public void testCannotUpdateDisabledGroup() throws Exception {
		Identifier groupId = getPermissionService().createGroup(new Localized("A", "A", "A")).getGroupId();
		getPermissionService().createGroup(new Localized("B", "B", "B")).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("A")).getOrganizationId();
		
		getOrganizationService().updateOrganizationGroups(organizationId, List.of("B"));
		getPermissionService().disableGroup(groupId);
		
		try {
			getOrganizationService().updateOrganizationGroups(organizationId, List.of("A"));
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "groups[0]", "Group is not active: A");
		}
	}

	@Test
	public void testCannotUpdateDisabledMainLocation() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("GRP")).getOrganizationId();
		Identifier locationId = getLocationService().createLocation(organizationId, "Location", "LOC", CrmAsserts.MAILING_ADDRESS).getLocationId();
		getLocationService().disableLocation(locationId);
		
		try {
			getOrganizationService().updateOrganizationMainLocation(organizationId, locationId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "mainLocationId", "Main location must be active");
		}
	}		

	@Test
	public void testCannotUpdateDisabledMainContact() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationId = getOrganizationService().createOrganization("ORG", List.of("GRP")).getOrganizationId();
		Identifier personId = getPersonService().createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		getPersonService().disablePerson(personId);
		
		try {
			getOrganizationService().updateOrganizationMainContact(organizationId, personId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "mainContactId", "Main contact must be active");
		}
	}
	
	@Test
	public void testCreatingOrgWithMainContactFromOtherOrg() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationA = getOrganizationService().createOrganization("A", List.of("GRP")).getOrganizationId();
		Identifier organizationB = getOrganizationService().createOrganization("B", List.of("GRP")).getOrganizationId();
		Identifier personB = getPersonService().createPerson(organizationB, PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		
		try {
			getOrganizationService().updateOrganizationMainContact(organizationA, personB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "error", "mainContactId", "Main contact organization has invalid referential integrity");
		}
	}
	
	@Test
	public void testCreatingOrgWithMainLocationFromOtherOrg() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		Identifier organizationA = getOrganizationService().createOrganization("A", List.of("GRP")).getOrganizationId();
		Identifier organizationB = getOrganizationService().createOrganization("B", List.of("GRP")).getOrganizationId();
		Identifier locationB = getLocationService().createLocation(organizationB, "Location", "B", CrmAsserts.MAILING_ADDRESS).getLocationId();
		
		try {
			getOrganizationService().updateOrganizationMainLocation(organizationA, locationB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "error", "mainLocationId", "Main location organization has invalid referential integrity");
		}
	}
	
	@Test
	public void testCreatingOrgsWithInvalidStatuses() throws Exception {
		getPermissionService().createGroup(GROUP).getGroupId();
		StructureValidationService validation = new StructureValidationService(getLookupService(), getPermissionService(), getOrganizationService(), getLocationService(), getPersonService());
		try {
			validation.validate(new OrganizationDetails(new Identifier("org"), Status.INACTIVE, "org name", null, null, List.of("GRP")));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("org"), "error", "status", "Cannot create a new organization that is inactive");
		}
		try {
			validation.validate(new OrganizationDetails(new Identifier("org"), null, "org name", null, null, List.of("GRP")));
			fail("Should fail validation");
		} catch(BadRequestException e) {
			assertBadRequestMessage(e, new Identifier("org"), "error", "status", "Status is mandatory for an organization");
		}
	}
	
}
