package ca.magex.crm.test;

import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.CrmLookupItem;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.test.function.TriFunction;

public abstract class AbstractLookupServiceTests {

	protected Crm crm;
	
	protected AbstractLookupServiceTests() {}
	
	public AbstractLookupServiceTests(Crm crm) {
		this.crm = crm;
	}

	@Before
	public void setup() {
		crm.reset();
		crm.initializeSystem("Magex", CrmAsserts.PERSON_NAME, "admin@magex.ca", "admin", "admin");
	}
	
	@Test
	public void testLookups() {
		/* test status lookups */
		runLookupTest(Status.class, crm::findStatuses, crm::findStatusByCode, crm::findStatusByLocalizedName);
		
		/* test country lookups */
		runLookupTest(Country.class, crm::findCountries, crm::findCountryByCode, crm::findCountryByLocalizedName);
		
		/* test language lookups */
		runLookupTest(Language.class, crm::findLanguages, crm::findLanguageByCode, crm::findLanguageByLocalizedName);
		
		/* test salutation lookups */
		runLookupTest(Salutation.class, crm::findSalutations, crm::findSalutationByCode, crm::findSalutationByLocalizedName);
		
		/* test sector lookups */
		runLookupTest(BusinessSector.class, crm::findBusinessSectors, crm::findBusinessSectorByCode, crm::findBusinessSectorByLocalizedName);
		
		/* test unit lookups */
		runLookupTest(BusinessUnit.class, crm::findBusinessUnits, crm::findBusinessUnitByCode, crm::findBusinessUnitByLocalizedName);
		
		/* test classification lookups */
		runLookupTest(BusinessClassification.class, crm::findBusinessClassifications, crm::findBusinessClassificationByCode, crm::findBusinessClassificationByLocalizedName);
		
		/* test province lookups */
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("CA"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("MX"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
		runQualifiedLookupTest(Province.class, crm::findProvinces, crm.findCountryByCode("US"), crm::findProvinceByCode, crm::findProvinceByLocalizedName);
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
			Assert.assertEquals(value, localizedLookup.apply(Lang.ROOT, value.getName(Lang.ROOT)));
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
	
	private <T extends CrmLookupItem> void runQualifiedLookupTest(Class<T> item, Function<String, List<T>> supplier, CrmLookupItem qualifier, BiFunction<String, String, T> codeLookup, TriFunction<Locale, String, String, T> localizedLookup) {
		/* countries tests */
		List<T> values = supplier.apply(qualifier.getCode());
		for (T value : values) {
			Assert.assertEquals(value, codeLookup.apply(value.getCode(), qualifier.getCode()));
			Assert.assertEquals(value, localizedLookup.apply(Lang.ENGLISH, value.getName(Lang.ENGLISH), qualifier.getName(Lang.ENGLISH)));
			Assert.assertEquals(value, localizedLookup.apply(Lang.FRENCH, value.getName(Lang.FRENCH), qualifier.getName(Lang.FRENCH)));
			try {
				localizedLookup.apply(Lang.ENGLISH, "????", qualifier.getName(Lang.ENGLISH));
				Assert.fail("Unsupported Value");
			} catch (ItemNotFoundException e) {
			}

			try {
				localizedLookup.apply(Locale.GERMAN, "", "Hans");
				Assert.fail("Unsupported Country");
			} catch (ItemNotFoundException e) {
			}
		}
	}
}
