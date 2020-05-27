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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"COUNTRY",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for country lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"SALUTATION",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for salutation lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"LANGUAGE",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for language lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"SECTOR",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for sector lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"UNIT",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for unit lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"CLASSIFICATION",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for classification lookup", api.getMessage());
		}

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
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
					"STATUS",
					"ABC");
			Assert.fail("should fail with qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier not required for status lookup", api.getMessage());
		}
		
		/* province lookups */
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, qualifier: %s) { code englishName frenchName } }",
				"PROVINCE",
				"CA");
		Assert.assertNotNull(lookups);
		
		lookups = execute(
				"findCodeLookups",
				"{ findCodeLookups(category: %s, qualifier: %s, code: %s) { code englishName frenchName } }",
				"PROVINCE",
				"CA",
				"ON");
		Assert.assertNotNull(lookups);
		
		try {
			execute(
					"findCodeLookups",
					"{ findCodeLookups(category: %s) { code englishName frenchName } }",
					"PROVINCE");
			Assert.fail("should fail without qualifier provided");
		}
		catch (ApiException api) {
			Assert.assertEquals("Errors encountered during findCodeLookups - qualifier required for province lookup", api.getMessage());
		}
		
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
