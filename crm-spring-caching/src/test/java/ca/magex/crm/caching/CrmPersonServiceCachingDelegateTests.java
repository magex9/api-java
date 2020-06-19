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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.caching.config.CachingTestConfig;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.config.MockConfig;
import ca.magex.crm.test.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CachingTestConfig.class, TestConfig.class, MockConfig.class })
@ActiveProfiles(profiles = { CrmProfiles.CRM_NO_AUTH })
public class CrmPersonServiceCachingDelegateTests {

	@Autowired @Qualifier("PrincipalPersonService") private CrmPersonService delegate;
	@Autowired private CacheManager cacheManager;
	@Autowired @Qualifier("CrmPersonServiceCachingDelegate") private CrmPersonService personService;

	@Before
	public void reset() {
		Mockito.reset(delegate);
		/* clear our caches */
		cacheManager.getCacheNames().forEach((cacheName) -> {
			cacheManager.getCache(cacheName).clear();
		});
	}

	@Test
	public void testCacheNewPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			return new PersonDetails(new Identifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4));
		}).given(delegate).createPerson(Mockito.any(Identifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.any(BusinessPosition.class));
		PersonDetails personDetails = personService.createPerson(new Identifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		BDDMockito.verify(delegate, Mockito.times(1)).createPerson(Mockito.any(Identifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.any(BusinessPosition.class));

		/* should have added the details to the cache */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testCacheNewPersonFromPrototype() {
		final AtomicInteger personIndex = new AtomicInteger();
		BDDMockito.willAnswer((invocation) -> {
			PersonDetails arg = invocation.getArgument(0);
			return new PersonDetails(new Identifier(Integer.toString(personIndex.getAndIncrement())), arg.getOrganizationId(), arg.getStatus(), arg.getDisplayName(), arg.getLegalName(), arg.getAddress(), arg.getCommunication(), arg.getPosition());
		}).given(delegate).createPerson(Mockito.any(PersonDetails.class));
		PersonDetails personDetails = personService.createPerson(new PersonDetails(null, new Identifier("ABC"), Status.ACTIVE, "display", CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION));
		BDDMockito.verify(delegate, Mockito.times(1)).createPerson(Mockito.any(PersonDetails.class));
		
		/* should have added the details to the cache */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));

		/* should have added the summary to the cache */
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testCacheExistingPerson() {
		BDDMockito.willAnswer((invocation) -> {
			return new PersonDetails(invocation.getArgument(0), new Identifier("ABC"), Status.ACTIVE, "display", CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		}).given(delegate).findPersonDetails(Mockito.any(Identifier.class));

		BDDMockito.willAnswer((invocation) -> {
			return new PersonDetails(invocation.getArgument(0), new Identifier("ABC"), Status.ACTIVE, "display", CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		}).given(delegate).findPersonSummary(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		PersonDetails personDetails = personService.findPersonDetails(new Identifier("1"));
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		PersonSummary personSummary = personService.findPersonSummary(new Identifier("1"));
		Assert.assertEquals(personSummary, personService.findPersonSummary(personSummary.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonSummary(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testCacheNonExistantPerson() {
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).findPersonDetails(Mockito.any(Identifier.class));

		/* this should also cache the result, so the second find doesn't hit the delegate */
		Assert.assertNull(personService.findPersonDetails(new Identifier("1")));
		Assert.assertNull(personService.findPersonDetails(new Identifier("1")));
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testDisableCachedPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new Identifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(Identifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.any(BusinessPosition.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.INACTIVE));
			return reference.get();
		}).given(delegate).disablePerson(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findPersonDetails(Mockito.any(Identifier.class));

		PersonDetails personDetails = personService.createPerson(new Identifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* make sure the summary is cached after the disable */
		PersonSummary personSummary = personService.disablePerson(personDetails.getPersonId());
		Assert.assertEquals(personSummary, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		personDetails = personService.findPersonDetails(personDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));
		personDetails = personService.findPersonDetails(personDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));
		
		/* enable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).disablePerson(new Identifier("JJ"));
		Assert.assertNull(personService.disablePerson(new Identifier("JJ")));		
		Assert.assertNull(personService.findPersonSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("JJ"));
	}
	
	@Test
	public void testEnableCachedPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new Identifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.INACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(Identifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.any(BusinessPosition.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withStatus(Status.ACTIVE));
			return reference.get();
		}).given(delegate).enablePerson(Mockito.any(Identifier.class));
		BDDMockito.willAnswer((invocation) -> {
			return reference.get();
		}).given(delegate).findPersonDetails(Mockito.any(Identifier.class));

		PersonDetails personDetails = personService.createPerson(new Identifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* make sure the summary is cached after the disable */
		PersonSummary personSummary = personService.enablePerson(personDetails.getPersonId());
		Assert.assertEquals(personSummary, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* details should have been evicted from the cache */
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		personDetails = personService.findPersonDetails(personDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));
		personDetails = personService.findPersonDetails(personDetails.getOrganizationId());
		BDDMockito.verify(delegate, Mockito.times(1)).findPersonDetails(Mockito.any(Identifier.class));
		
		/* enable non existent organization (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).enablePerson(new Identifier("JJ"));
		Assert.assertNull(personService.enablePerson(new Identifier("JJ")));
		Assert.assertNull(personService.findPersonSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("JJ"));
	}

	@Test
	public void testUpdateExistingPerson() {
		final AtomicInteger personIndex = new AtomicInteger();
		final AtomicReference<PersonDetails> reference = new AtomicReference<PersonDetails>();
		BDDMockito.willAnswer((invocation) -> {
			reference.set(new PersonDetails(new Identifier(Integer.toString(personIndex.getAndIncrement())), invocation.getArgument(0), Status.ACTIVE, "display", invocation.getArgument(1), invocation.getArgument(2), invocation.getArgument(3), invocation.getArgument(4)));
			return reference.get();
		}).given(delegate).createPerson(Mockito.any(Identifier.class), Mockito.any(PersonName.class), Mockito.any(MailingAddress.class), Mockito.any(Communication.class), Mockito.any(BusinessPosition.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withLegalName(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonName(Mockito.any(Identifier.class), Mockito.any(PersonName.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withAddress(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonAddress(Mockito.any(Identifier.class), Mockito.any(MailingAddress.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withCommunication(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonCommunication(Mockito.any(Identifier.class), Mockito.any(Communication.class));
		BDDMockito.willAnswer((invocation) -> {
			reference.set(reference.get().withPosition(invocation.getArgument(1)));
			return reference.get();
		}).given(delegate).updatePersonBusinessPosition(Mockito.any(Identifier.class), Mockito.any(BusinessPosition.class));
		
		/* create and ensure cached */
		PersonDetails personDetails = personService.createPerson(new Identifier("ABC"), CrmAsserts.PERSON_NAME, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		Assert.assertEquals(reference.get(), personDetails);
		/* ensure the details and summary are cached */
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* clear cache, update name, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonName(personDetails.getPersonId(), CrmAsserts.ADAM);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonAddress(personDetails.getPersonId(), CrmAsserts.DE_ADDRESS);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
	
		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonCommunication(personDetails.getPersonId(), CrmAsserts.WORK_COMMUNICATIONS);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
		
		/* clear cache, update address, and ensure cached */
		cacheManager.getCache("persons").clear();
		personDetails = personService.updatePersonBusinessPosition(personDetails.getPersonId(), CrmAsserts.BUSINESS_POSITION);
		Assert.assertEquals(personDetails, personService.findPersonDetails(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		Assert.assertEquals(personDetails, personService.findPersonSummary(personDetails.getPersonId()));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonName(Mockito.eq(new Identifier("JJ")), Mockito.any());
		Assert.assertNull(personService.updatePersonName(new Identifier("JJ"), CrmAsserts.DAN));
		Assert.assertNull(personService.findPersonDetails(new Identifier("JJ")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new Identifier("JJ"));
		Assert.assertNull(personService.findPersonSummary(new Identifier("JJ")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("JJ"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonAddress(Mockito.eq(new Identifier("KK")), Mockito.any());
		Assert.assertNull(personService.updatePersonAddress(new Identifier("KK"), CrmAsserts.DE_ADDRESS));
		Assert.assertNull(personService.findPersonDetails(new Identifier("KK")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new Identifier("KK"));
		Assert.assertNull(personService.findPersonSummary(new Identifier("KK")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("KK"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonCommunication(Mockito.eq(new Identifier("LL")), Mockito.any());
		Assert.assertNull(personService.updatePersonCommunication(new Identifier("LL"), CrmAsserts.HOME_COMMUNICATIONS));
		Assert.assertNull(personService.findPersonDetails(new Identifier("LL")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new Identifier("LL"));
		Assert.assertNull(personService.findPersonSummary(new Identifier("LL")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("LL"));
		
		/* update non existent person (should cache the fact that it doesn't exist for the summary) */
		BDDMockito.willAnswer((invocation) -> {
			return null;
		}).given(delegate).updatePersonBusinessPosition(Mockito.eq(new Identifier("MM")), Mockito.any());
		Assert.assertNull(personService.updatePersonBusinessPosition(new Identifier("MM"), CrmAsserts.DEVELOPER_POSITION));
		Assert.assertNull(personService.findPersonDetails(new Identifier("MM")));		
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(new Identifier("MM"));
		Assert.assertNull(personService.findPersonSummary(new Identifier("MM")));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(new Identifier("MM"));
	}
	
	@Test
	public void testCachingFindDetailsResults() {
		PersonDetails details1 = new PersonDetails(new Identifier("A"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.CA_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		PersonDetails details2 = new PersonDetails(new Identifier("B"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		PersonDetails details3 = new PersonDetails(new Identifier("C"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.DE_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);

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
		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonDetails(details2.getPersonId()));
		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonDetails(details3.getPersonId()));
		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("persons").clear();
		Assert.assertEquals(3, personService.findPersonDetails(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonDetails(details1.getPersonId()));
		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonDetails(details2.getPersonId()));
		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonDetails(details3.getPersonId()));
		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonDetails(Mockito.any(Identifier.class));
		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
	}
	
	@Test
	public void testCachingFindSummariesResults() {
		PersonDetails details1 = new PersonDetails(new Identifier("A"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.CA_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		PersonDetails details2 = new PersonDetails(new Identifier("B"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.FR_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);
		PersonDetails details3 = new PersonDetails(new Identifier("C"), new Identifier("O"), Status.ACTIVE, "display", CrmAsserts.ADAM, CrmAsserts.DE_ADDRESS, CrmAsserts.HOME_COMMUNICATIONS, CrmAsserts.DEVELOPER_POSITION);

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), invocation.getArgument(1), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonSummaries(Mockito.any(PersonsFilter.class), Mockito.any(Paging.class));

		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findPersonSummaries(Mockito.any(PersonsFilter.class));
		
		BDDMockito.willAnswer((invocation) -> {
			return new FilteredPage<>(invocation.getArgument(0), PersonsFilter.getDefaultPaging(), List.of(details1, details2, details3), 3);
		}).given(delegate).findActivePersonSummariesForOrg(Mockito.any(Identifier.class));

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

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));

		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findPersonSummaries(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
		
		/* find locations summaries with default paging and ensure cached results */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findPersonSummaries(new PersonsFilter()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
		
		/* find active locations summaries */
		cacheManager.getCache("locations").clear();
		Assert.assertEquals(3, personService.findActivePersonSummariesForOrg(details1.getOrganizationId()).getNumberOfElements());

		Assert.assertEquals(details1, personService.findPersonSummary(details1.getPersonId()));

		Assert.assertEquals(details2, personService.findPersonSummary(details2.getPersonId()));

		Assert.assertEquals(details3, personService.findPersonSummary(details3.getPersonId()));

		BDDMockito.verify(delegate, Mockito.times(0)).findPersonSummary(Mockito.any(Identifier.class));
	}
}
