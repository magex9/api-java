package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.system.Identifier;

public class OrganizationDataFetcherTests extends AbstractDataFetcherTests {

	@Before
	public void createGroup() throws Exception {
		execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				"DEV",
				"developers",
				"developeurs");
		
		execute(
				"createGroup",
				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
				"OPS",
				"operations",
				"opèrations");
	}
	
	@Test
	public void organizationDataFetching() throws Exception {
		JSONObject johnnuy = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s, groups: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				"Johnnuy",
				List.of("DEV", "OPS"));
		Identifier johnnuyId = new Identifier(johnnuy.getString("organizationId"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));

		JSONObject hq = execute(
				"createLocation",
				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				johnnuyId,
				"Head Quarters",
				"HQ",
				"123 Frank St",
				"Ottawa",
				"Ontario",
				"CA",
				"K5J9F4");
		Identifier headQuartersId = new Identifier(hq.getString("locationId"));

		/* update the organization details */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, displayName: %s, locationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				johnnuyId,
				"Johnnuy.org",
				headQuartersId,
				"active");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));

		/* update the organization details with status */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				johnnuyId,
				"inactive");
		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));

		/* update the organization details with status and name */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, displayName: %s, status: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				johnnuyId,
				"johnnuy industries",
				"inactive");
		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
		Assert.assertEquals("johnnuy industries", johnnuy.get("displayName"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));

		/* update the organization details with status and name */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, displayName: %s, status: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				johnnuyId,
				"Johnnuy",
				"active");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));

		johnnuy = execute(
				"findOrganization",
				"{ findOrganization(organizationId: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				johnnuyId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));

		/* should have 0 orgs matching %JJ% */
		Integer orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { displayName: %s, status: %s } ) }",
				"JJ",
				"ACTIVE");
		Assert.assertEquals(0, orgCount.intValue());

		/* should have 1 org matching %J% */
		orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { displayName: %s, status: %s } ) }",
				"John",
				"ACTIVE");
		Assert.assertEquals(1, orgCount.intValue());

		/* should have no inactive orgs */
		orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { status: %s } ) }",
				"INACTIVE");
		Assert.assertEquals(0, orgCount.intValue());

		/* should have one active org */
		orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { status: %s } ) }",
				"ACTIVE");
		Assert.assertEquals(1, orgCount.intValue());

		/* find our page of Active Orgs */
		JSONObject orgs = execute(
				"findOrganizations",
				"{ findOrganizations(filter: { status: %s }, paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organizationId status displayName } } }",
				"active",
				1,
				5,
				"displayName",
				"ASC");
		Assert.assertEquals(1, orgs.getInt("number"));
		Assert.assertEquals(1, orgs.getInt("numberOfElements"));
		Assert.assertEquals(5, orgs.getInt("size"));
		Assert.assertEquals(1, orgs.getInt("totalPages"));
		Assert.assertEquals(1, orgs.getInt("totalElements"));
		JSONArray content = orgs.getJSONArray("content");
		Assert.assertEquals(1, content.length());
		Assert.assertNotNull(content.getJSONObject(0).getString("organizationId"));
		Assert.assertEquals("Johnnuy", content.getJSONObject(0).getString("displayName"));
		Assert.assertEquals("ACTIVE", content.getJSONObject(0).getString("status"));
		Assert.assertFalse(content.getJSONObject(0).has("mainLocation"));

		/* find our page of Inactive Orgs */
		orgs = execute(
				"findOrganizations",
				"{ findOrganizations(filter: { status: %s }, paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organizationId status displayName } } }",
				"inactive",
				1,
				5,
				"displayName",
				"ASC");
		Assert.assertEquals(1, orgs.getInt("number"));
		Assert.assertEquals(0, orgs.getInt("numberOfElements"));
		Assert.assertEquals(5, orgs.getInt("size"));
		Assert.assertEquals(0, orgs.getInt("totalPages"));
		Assert.assertEquals(0, orgs.getInt("totalElements"));
		content = orgs.getJSONArray("content");
		Assert.assertEquals(0, content.length());
	}
}
