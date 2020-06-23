package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.GROUP;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ONTARIO;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.QUEBEC;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.assertBadRequestMessage;
import static ca.magex.crm.test.CrmAsserts.assertMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;

public abstract class AbstractOrganizationServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected BasicAuthenticationService auth;
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth.login("admin", "admin");
		crm.createGroup(new Localized("NHL", "NHL", "LNH"));
		crm.createGroup(new Localized("PLAYOFFS", "Playoffs", "Playoffs"));
		crm.createGroup(new Localized("ONTARIO", "Ontario", "Ontario"));
		crm.createGroup(new Localized("QUEBEC", "Quebec", "Québec"));
	}
	
	@After
	public void cleanup() {
		auth.logout();
	}

	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = crm.createOrganization("Maple Leafs", List.of("NHL"));
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = crm.createOrganization("Senators", List.of("NHL"));
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = crm.createOrganization("Canadiens", List.of("NHL"));
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));

		/* update display name */
		o1 = crm.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = crm.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = crm.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");

		/* update main location */
		Identifier torontoId = crm.createLocation(
				o1.getOrganizationId(),
				"TORONTO",
				"Toronto",
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2")).getLocationId();
		o1 = crm.updateOrganizationMainLocation(o1.getOrganizationId(), torontoId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertEquals(torontoId, o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationMainLocation(o1.getOrganizationId(), torontoId); // set to duplicate value

		Identifier ottawaId = crm.createLocation(
				o2.getOrganizationId(),
				"OTTAWA",
				"Ottawa",
				new MailingAddress("1000 Palladium Dr", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K2V 1A5")).getLocationId();
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertEquals(ottawaId, o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);

		Identifier montrealId = crm.createLocation(
				o3.getOrganizationId(),
				"MONTREAL",
				"Montreal",
				new MailingAddress("1909 Avenue des Canadiens-de-Montréal", "Montreal", QUEBEC.getCode(), CANADA.getCode(), "H4B 5G0")).getLocationId();
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertEquals(montrealId, o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);

		/* update main contact */
		Identifier freddyId = crm.createPerson(
				o1.getOrganizationId(),
				new PersonName("1", "Freddy", "R", "Davis"),
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2"),
				WORK_COMMUNICATIONS,
				BUSINESS_POSITION).getPersonId();
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getGroups().size());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // set to duplicate value
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // reset to original value

		Identifier craigId = crm.createPerson(
				o2.getOrganizationId(),
				new PersonName("3", "Craig", null, "Phillips"),
				new MailingAddress("1000 Palladium Dr", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K2V 1A5"),
				WORK_COMMUNICATIONS,
				BUSINESS_POSITION).getPersonId();
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getGroups().size());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);

		Identifier careyId = crm.createPerson(
				o3.getOrganizationId(),
				new PersonName(null, "Carey", null, "Thomas"),
				new MailingAddress("40 Bay St", "Toronto", ONTARIO.getCode(), CANADA.getCode(), "M5J 2X2"),
				WORK_COMMUNICATIONS,
				BUSINESS_POSITION).getPersonId();
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getGroups().size());
		Assert.assertEquals(careyId, o3.getMainContactId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);

		/* update groups */
		o1 = crm.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		Assert.assertEquals(o1, crm.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO")));
		o1 = crm.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o1.getGroups());
		o1 = crm.updateOrganizationGroups(o1.getOrganizationId(), List.of("NHL", "PLAYOFFS", "ONTARIO"));
		Assert.assertEquals(List.of("NHL", "PLAYOFFS", "ONTARIO"), o1.getGroups());

		o2 = crm.updateOrganizationGroups(o2.getOrganizationId(), List.of("NHL", "ONTARIO"));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(List.of("NHL", "ONTARIO"), o2.getGroups());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));

		o3 = crm.updateOrganizationGroups(o3.getOrganizationId(), List.of("NHL", "QUEBEC"));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(List.of("NHL", "QUEBEC"), o3.getGroups());
		Assert.assertEquals(careyId, o3.getMainContactId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));

		/* disable */
		OrganizationSummary os1 = crm.disableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, os1.getStatus());
		Assert.assertEquals(os1, crm.disableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, crm.findOrganizationSummary(os1.getOrganizationId()));

		/* enable */
		os1 = crm.enableOrganization(o1.getOrganizationId());
		Assert.assertEquals("Toronto Maple Leafs", os1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, os1.getStatus());
		Assert.assertEquals(os1, crm.enableOrganization(os1.getOrganizationId()));
		Assert.assertEquals(os1, crm.findOrganizationSummary(os1.getOrganizationId()));

		/* count organizations */
		Assert.assertEquals(1, crm.countOrganizations(new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE, "NHL")));
		Assert.assertEquals(4, crm.countOrganizations(new OrganizationsFilter(null, Status.ACTIVE, null)));
		Assert.assertEquals(0, crm.countOrganizations(new OrganizationsFilter(null, Status.INACTIVE, null)));
		Assert.assertEquals(0, crm.countOrganizations(new OrganizationsFilter("Edmonton Oilers", null, null)));
		Assert.assertEquals(1, crm.countOrganizations(new OrganizationsFilter("Ottawa Senators", null, null)));

		/* find pages of organization details */
		Page<OrganizationDetails> detailsPage = crm.findOrganizationDetails(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE, null),
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());

		detailsPage = crm.findOrganizationDetails(
				new OrganizationsFilter(null, Status.ACTIVE, null),
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(2, detailsPage.getSize());
		Assert.assertEquals(2, detailsPage.getNumberOfElements());
		Assert.assertEquals(2, detailsPage.getTotalPages());
		Assert.assertEquals(4, detailsPage.getTotalElements());

		detailsPage = crm.findOrganizationDetails(
				new OrganizationsFilter(null, Status.INACTIVE, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());

		detailsPage = crm.findOrganizationDetails(
				new OrganizationsFilter("Edmonton Oilers", null, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());

		detailsPage = crm.findOrganizationDetails(
				new OrganizationsFilter("Ottawa Senators", null, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(10, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());

		Page<OrganizationSummary> summariesPage = crm.findOrganizationSummaries(
				new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE, null),
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());

		summariesPage = crm.findOrganizationSummaries(
				new OrganizationsFilter(null, Status.ACTIVE, null),
				new Paging(1, 2, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(2, summariesPage.getSize());
		Assert.assertEquals(2, summariesPage.getNumberOfElements());
		Assert.assertEquals(2, summariesPage.getTotalPages());
		Assert.assertEquals(4, summariesPage.getTotalElements());

		summariesPage = crm.findOrganizationSummaries(
				new OrganizationsFilter(null, Status.INACTIVE, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10, summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());

		summariesPage = crm.findOrganizationSummaries(
				new OrganizationsFilter("Edmonton Oilers", null, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10, summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());

		summariesPage = crm.findOrganizationSummaries(
				new OrganizationsFilter("Ottawa Senators", null, null),
				new Paging(1, 10, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(10, summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
	}

	@Test
	public void testInvalidOrgId() {
		try {
			crm.findOrganizationDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.findOrganizationSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationDisplayName(new Identifier("abc"), "Oilers");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationMainLocation(new Identifier("abc"), new Identifier("Edmonton"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationMainContact(new Identifier("abc"), new Identifier("Mikko"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationGroups(new Identifier("abc"), Collections.emptyList());
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.disableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}

		try {
			crm.enableOrganization(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID 'abc'", e.getMessage());
		}
	}

	@Test
	public void testWrongIdentifiers() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("Org Name", List.of("GRP")).getOrganizationId();

		assertEquals("Org Name", crm.findOrganizationDetails(organizationId).getDisplayName());
		assertEquals("Org Name", crm.findOrganizationSummary(organizationId).getDisplayName());
		try {
			crm.findOrganizationDetails(groupId);
			fail("Not a valid identifier");
		} catch (ItemNotFoundException e) {
		}
	}

	@Test
	public void testCreateOrgWithMissingGroup() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("ORG", List.of("GRP")).getOrganizationId();
		assertEquals(crm.findGroup(groupId).getCode(), crm.findOrganizationDetails(organizationId).getGroups().get(0));
		try {
			crm.createOrganization("INVALID", List.of("MISSING"));
			fail("Should have gotten bad request");
		} catch (BadRequestException e) {
			assertEquals("Bad Request: Organization has validation errors", e.getMessage());
			assertBadRequestMessage(e, null, "error", "groups[0]", "Group does not exist: MISSING");
		}
	}

	@Test
	public void testFindByIdentifierOtherType() throws Exception {
		Identifier groupId = crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("ORG", List.of("GRP")).getOrganizationId();
		assertEquals("ORG", crm.findOrganizationDetails(organizationId).getDisplayName());
		try {
			crm.findOrganizationDetails(groupId);
			fail("Requested the wrong type");
		} catch (ItemNotFoundException expected) {
		}
	}

	@Test
	public void testOrgWithNoName() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		try {
			crm.createOrganization("", List.of("GRP")).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "displayName", "Display name is mandatory for an organization");
		}
	}

	@Test
	public void testOrgWithLongName() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		try {
			crm.createOrganization("The organization can only have a name with a maximum or 60 characters", List.of("GRP")).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "displayName", "Display name must be 60 characters or less");
		}
	}

	@Test
	public void testOrgWithNoGroup() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		try {
			crm.createOrganization("Org", List.of()).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "error", "groups", "Organizations must have a permission group assigned to them");
		}
	}

	@Test
	public void testCannotUpdateDisabledGroup() throws Exception {
		Identifier groupId = crm.createGroup(new Localized("A", "A", "A")).getGroupId();
		crm.createGroup(new Localized("B", "B", "B")).getGroupId();
		Identifier organizationId = crm.createOrganization("ORG", List.of("A")).getOrganizationId();

		crm.updateOrganizationGroups(organizationId, List.of("B"));
		crm.disableGroup(groupId);

		try {
			crm.updateOrganizationGroups(organizationId, List.of("A"));
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "groups[0]", "Group is not active: A");
		}
	}

	@Test
	public void testCannotUpdateDisabledMainLocation() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("ORG", List.of("GRP")).getOrganizationId();
		Identifier locationId = crm.createLocation(organizationId, "LOC", "Location", CrmAsserts.MAILING_ADDRESS).getLocationId();
		crm.disableLocation(locationId);

		try {
			crm.updateOrganizationMainLocation(organizationId, locationId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "mainLocationId", "Main location must be active");
		}
	}

	@Test
	public void testCannotUpdateDisabledMainContact() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		Identifier organizationId = crm.createOrganization("ORG", List.of("GRP")).getOrganizationId();
		Identifier personId = crm.createPerson(organizationId, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();
		crm.disablePerson(personId);

		try {
			crm.updateOrganizationMainContact(organizationId, personId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "error", "mainContactId", "Main contact must be active");
		}
	}

	@Test
	public void testCreatingOrgWithMainContactFromOtherOrg() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		Identifier organizationA = crm.createOrganization("A", List.of("GRP")).getOrganizationId();
		Identifier organizationB = crm.createOrganization("B", List.of("GRP")).getOrganizationId();
		Identifier personB = crm.createPerson(organizationB, PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, BUSINESS_POSITION).getPersonId();

		try {
			crm.updateOrganizationMainContact(organizationA, personB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "error", "mainContactId", "Main contact organization has invalid referential integrity");
		}
	}

	@Test
	public void testCreatingOrgWithMainLocationFromOtherOrg() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		Identifier organizationA = crm.createOrganization("A", List.of("GRP")).getOrganizationId();
		Identifier organizationB = crm.createOrganization("B", List.of("GRP")).getOrganizationId();
		Identifier locationB = crm.createLocation(organizationB, "B", "Location", CrmAsserts.MAILING_ADDRESS).getLocationId();

		try {
			crm.updateOrganizationMainLocation(organizationA, locationB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "error", "mainLocationId", "Main location organization has invalid referential integrity");
		}
	}

	@Test
	public void testCreatingOrgsWithInvalidStatuses() throws Exception {
		crm.createGroup(GROUP).getGroupId();
		List<Message> messages = CrmOrganizationService.validateOrganizationDetails(crm, new OrganizationDetails(new Identifier("org"), null, "org name", null, null, List.of("GRP")));
		assertEquals(1, messages.size());
		assertMessage(messages.get(0), new Identifier("org"), "error", "status", crm.getDictionary().getMessage("validation.organization.status.required").getEnglishName());
	}

}
