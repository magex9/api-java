package ca.magex.crm.graphql.datafetcher;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import ca.magex.crm.api.exceptions.ApiException;

public class LookupDataFetcherTests extends AbstractDataFetcherTests {

	@Test
	public void lookupDataFetching() throws Exception {
		/* country lookups */
		JSONArray lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"COUNTRY");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"COUNTRY",
				"CA");
		Assert.assertNotNull(lookups);

		/* salutation lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"SALUTATION");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"SALUTATION",
				"1");
		Assert.assertNotNull(lookups);

		/* language lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"LANGUAGE");
		Assert.assertNotNull(lookups);
		System.out.println(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"LANGUAGE",
				"en");
		Assert.assertNotNull(lookups);

		/* sector lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"SECTOR");
		Assert.assertNotNull(lookups);
		System.out.println(lookups);

		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"SECTOR",
				"1");
		Assert.assertNotNull(lookups);

		/* unit lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"UNIT");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"UNIT",
				"1");
		Assert.assertNotNull(lookups);

		/* classification lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"CLASSIFICATION");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"CLASSIFICATION",
				"1");
		Assert.assertNotNull(lookups);

		/* status lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s) { code englishName frenchName } }",
				"STATUS");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, code: %s) { code englishName frenchName } }",
				"STATUS",
				"active");
		Assert.assertNotNull(lookups);
		
		/* invalid lookup */
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s) { code englishName frenchName } }",
					"PLANETS");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - Item not found: invalid category 'PLANETS'", api.getMessage());
		}
	}
}
