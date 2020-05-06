package ca.magex.crm.resource;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CrmLookupLoaderTest extends AbstractJUnit4SpringContextTests {

	@Autowired CrmLookupLoader crmLookupLoader;

	@Test
	public void testLoadBusinessClassifications() {
		List<BusinessClassification> roles = crmLookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv");
		Assert.assertEquals(roles.toString(), 4, roles.size());
	}
	
	@Test
	public void testLoadBusinessSectors() {
		List<BusinessSector> roles = crmLookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv");
		Assert.assertEquals(roles.toString(), 4, roles.size());
	}
	
	@Test
	public void testLoadBusinessUnits() {
		List<BusinessUnit> roles = crmLookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv");
		Assert.assertEquals(roles.toString(), 4, roles.size());
	}
	
	@Test
	public void testLoadCountries() {
		List<Country> roles = crmLookupLoader.loadLookup(Country.class, "Country.csv");
		Assert.assertEquals(roles.toString(), 252, roles.size());
	}
	
	@Test
	public void testLoadLanguages() {
		List<Language> roles = crmLookupLoader.loadLookup(Language.class, "Language.csv");
		Assert.assertEquals(roles.toString(), 2, roles.size());
	}
	
	@Test
	public void testLoadSalutations() {
		List<Salutation> roles = crmLookupLoader.loadLookup(Salutation.class, "Salutation.csv");
		Assert.assertEquals(roles.toString(), 3, roles.size());
	}
}
