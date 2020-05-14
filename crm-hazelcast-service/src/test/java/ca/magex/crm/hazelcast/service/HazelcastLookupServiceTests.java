package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLookupServiceTests {

	@Autowired private CrmLookupService hzLookupService;
	
	@Test
	public void testLookups() {
		/* test status lookups */
		runLookupTest(Status.class, hzLookupService::findStatuses, hzLookupService::findStatusByCode, hzLookupService::findStatusByLocalizedName);
		
		/* test country lookups */
		runLookupTest(Country.class, hzLookupService::findCountries, hzLookupService::findCountryByCode, hzLookupService::findCountryByLocalizedName);
		
		/* test language lookups */
		runLookupTest(Language.class, hzLookupService::findLanguages, hzLookupService::findLanguageByCode, hzLookupService::findLanguageByLocalizedName);
		
		/* test salutation lookups */
		runLookupTest(Salutation.class, hzLookupService::findSalutations, hzLookupService::findSalutationByCode, hzLookupService::findSalutationByLocalizedName);
		
		/* test sector lookups */
		runLookupTest(BusinessSector.class, hzLookupService::findBusinessSectors, hzLookupService::findBusinessSectorByCode, hzLookupService::findBusinessSectorByLocalizedName);
		
		/* test unit lookups */
		runLookupTest(BusinessUnit.class, hzLookupService::findBusinessUnits, hzLookupService::findBusinessUnitByCode, hzLookupService::findBusinessUnitByLocalizedName);
		
		/* test classification lookups */
		runLookupTest(BusinessClassification.class, hzLookupService::findBusinessClassifications, hzLookupService::findBusinessClassificationByCode, hzLookupService::findBusinessClassificationByLocalizedName);
	}

	/**
	 * tests for the lookup routines
	 * @param <T>
	 * @param item
	 * @param supplier
	 * @param codeLookup
	 * @param localizedLookup
	 */
	private <T extends CrmLookupItem> void runLookupTest(Class<T> item, Supplier<List<T>> supplier, Function<String, T> codeLookup, BiFunction<Locale, String, T> localizedLookup) {
		/* countries tests */
		List<T> values = supplier.get();
		Assert.assertTrue(values.size() > 0);
		for (T value : values) {
			Assert.assertEquals(value, codeLookup.apply(value.getCode()));
			Assert.assertEquals(value, localizedLookup.apply(Lang.ENGLISH, value.getName(Lang.ENGLISH)));
			Assert.assertEquals(value, localizedLookup.apply(Lang.FRENCH, value.getName(Lang.FRENCH)));
			try {
				codeLookup.apply("???");
				Assert.fail("should have failed here");
			}
			catch(ItemNotFoundException e) {
				Assert.assertEquals("Item not found: " + item.getSimpleName() + " '???'", e.getMessage());
			}
			try {
				localizedLookup.apply(Lang.ENGLISH, "???");
				Assert.fail("should have failed here");
			} catch (ItemNotFoundException e) {
				Assert.assertEquals("Item not found: " + item.getSimpleName() + "[en_CA] '???'", e.getMessage());
			}

			try {
				localizedLookup.apply(Locale.GERMAN, "???");
				Assert.fail("should have failed here");
			} catch (ItemNotFoundException e) {
				Assert.assertEquals("Item not found: " + item.getSimpleName() + "[de] '???'", e.getMessage());
			}
		}
	}
}
