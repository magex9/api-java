package ca.magex.crm.test;

import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.FRANCE;
import static ca.magex.crm.test.CrmAsserts.FRENCH;
import static ca.magex.crm.test.CrmAsserts.ILE_DE_FRANCE;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;

@Transactional
public abstract class AbstractPersonServiceTests {

	/**
	 * Configuration Service used to setup the system for testing
	 * @return
	 */
	protected abstract Crm config();			
	
	/**
	 * Authentication service used to allow an authenticated test
	 * @return
	 */
	protected abstract CrmAuthenticationService auth();
	
	/**
	 * The CRM Services to be tested
	 * @return
	 */
	protected abstract CrmPersonService persons();
	
	@Before
	public void setup() {
		config().reset();
		config().initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		auth().login("admin", "admin");
	}
	
	@After
	public void cleanup() {
		auth().logout();
	}

	@Test
	public void testPersons() {
		OrganizationIdentifier blizzardId = config().createOrganization("Blizzard", List.of(new AuthenticationGroupIdentifier("ORG")), List.of(new BusinessGroupIdentifier("ORG"))).getOrganizationId();
		
		PersonName leroy = new PersonName(CrmAsserts.MR, "Leroy", "MF", "Jenkins");
		MailingAddress eiffel = new MailingAddress("5 Avenue Anatole France", "Paris", ILE_DE_FRANCE, FRANCE, "75007");
		Communication comms = new Communication("Leader", ENGLISH, "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797");
		
		/* create */
		PersonDetails p1 = persons().createPerson(blizzardId, CrmAsserts.displayName(leroy), leroy, eiffel, comms, List.of(CrmAsserts.CEO));
		Assert.assertEquals("Jenkins, Leroy MF", p1.getDisplayName());
		Assert.assertEquals(leroy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.CEO), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1.asSummary(), persons().findPersonSummary(p1.getPersonId()));
		
		PersonName tammy = new PersonName(CrmAsserts.MRS, "Tammy", "GD", "Jones");
		
		PersonName james = new PersonName(CrmAsserts.MR, "James", "Earl", "Bond");
		
		persons().createPerson(
			blizzardId, CrmAsserts.displayName(tammy), tammy, 
			new MailingAddress("5 Avenue Anatole France", "Paris", ILE_DE_FRANCE, FRANCE, "75007"), 
			new Communication("Leader", ENGLISH, "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"), 
			List.of(CrmAsserts.QA_TEAMLEAD));
		
		persons().createPerson(
			blizzardId, CrmAsserts.displayName(james), james, 
			new MailingAddress("5 Avenue Anatole France", "Paris", ILE_DE_FRANCE, FRANCE, "75007"), 
			new Communication("Leader", FRENCH, "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"),
			List.of(CrmAsserts.DEV_TEAMLEAD));
		
		/* update */
		PersonName tommy = new PersonName(CrmAsserts.MRS, "Michelle", "Pauline", "Smith");
		p1 = persons().updatePersonDisplayName(p1.getPersonId(), CrmAsserts.displayName(tommy));
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(CrmAsserts.displayName(tommy), p1.getDisplayName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.CEO), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, persons().updatePersonDisplayName(p1.getPersonId(), CrmAsserts.displayName(tommy)));

		p1 = persons().updatePersonLegalName(p1.getPersonId(), tommy);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.CEO), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, persons().updatePersonLegalName(p1.getPersonId(), tommy));
		
		MailingAddress louvre = new MailingAddress("Rue de Rivoli", "Paris", ILE_DE_FRANCE, FRANCE, "75001");
		p1 = persons().updatePersonAddress(p1.getPersonId(), louvre);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.CEO), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, persons().updatePersonAddress(p1.getPersonId(), louvre));
		
		Communication comms2 = new Communication("Follower", FRENCH, "follower@blizzard.com", new Telephone("666-9898"), "666-9797");
		p1 = persons().updatePersonCommunication(p1.getPersonId(), comms2);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.CEO), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, persons().updatePersonCommunication(p1.getPersonId(), comms2));
		
		p1 = persons().updatePersonRoles(p1.getPersonId(), List.of(CrmAsserts.DEV_TEAMLEAD));
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(List.of(CrmAsserts.DEV_TEAMLEAD), p1.getBusinessRoleIds());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, persons().findPersonDetails(p1.getPersonId()));
		
		/* disable */
		PersonSummary ps1 = persons().disablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, persons().findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, persons().disablePerson(p1.getPersonId()));
		
		/* enable */
		ps1 = persons().enablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, persons().findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, persons().enablePerson(p1.getPersonId()));
		
		/* count */
		Assert.assertEquals(4, persons().countPersons(new PersonsFilter(null, null, null)));
		Assert.assertEquals(3, persons().countPersons(new PersonsFilter(blizzardId, null, null)));
		Assert.assertEquals(3, persons().countPersons(new PersonsFilter(blizzardId, null, Status.ACTIVE)));
		Assert.assertEquals(1, persons().countPersons(new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE)));
		Assert.assertEquals(0, persons().countPersons(new PersonsFilter(blizzardId, null, Status.INACTIVE)));
		Assert.assertEquals(0, persons().countPersons(new PersonsFilter(new OrganizationIdentifier("ACTIVISION"), "SpaceX", Status.INACTIVE)));
		
		/* find details */
		Page<PersonDetails> detailsPage = persons().findPersonDetails(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(4, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(4, detailsPage.getTotalElements());
		
		detailsPage = persons().findPersonDetails(
				new PersonsFilter(blizzardId, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = persons().findPersonDetails(
				new PersonsFilter(blizzardId, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = persons().findPersonDetails(
				new PersonsFilter(blizzardId, null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = persons().findPersonDetails(
				new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = persons().findPersonDetails(
				new PersonsFilter(new OrganizationIdentifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find summaries */
		Page<PersonSummary> summariesPage = persons().findPersonSummaries(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(4, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(4, summariesPage.getTotalElements());
		
		summariesPage = persons().findPersonSummaries(
				new PersonsFilter(blizzardId, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = persons().findPersonSummaries(
				new PersonsFilter(blizzardId, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = persons().findPersonSummaries(
				new PersonsFilter(blizzardId, null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = persons().findPersonSummaries(
				new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = persons().findPersonSummaries(
				new PersonsFilter(new OrganizationIdentifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
	}
	
	@Test
	public void testInvalidPersonId() {
		try {
			persons().findPersonDetails(new PersonIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}

		try {
			persons().findPersonSummary(new PersonIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}

		try {
			persons().updatePersonLegalName(new PersonIdentifier("abc"), CrmAsserts.ADAM);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
		
		try {
			persons().updatePersonAddress(new PersonIdentifier("abc"), MAILING_ADDRESS);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
		
		try {
			persons().updatePersonCommunication(new PersonIdentifier("abc"), WORK_COMMUNICATIONS);
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
		
		try {
			persons().updatePersonRoles(new PersonIdentifier("abc"), List.of(CrmAsserts.CEO));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
		
		try {
			persons().disablePerson(new PersonIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
		
		try {
			persons().enablePerson(new PersonIdentifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID '/persons/abc'", e.getMessage());
		}
	}	
}