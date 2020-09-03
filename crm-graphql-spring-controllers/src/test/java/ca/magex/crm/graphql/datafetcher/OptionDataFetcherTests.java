package ca.magex.crm.graphql.datafetcher;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.CountryIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.ProvinceIdentifier;

public class OptionDataFetcherTests extends AbstractDataFetcherTests {

	@Test
	public void optionDataFetching() throws Exception {
		JSONObject petoria = execute(
				"createOption",
				"mutation { createOption(type: %s, name: { code: %s, english: %s, french: %s }) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				Type.COUNTRY.getCode(),
				"PT",
				"Petoria",
				"Pètorie");

		CountryIdentifier petoriaId = new CountryIdentifier(petoria.getString("optionId"));
		Assert.assertEquals(JSONObject.NULL, petoria.get("parent"));
		Assert.assertEquals(Type.COUNTRY.getCode(), petoria.getString("type"));
		Assert.assertEquals("ACTIVE", petoria.getString("status"));
		Assert.assertTrue(petoria.getBoolean("mutable"));
		Assert.assertEquals("PT", petoria.getJSONObject("name").getString("code"));
		Assert.assertEquals("Petoria", petoria.getJSONObject("name").getString("english"));
		Assert.assertEquals("Pètorie", petoria.getJSONObject("name").getString("french"));

		/* activate already active option */
		try {
			petoria = execute(
				"enableOption",
				"mutation { enableOption(optionId: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId);
			Assert.fail("Already active");
		} catch (ApiException e) { }

		/* inactivate active option */
		petoria = execute(
				"disableOption",
				"mutation { disableOption(optionId: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId);
		Assert.assertEquals(JSONObject.NULL, petoria.get("parent"));
		Assert.assertEquals(Type.COUNTRY.getCode(), petoria.getString("type"));
		Assert.assertEquals("INACTIVE", petoria.getString("status"));
		Assert.assertTrue(petoria.getBoolean("mutable"));
		Assert.assertEquals("PT", petoria.getJSONObject("name").getString("code"));
		Assert.assertEquals("Petoria", petoria.getJSONObject("name").getString("english"));
		Assert.assertEquals("Pètorie", petoria.getJSONObject("name").getString("french"));

		/* inactivate already inactive option */
		try {
			petoria = execute(
				"disableOption",
				"mutation { disableOption(optionId: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId);
			Assert.fail("Already inactive");
		} catch (ApiException e) { }

		/* activate inactive option */
		petoria = execute(
				"enableOption",
				"mutation { enableOption(optionId: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId);
		Assert.assertEquals(JSONObject.NULL, petoria.get("parent"));
		Assert.assertEquals(Type.COUNTRY.getCode(), petoria.getString("type"));
		Assert.assertEquals("ACTIVE", petoria.getString("status"));
		Assert.assertTrue(petoria.getBoolean("mutable"));
		Assert.assertEquals("PT", petoria.getJSONObject("name").getString("code"));
		Assert.assertEquals("Petoria", petoria.getJSONObject("name").getString("english"));
		Assert.assertEquals("Pètorie", petoria.getJSONObject("name").getString("french"));

		/* update english name */
		petoria = execute(
				"updateOption",
				"mutation { updateOption(optionId: %s, english: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId,
				"Petoria Island");
		Assert.assertEquals(JSONObject.NULL, petoria.get("parent"));
		Assert.assertEquals(Type.COUNTRY.getCode(), petoria.getString("type"));
		Assert.assertEquals("ACTIVE", petoria.getString("status"));
		Assert.assertTrue(petoria.getBoolean("mutable"));
		Assert.assertEquals("PT", petoria.getJSONObject("name").getString("code"));
		Assert.assertEquals("Petoria Island", petoria.getJSONObject("name").getString("english"));
		Assert.assertEquals("Pètorie", petoria.getJSONObject("name").getString("french"));

		/* update french name */
		petoria = execute(
				"updateOption",
				"mutation { updateOption(optionId: %s, french: %s) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				petoriaId,
				"Isle Pètorie");
		Assert.assertEquals(JSONObject.NULL, petoria.get("parent"));
		Assert.assertEquals(Type.COUNTRY.getCode(), petoria.getString("type"));
		Assert.assertEquals("ACTIVE", petoria.getString("status"));
		Assert.assertTrue(petoria.getBoolean("mutable"));
		Assert.assertEquals("PT", petoria.getJSONObject("name").getString("code"));
		Assert.assertEquals("Petoria Island", petoria.getJSONObject("name").getString("english"));
		Assert.assertEquals("Isle Pètorie", petoria.getJSONObject("name").getString("french"));

		/* create nested options */
		JSONObject megville = execute(
				"createOption",
				"mutation { createOption(type: %s, parentId: %s, name: { code: %s, english: %s, french: %s }) { " +
						"optionId parent { optionId } type status mutable name { code english french } } }",
				Type.PROVINCE.getCode(),
				petoriaId,
				"MV",
				"Megville",
				"Mègville");
		OptionIdentifier megvillId = new ProvinceIdentifier(megville.getString("optionId"));
		Assert.assertEquals(petoriaId.toString(), megville.getJSONObject("parent").getString("optionId"));
		Assert.assertEquals(Type.PROVINCE.getCode(), megville.getString("type"));
		Assert.assertEquals("ACTIVE", megville.getString("status"));
		Assert.assertTrue(megville.getBoolean("mutable"));
		Assert.assertEquals("PT/MV", megville.getJSONObject("name").getString("code"));
		Assert.assertEquals("Megville", megville.getJSONObject("name").getString("english"));
		Assert.assertEquals("Mègville", megville.getJSONObject("name").getString("french"));
		
		/* count options */
		int optionCount = execute(
				"countOptions",
				"{ countOptions(filter: { english: %s, status: %s } ) }",
				"Megville",
				"active");
		Assert.assertEquals(1, optionCount);
		
		optionCount = execute(
				"countOptions",
				"{ countOptions(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, optionCount);
		
		/* page locations */
		JSONObject organizations = execute(
				"findOptions",
				"{ findOptions(filter: {english: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { " + 
						"number numberOfElements size totalPages totalElements content { optionId status name { english } } } }",
				"Megville",				
				"active",
				1,
				5,
				"englishName",
				"ASC");
		Assert.assertEquals(1, organizations.getInt("number"));
		Assert.assertEquals(1, organizations.getInt("numberOfElements"));
		Assert.assertEquals(5, organizations.getInt("size"));
		Assert.assertEquals(1, organizations.getInt("totalPages"));
		Assert.assertEquals(1, organizations.getInt("totalElements"));
		JSONArray devContents = organizations.getJSONArray("content");
		Assert.assertEquals(megvillId.toString(), devContents.getJSONObject(0).get("optionId"));
		Assert.assertEquals("Megville", devContents.getJSONObject(0).getJSONObject("name").get("english"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
