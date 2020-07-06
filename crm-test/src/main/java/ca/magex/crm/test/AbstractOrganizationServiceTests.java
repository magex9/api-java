package ca.magex.crm.test;

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
import org.springframework.transaction.annotation.Transactional;

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
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.PhraseIdentifier;

@Transactional
public abstract class AbstractOrganizationServiceTests {

	@Autowired
	protected Crm crm;
	
	@Autowired
	protected BasicAuthenticationService auth;
	
	private AuthenticationGroupIdentifier NHL = null;
	private AuthenticationGroupIdentifier PLAYOFFS = null;
	private AuthenticationGroupIdentifier ONTARIO = null;
	private AuthenticationGroupIdentifier QUEBEC = null;	
	private BusinessRoleIdentifier GM = null;
	
	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem(CrmAsserts.SYSTEM_ORG, CrmAsserts.SYSTEM_PERSON, CrmAsserts.SYSTEM_EMAIL, "admin", "admin");
		
		auth.login("admin", "admin");
		NHL = crm.createOption(null, Type.AUTHENTICATION_GROUP, new Localized("NHL", "NHL", "LNH")).getOptionId();
		PLAYOFFS = crm.createOption(NHL, Type.AUTHENTICATION_GROUP, new Localized("PLAYOFFS", "Playoffs", "Playoffs")).getOptionId();
		ONTARIO = crm.createOption(NHL, Type.AUTHENTICATION_GROUP, new Localized("ONTARIO", "Ontario", "Ontario")).getOptionId();		
		QUEBEC = crm.createOption(NHL, Type.AUTHENTICATION_GROUP, new Localized("QUEBEC", "Quebec", "Québec")).getOptionId();
	
		crm.createOption(NHL, Type.AUTHENTICATION_ROLE, new Localized("GM", "General Manager", "Gestionnaire Genèrale"));
		
		GM = crm.createOption(null, Type.BUSINESS_ROLE, new Localized("EXEC", "Owner", "Owner")).getOptionId();
		
	}
	
	@After
	public void cleanup() {
		auth.logout();
	}

	@Test
	public void testOrganizations() {
		/* create */
		OrganizationDetails o1 = crm.createOrganization("Maple Leafs", List.of(new AuthenticationGroupIdentifier("NHL")), List.of(new BusinessGroupIdentifier("ORG")));
		Assert.assertEquals("Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getAuthenticationGroupIds().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		OrganizationDetails o2 = crm.createOrganization("Senators", List.of(new AuthenticationGroupIdentifier("NHL")), List.of(new BusinessGroupIdentifier("ORG")));
		Assert.assertEquals("Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getAuthenticationGroupIds().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		OrganizationDetails o3 = crm.createOrganization("Canadiens", List.of(new AuthenticationGroupIdentifier("NHL")), List.of(new BusinessGroupIdentifier("ORG")));
		Assert.assertEquals("Canadiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getAuthenticationGroupIds().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));

		/* update display name */
		o1 = crm.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getAuthenticationGroupIds().size());
		Assert.assertNull(o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationDisplayName(o1.getOrganizationId(), "Toronto Maple Leafs");
		o2 = crm.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getAuthenticationGroupIds().size());
		Assert.assertNull(o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationDisplayName(o2.getOrganizationId(), "Ottawa Senators");
		o3 = crm.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getAuthenticationGroupIds().size());
		Assert.assertNull(o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationDisplayName(o3.getOrganizationId(), "Montreal Candiens");

		/* update main location */
		LocationIdentifier torontoId = crm.createLocation(
				o1.getOrganizationId(),
				"TORONTO",
				"Toronto",
				new MailingAddress("40 Bay St", "Toronto", CrmAsserts.ONTARIO, CrmAsserts.CANADA, "M5J 2X2")).getLocationId();
		o1 = crm.updateOrganizationMainLocation(o1.getOrganizationId(), torontoId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getAuthenticationGroupIds().size());
		Assert.assertEquals(torontoId, o1.getMainLocationId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationMainLocation(o1.getOrganizationId(), torontoId); // set to duplicate value

		LocationIdentifier ottawaId = crm.createLocation(
				o2.getOrganizationId(),
				"OTTAWA",
				"Ottawa",
				new MailingAddress("1000 Palladium Dr", "Ottawa", CrmAsserts.ONTARIO, CrmAsserts.CANADA, "K2V 1A5")).getLocationId();
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getAuthenticationGroupIds().size());
		Assert.assertEquals(ottawaId, o2.getMainLocationId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);
		o2 = crm.updateOrganizationMainLocation(o2.getOrganizationId(), ottawaId);

		LocationIdentifier montrealId = crm.createLocation(
				o3.getOrganizationId(),
				"MONTREAL",
				"Montreal",
				new MailingAddress("1909 Avenue des Canadiens-de-Montréal", "Montreal", CrmAsserts.QUEBEC, CrmAsserts.CANADA, "H4B 5G0")).getLocationId();
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getAuthenticationGroupIds().size());
		Assert.assertEquals(montrealId, o3.getMainLocationId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);
		o3 = crm.updateOrganizationMainLocation(o3.getOrganizationId(), montrealId);

		/* update main contact */
		PersonIdentifier freddyId = crm.createPerson(
				o1.getOrganizationId(),
				new PersonName(CrmAsserts.MR, "Freddy", "R", "Davis"),
				new MailingAddress("40 Bay St", "Toronto", CrmAsserts.ONTARIO, CrmAsserts.CANADA, "M5J 2X2"),
				CrmAsserts.WORK_COMMUNICATIONS,
				List.of(new BusinessRoleIdentifier("CEO"))).getPersonId();
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId);
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(1, o1.getAuthenticationGroupIds().size());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // set to duplicate value
		o1 = crm.updateOrganizationMainContact(o1.getOrganizationId(), freddyId); // reset to original value

		PersonIdentifier craigId = crm.createPerson(
				o2.getOrganizationId(),
				new PersonName(CrmAsserts.MR, "Craig", null, "Phillips"),
				new MailingAddress("1000 Palladium Dr", "Ottawa", CrmAsserts.ONTARIO, CrmAsserts.CANADA, "K2V 1A5"),
				CrmAsserts.WORK_COMMUNICATIONS,
				List.of(new BusinessRoleIdentifier("CEO"))).getPersonId();
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(1, o2.getAuthenticationGroupIds().size());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);
		o2 = crm.updateOrganizationMainContact(o2.getOrganizationId(), craigId);

		PersonIdentifier careyId = crm.createPerson(
				o3.getOrganizationId(),
				new PersonName(null, "Carey", null, "Thomas"),
				new MailingAddress("40 Bay St", "Toronto", CrmAsserts.ONTARIO, CrmAsserts.CANADA, "M5J 2X2"),
				CrmAsserts.WORK_COMMUNICATIONS,
				List.of(new BusinessRoleIdentifier("CEO"))).getPersonId();
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(1, o3.getAuthenticationGroupIds().size());
		Assert.assertEquals(careyId, o3.getMainContactId());
		Assert.assertEquals(o3, crm.findOrganizationDetails(o3.getOrganizationId()));
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);
		o3 = crm.updateOrganizationMainContact(o3.getOrganizationId(), careyId);

		/* update groups */
		o1 = crm.updateOrganizationAuthenticationGroups(o1.getOrganizationId(), List.of(NHL, PLAYOFFS, ONTARIO));
		Assert.assertEquals("Toronto Maple Leafs", o1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o1.getStatus());
		Assert.assertEquals(List.of(NHL, PLAYOFFS, ONTARIO), o1.getAuthenticationGroupIds());
		Assert.assertEquals(freddyId, o1.getMainContactId());
		Assert.assertEquals(o1, crm.findOrganizationDetails(o1.getOrganizationId()));
		Assert.assertEquals(o1, crm.updateOrganizationAuthenticationGroups(o1.getOrganizationId(), List.of(NHL, PLAYOFFS, ONTARIO)));
		o1 = crm.updateOrganizationAuthenticationGroups(o1.getOrganizationId(), List.of(NHL, ONTARIO));
		Assert.assertEquals(List.of(NHL, ONTARIO), o1.getAuthenticationGroupIds());
		o1 = crm.updateOrganizationAuthenticationGroups(o1.getOrganizationId(), List.of(NHL, PLAYOFFS, ONTARIO));
		Assert.assertEquals(List.of(NHL, PLAYOFFS, ONTARIO), o1.getAuthenticationGroupIds());

		o2 = crm.updateOrganizationAuthenticationGroups(o2.getOrganizationId(), List.of(NHL, ONTARIO));
		Assert.assertEquals("Ottawa Senators", o2.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o2.getStatus());
		Assert.assertEquals(List.of(NHL, ONTARIO), o2.getAuthenticationGroupIds());
		Assert.assertEquals(craigId, o2.getMainContactId());
		Assert.assertEquals(o2, crm.findOrganizationDetails(o2.getOrganizationId()));

		o3 = crm.updateOrganizationAuthenticationGroups(o3.getOrganizationId(), List.of(NHL, QUEBEC));
		Assert.assertEquals("Montreal Candiens", o3.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, o3.getStatus());
		Assert.assertEquals(List.of(NHL, QUEBEC), o3.getAuthenticationGroupIds());
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
		Assert.assertEquals(1, crm.countOrganizations(new OrganizationsFilter("Toronto Maple Leafs", Status.ACTIVE, NHL)));
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
			crm.findOrganizationDetails(new OrganizationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.findOrganizationSummary(new OrganizationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationDisplayName(new OrganizationIdentifier("abc"), "Oilers");
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationMainLocation(new OrganizationIdentifier("abc"), new LocationIdentifier("Edmonton"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationMainContact(new OrganizationIdentifier("abc"), new PersonIdentifier("Mikko"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.updateOrganizationAuthenticationGroups(new OrganizationIdentifier("abc"), Collections.emptyList());
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.disableOrganization(new OrganizationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}

		try {
			crm.enableOrganization(new OrganizationIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Organization ID '/organizations/abc'", e.getMessage());
		}
	}

	@Test
	public void testCreateOrgWithMissingGroup() throws Exception {				
		try {
			crm.createOrganization("INVALID", List.of(new AuthenticationGroupIdentifier("MISSING")), List.of(new BusinessGroupIdentifier("ORG")));
			fail("Should have gotten bad request");
		} catch (BadRequestException e) {
			assertEquals("Bad Request: Organization has validation errors", e.getMessage());
			assertBadRequestMessage(e, null, "/options/message-types/ERROR", "authenticationGroupIds[0]", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INVALID")));
		}
	}

	@Test
	public void testOrgWithNoName() throws Exception {
		try {
			crm.createOrganization("", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG")));
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "/options/message-types/ERROR", "displayName", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/REQUIRED")));
		}
	}

	@Test
	public void testOrgWithLongName() throws Exception {
		try {
			crm.createOrganization("The organization can only have a name with a maximum or 60 characters", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "/options/message-types/ERROR", "displayName", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/MAXLENGTH")));
		}
	}

	@Test
	public void testOrgWithNoGroup() throws Exception {
		try {
			crm.createOrganization("Org", List.of(), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
			fail("Requested the wrong type");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, null, "/options/message-types/ERROR", "authenticationGroupIds", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/REQUIRED")));
		}
	}

	@Test
	public void testCannotUpdateDisabledGroup() throws Exception {		
		AuthenticationGroupIdentifier authGroupA = (AuthenticationGroupIdentifier) crm.createOption(null, Type.AUTHENTICATION_GROUP, new Localized("A", "A", "A")).getOptionId();
		crm.disableOption(authGroupA);
		
		OrganizationDetails organization = crm.createOrganization("ORG", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG")));

		try {
			crm.updateOrganizationAuthenticationGroups(organization.getOrganizationId(), List.of(authGroupA));
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organization.getOrganizationId(), "/options/message-types/ERROR", "authenticationGroupIds[0]", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INACTIVE")));
		}
	}

	@Test
	public void testCannotUpdateDisabledMainLocation() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization("ORG", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		LocationIdentifier locationId = crm.createLocation(organizationId, "LOC", "Location", CrmAsserts.MAILING_ADDRESS).getLocationId();
		crm.disableLocation(locationId);

		try {
			crm.updateOrganizationMainLocation(organizationId, locationId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "/options/message-types/ERROR", "mainLocationId", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INACTIVE")));
		}
	}

	@Test
	public void testCannotUpdateDisabledMainContact() throws Exception {
		OrganizationIdentifier organizationId = crm.createOrganization("ORG", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		PersonIdentifier personId = crm.createPerson(organizationId, CrmAsserts.PERSON_NAME, CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, List.of(GM)).getPersonId();
		crm.disablePerson(personId);

		try {
			crm.updateOrganizationMainContact(organizationId, personId);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationId, "/options/message-types/ERROR", "mainContactId", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INACTIVE")));
		}
	}

	@Test
	public void testCreatingOrgWithMainContactFromOtherOrg() throws Exception {
		OrganizationIdentifier organizationA = crm.createOrganization("A", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		OrganizationIdentifier organizationB = crm.createOrganization("B", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		PersonIdentifier personB = crm.createPerson(organizationB, CrmAsserts.PERSON_NAME, CrmAsserts.MAILING_ADDRESS, CrmAsserts.WORK_COMMUNICATIONS, List.of(GM)).getPersonId();

		try {
			crm.updateOrganizationMainContact(organizationA, personB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "/options/message-types/ERROR", "mainContactId", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INVALID")));
		}
	}

	@Test
	public void testCreatingOrgWithMainLocationFromOtherOrg() throws Exception {
		OrganizationIdentifier organizationA = crm.createOrganization("A", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		OrganizationIdentifier organizationB = crm.createOrganization("B", List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		LocationIdentifier locationB = crm.createLocation(organizationB, "B", "Location", CrmAsserts.MAILING_ADDRESS).getLocationId();

		try {
			crm.updateOrganizationMainLocation(organizationA, locationB);
			fail("Unable to assign disabled references");
		} catch (BadRequestException e) {
			assertBadRequestMessage(e, organizationA, "/options/message-types/ERROR", "mainLocationId", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/INVALID")));
		}
	}

	@Test
	public void testCreatingOrgsWithInvalidStatuses() throws Exception {
		List<Message> messages = CrmOrganizationService.validateOrganizationDetails(
				crm, 
				new OrganizationDetails(new OrganizationIdentifier("org"), null, "org name", null, null, List.of(NHL), List.of(new BusinessGroupIdentifier("ORG"))));
		assertEquals(1, messages.size());
		assertMessage(messages.get(0), new OrganizationIdentifier("org"), "/options/message-types/ERROR", "status", new Choice<>(new PhraseIdentifier("/options/phrases/VALIDATION/FIELD/REQUIRED")));
	}
}
