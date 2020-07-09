package ca.magex.crm.caching;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.caching.config.CachingConfig;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.caching.util.CacheTemplate;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.config.MockTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, MockTestConfig.class })
public class CrmPersonServiceCachingDelegateTests {

	@Autowired private CrmPersonService delegate;
	@Autowired private CacheManager cacheManager;
	
	private CacheTemplate cacheTemplate;
	private CrmPersonServiceCachingDelegate personService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
		cacheTemplate = new CacheTemplate(cacheManager, CachingConfig.Caches.Persons);
		personService = new CrmPersonServiceCachingDelegate(delegate, cacheTemplate);
	}

	@Test
	public void testCacheNewPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new PersonDetails(new PersonIdentifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4));
		}).given(delegate).createPerson(Mockito.any(OrganizationIdentifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.anyList());
		PersonDetails personDetails = personService.createPerson(new OrganizationIdentifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		BDDMockito.verify(delegate, Mockito.times(1)).createPerson(Mockito.any(), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.anyList());

		/* should have added the details to the cache */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
	
	@Test
	public void testCacheNewPersonFromPrototype() {
		final AtomicInteger personIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			PersonDetails arg = invocation.getArgument(0);
			return new PersonDetails(new PersonIdentifier(Integer.toString(personIndex.getAndIncrement())), arg.getOrganizationId(), arg.getStatus(), arg.getDisplayName(), arg.getLegalName(), arg.getAddress(), arg.getCommunication(), arg.getBusinessRoleIds());
		}).given(delegate).createPerson(Mockito.any(PersonDetails.class));
		PersonDetails personDetails = personService.createPerson(new PersonDetails(null, new OrganizationIdentifier("ABC"), Status.ACTIVE, "display", CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER"))));
		BDDMockito.verify(delegate, Mockito.times(1)).createPerson(Mockito.any(PersonDetails.class));
		
		/* should have added the details to the cache */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
	
	@Test
	public void testCacheExistingPerson() {
		BDDMockito.willAnswer((invocation) -> {
			return new PersonDetails(invocation.getArgument(0), new OrganizationIdentifier("ABC"), Status.ACTIVE, "display", CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		}).given(delegate).findPersonDetails(Mockito.any(PersonIdentifier.class));
		
		/* this should also cache the result, so the second find doesn't hit the delegate */
		PersonDetails personDetails = personService.findPersonDetails(new PersonIdentifier("1"));
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		PersonSummary personSummary = personService.findPersonSummary(new PersonIdentifier("1"));
		Assert.assertEquals(personSummary, personService.findPersonSummary(personSummary.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
	
	@Test
	public void testCacheNonExistantPerson() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findPersonDetails(Mockito.any(PersonIdentifier.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("1")));
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		
		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("2")));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("2")));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
	
	@Test
	public void testDisableCachedPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new PersonIdentifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(OrganizationIdentifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disablePerson(Mockito.any(PersonIdentifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findPersonDetails(Mockito.any(PersonIdentifier.class));

		PersonDetails personDetails = personService.createPerson(new OrganizationIdentifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* make sure the summary is cached after the disable */
		PersonSummary personSummary = personService.disablePerson(personDetails.getPersonId());
		Assert.assertEquals(personSummary, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		
		/* enable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disablePerson(new PersonIdentifier("JJ"));
		Assert.assertNull(personService.disablePerson(new PersonIdentifier("JJ")));		
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("JJ"));
	}
	
	@Test
	public void testEnableCachedPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new PersonIdentifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.INACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(OrganizationIdentifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enablePerson(Mockito.any(PersonIdentifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findPersonDetails(Mockito.any(PersonIdentifier.class));

		PersonDetails personDetails = personService.createPerson(new OrganizationIdentifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* make sure the summary is cached after the disable */
		PersonSummary personSummary = personService.enablePerson(personDetails.getPersonId());
		Assert.assertEquals(personSummary, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		personDetails = personService.findPersonDetails(personDetails.getPersonId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		
		/* enable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enablePerson(new PersonIdentifier("JJ"));
		Assert.assertNull(personService.enablePerson(new PersonIdentifier("JJ")));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("JJ"));
	}

	@Test
	public void testUpdateExistingPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<PersonDetails>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new PersonIdentifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(OrganizationIdentifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.anyList());
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withLegalName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonName(Mockito.any(PersonIdentifier.class), Mockito.any(PersonName.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withAddress(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonAddress(Mockito.any(PersonIdentifier.class), Mockito.any(MailingAddress.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withCommunication(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonCommunication(Mockito.any(PersonIdentifier.class), Mockito.any(Communication.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withBusinessRoleIds(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonRoles(Mockito.any(PersonIdentifier.class), Mockito.anyList());
		
		/* create and ensure cached */
		PersonDetails personDetails = personService.createPerson(new OrganizationIdentifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* clear cache, update name, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonName(personDetails.getPersonId(), CrmAsserts.ADAM);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonAddress(personDetails.getPersonId(), CrmAsserts.DE_ADDRESS);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	
		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonCommunication(personDetails.getPersonId(), CrmAsserts.WORK_COMMUNICATIONS);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
		
		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonRoles(personDetails.getPersonId(), List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		Assert.assertEquals(personDetails.asSummary(), personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonName(Mockito.eq(new PersonIdentifier("JJ")), Mockito.any());
		Assert.assertNull(personService.updatePersonName(new PersonIdentifier("JJ"), CrmAsserts.DAN));
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("JJ")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new PersonIdentifier("JJ"));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("JJ"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonAddress(Mockito.eq(new PersonIdentifier("KK")), Mockito.any());
		Assert.assertNull(personService.updatePersonAddress(new PersonIdentifier("KK"), CrmAsserts.DE_ADDRESS));
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("KK")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new PersonIdentifier("KK"));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("KK"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonCommunication(Mockito.eq(new PersonIdentifier("LL")), Mockito.any());
		Assert.assertNull(personService.updatePersonCommunication(new PersonIdentifier("LL"), CrmAsserts.HOME_COMMUNICATIONS));
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("LL")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new PersonIdentifier("LL"));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("LL"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonRoles(Mockito.eq(new PersonIdentifier("MM")), Mockito.anyList());
		Assert.assertNull(personService.updatePersonRoles(new PersonIdentifier("MM"), List.of(new BusinessRoleIdentifier("IMIT/MANAGER"))));
		Assert.assertNull(personService.findPersonDetails(new PersonIdentifier("MM")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new PersonIdentifier("MM"));
		Assert.assertNull(personService.findPersonSummary(new PersonIdentifier("MM")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new PersonIdentifier("MM"));
	}
	
	@Test
	public void testCachingFindDetailsResults() {
		PersonDetails details1 = new PersonDetails(new PersonIdentifier("A"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.CA_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		PersonDetails details2 = new PersonDetails(new PersonIdentifier("B"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		PersonDetails details3 = new PersonDetails(new PersonIdentifier("C"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.DE_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonDetails(Mockito.any(PersonsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonDetails(Mockito.any(PersonsFilter.class));

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countPersons(Mockito.any(PersonsFilter.class));

		Assert.assertEquals(3l, personService.countPersons(new PersonsFilter()));

		/* find locations summaries with paging and ensure cached results */
		cacheManager.getCache("persons").clear();
		Assert.assertEquals(3, personService.findPersonDetails(new PersonsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonDetails(details1.getPersonId()));
		Assert.assertEquals(details1.asSummary(), personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonDetails(details2.getPersonId()));
		Assert.assertEquals(details2.asSummary(), personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonDetails(details3.getPersonId()));
		Assert.assertEquals(details3.asSummary(), personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("persons").clear();
		Assert.assertEquals(3, personService.findPersonDetails(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonDetails(details1.getPersonId()));
		Assert.assertEquals(details1.asSummary(), personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonDetails(details2.getPersonId()));
		Assert.assertEquals(details2.asSummary(), personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonDetails(details3.getPersonId()));
		Assert.assertEquals(details3.asSummary(), personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(PersonIdentifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
	
	@Test
	public void testCachingFindSummariesResults() {
		PersonDetails details1 = new PersonDetails(new PersonIdentifier("A"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.CA_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		PersonDetails details2 = new PersonDetails(new PersonIdentifier("B"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));
		PersonDetails details3 = new PersonDetails(new PersonIdentifier("C"), new OrganizationIdentifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.DE_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, List.of(new BusinessRoleIdentifier("IMIT/MANAGER")));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonSummaries(Mockito.any(PersonsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonSummaries(Mockito.any(PersonsFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findActivePersonSummariesForOrg(Mockito.any(OrganizationIdentifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return 3l;
		}).given(delegate).countPersons(Mockito.any(PersonsFilter.class));

		Assert.assertEquals(3l, personService.countPersons(new PersonsFilter()));

		/* find locations summaries with paging and ensure cached results */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findPersonSummaries(new PersonsFilter(), new Paging(1, 5, Sort.unsorted())).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findPersonSummaries(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
		
		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findPersonSummaries(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
		
		/* find active locations summaries */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findActivePersonSummariesForOrg(details1.getOrganizationId()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(PersonIdentifier.class));
	}
}
