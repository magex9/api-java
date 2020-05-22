package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;

public class LocationDataFetcherTests extends AbstractDataFetcherTests {

	private String orgId;
	
	@Before
	public void createGroup() throws Exception {
		execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId } }",
				"DEV",
				"developers",
				"developeurs");

		execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId } }",
				"OPS",
				"operations",
				"opèrations");
		
		JSONObject org = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s, groups: %s) { organizationId } }",
				"Johnnuy",
				List.of("DEV", "OPS"));
		orgId = org.getString("organizationId");
	}
	
	@Test
	public void locationDataFetching() throws Exception {
		JSONObject hq = execute(
				"createLocation",
				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				orgId,
				"Head Quarters",
				"HQ",
				"123 Frank St",
				"Ottawa",
				"Ontario",
				"CA",
				"K5J9F4");
		Identifier headQuartersId = new Identifier(hq.getString("locationId"));
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* activate already active location */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"active");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* inactivate active location */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"inactive");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("INACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* inactivate inactive location */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"inactive");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("INACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* activate inactive location */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"active");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* pass invalid status */
		try {
			execute(
					"updateLocation",
					"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
					headQuartersId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updateLocation - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update location name with change */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, locationName: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"The Mansion");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("The Mansion", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location name with no change */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, locationName: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"The Mansion");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("The Mansion", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location address with change */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"299 Bobby Rd",
				"Gatineau",
				"Quebec",
				"CA",
				"L5K0F4"); 
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("The Mansion", hq.getString("displayName"));
		Assert.assertEquals("299 Bobby Rd", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Gatineau", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Quebec", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("L5K0F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location address with no change */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"299 Bobby Rd",
				"Gatineau",
				"Quebec",
				"CA",
				"L5K0F4"); 
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("The Mansion", hq.getString("displayName"));
		Assert.assertEquals("299 Bobby Rd", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Gatineau", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Quebec", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("L5K0F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* find by id */
		hq = execute(
				"findLocation",
				"{ findLocation(locationId: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"299 Bobby Rd",
				"Gatineau",
				"Quebec",
				"CA",
				"L5K0F4");
		Assert.assertEquals(orgId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("The Mansion", hq.getString("displayName"));
		Assert.assertEquals("299 Bobby Rd", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Gatineau", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Quebec", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("L5K0F4", hq.getJSONObject("address").getString("postalCode"));
		
		/* count locations */
		int orgCount = execute(
				"countLocations",
				"{ countLocations(filter: { status: %s } ) }",
				"active");
		Assert.assertEquals(1, orgCount);
		
		orgCount = execute(
				"countLocations",
				"{ countLocations(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, orgCount);
		
		/* page locations */
		JSONObject organizations = execute(
				"findLocations",
				"{ findLocations(filter: {organizationId: %s, displayName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { locationId status displayName } } }",
				orgId,
				"Mansion",
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
		Assert.assertEquals(headQuartersId.toString(), devContents.getJSONObject(0).get("locationId"));
		Assert.assertEquals("The Mansion", devContents.getJSONObject(0).get("displayName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
		
	}
}
