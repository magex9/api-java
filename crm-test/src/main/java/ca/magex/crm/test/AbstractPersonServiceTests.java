package ca.magex.crm.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public abstract class AbstractPersonServiceTests {

	public abstract CrmOrganizationService getOrganizationService();
	
	public abstract CrmPersonService getPersonService();

	public abstract void reset();
		
	@Before
	public void setup() {
		reset();	
	}

	@Test
	public void testPersons() {
		Identifier blizzardId = getOrganizationService().createOrganization("Blizzard", List.of("ORG")).getOrganizationId();
		
		PersonName leroy = new PersonName("Mr", "Leroy", "MF", "Jenkins");
		MailingAddress eiffel = new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007");
		Communication comms = new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797");
		BusinessPosition position = new BusinessPosition("IT", "Tester", "Junior");
		/* create */
		PersonDetails p1 = getPersonService().createPerson(blizzardId, leroy, eiffel, comms, position);
		Assert.assertEquals("Jenkins, Leroy MF", p1.getDisplayName());
		Assert.assertEquals(leroy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, getPersonService().findPersonDetails(p1.getPersonId()));
		
		getPersonService().createPerson(
			blizzardId, 
			new PersonName("Ms", "Tammy", "GD", "Jones"), 
			new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007"), 
			new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"), 
			new BusinessPosition("IT", "Tester", "Junior"));
		
		getPersonService().createPerson(
			blizzardId, 
			new PersonName("Mr", "James", "Earl", "Bond"), 
			new MailingAddress("5 Avenue Anatole France", "Paris", "", "France", "75007"), 
			new Communication("Leader", "English", "leeroy@blizzard.com", new Telephone("555-9898"), "555-9797"), 
			new BusinessPosition("IT", "Tester", "Junior"));
		
		/* update */
		PersonName tommy = new PersonName("Mrs", "Michelle", "Pauline", "Smith");
		p1 = getPersonService().updatePersonName(p1.getPersonId(), tommy);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(eiffel, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, getPersonService().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, getPersonService().updatePersonName(p1.getPersonId(), tommy));
		
		MailingAddress louvre = new MailingAddress("Rue de Rivoli", "Paris", "", "France", "75001");
		p1 = getPersonService().updatePersonAddress(p1.getPersonId(), louvre);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, getPersonService().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, getPersonService().updatePersonAddress(p1.getPersonId(), louvre));
		
		Communication comms2 = new Communication("Follower", "French", "follower@blizzard.com", new Telephone("666-9898"), "666-9797");
		p1 = getPersonService().updatePersonCommunication(p1.getPersonId(), comms2);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(position, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, getPersonService().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, getPersonService().updatePersonCommunication(p1.getPersonId(), comms2));
		
		BusinessPosition position2 = new BusinessPosition("Legal", "Lawyer", "Senior");
		p1 = getPersonService().updatePersonBusinessPosition(p1.getPersonId(), position2);
		Assert.assertEquals("Smith, Michelle Pauline", p1.getDisplayName());
		Assert.assertEquals(tommy, p1.getLegalName());
		Assert.assertEquals(louvre, p1.getAddress());
		Assert.assertEquals(comms2, p1.getCommunication());
		Assert.assertEquals(position2, p1.getPosition());
		Assert.assertEquals(Status.ACTIVE, p1.getStatus());
		Assert.assertEquals(p1, getPersonService().findPersonDetails(p1.getPersonId()));
		Assert.assertEquals(p1, getPersonService().updatePersonBusinessPosition(p1.getPersonId(), position2));
		
		/* disable */
		PersonSummary ps1 = getPersonService().disablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.INACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, getPersonService().findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, getPersonService().disablePerson(p1.getPersonId()));
		
		/* enable */
		ps1 = getPersonService().enablePerson(p1.getPersonId());
		Assert.assertEquals("Smith, Michelle Pauline", ps1.getDisplayName());
		Assert.assertEquals(Status.ACTIVE, ps1.getStatus());
		Assert.assertEquals(ps1, getPersonService().findPersonSummary(ps1.getPersonId()));
		Assert.assertEquals(ps1, getPersonService().enablePerson(p1.getPersonId()));
		
		/* count */
		Assert.assertEquals(3, getPersonService().countPersons(new PersonsFilter(null, null, null)));
		Assert.assertEquals(3, getPersonService().countPersons(new PersonsFilter(blizzardId, null, null)));
		Assert.assertEquals(3, getPersonService().countPersons(new PersonsFilter(blizzardId, null, Status.ACTIVE)));
		Assert.assertEquals(1, getPersonService().countPersons(new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE)));
		Assert.assertEquals(0, getPersonService().countPersons(new PersonsFilter(blizzardId, null, Status.INACTIVE)));
		Assert.assertEquals(0, getPersonService().countPersons(new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE)));
		
		/* find details */
		Page<PersonDetails> detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(blizzardId, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(blizzardId, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(3, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(3, detailsPage.getTotalElements());
		
		detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(blizzardId, null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(1, detailsPage.getNumberOfElements());		
		Assert.assertEquals(1, detailsPage.getTotalPages());
		Assert.assertEquals(1, detailsPage.getTotalElements());
		
		detailsPage = getPersonService().findPersonDetails(
				new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, detailsPage.getNumber());
		Assert.assertEquals(5, detailsPage.getSize());
		Assert.assertEquals(0, detailsPage.getNumberOfElements());		
		Assert.assertEquals(0, detailsPage.getTotalPages());
		Assert.assertEquals(0, detailsPage.getTotalElements());
		
		/* find summaries */
		Page<PersonSummary> summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(null, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(blizzardId, null, null), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(blizzardId, null, Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(3, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(3, summariesPage.getTotalElements());
		
		summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(blizzardId, null, Status.INACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(0, summariesPage.getNumberOfElements());		
		Assert.assertEquals(0, summariesPage.getTotalPages());
		Assert.assertEquals(0, summariesPage.getTotalElements());
		
		summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(blizzardId, p1.getDisplayName(), Status.ACTIVE), 
				new Paging(1, 5, Sort.by("displayName")));
		Assert.assertEquals(1, summariesPage.getNumber());
		Assert.assertEquals(5, summariesPage.getSize());
		Assert.assertEquals(1, summariesPage.getNumberOfElements());		
		Assert.assertEquals(1, summariesPage.getTotalPages());
		Assert.assertEquals(1, summariesPage.getTotalElements());
		
		summariesPage = getPersonService().findPersonSummaries(
				new PersonsFilter(new Identifier("ACTIVISION"), "SpaceX", Status.INACTIVE), 
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
			getPersonService().findPersonDetails(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}

		try {
			getPersonService().findPersonSummary(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}

		try {
			getPersonService().updatePersonName(new Identifier("abc"), new PersonName("", "", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			getPersonService().updatePersonAddress(new Identifier("abc"), new MailingAddress("", "", "", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			getPersonService().updatePersonCommunication(new Identifier("abc"), new Communication("", "", "", new Telephone(""), ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			getPersonService().updatePersonBusinessPosition(new Identifier("abc"), new BusinessPosition("", "", ""));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			getPersonService().disablePerson(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
		
		try {
			getPersonService().enablePerson(new Identifier("abc"));
			Assert.fail("should fail if we get here");
		} catch (ItemNotFoundException e) {
			Assert.assertEquals("Item not found: Person ID 'abc'", e.getMessage());
		}
	}
}