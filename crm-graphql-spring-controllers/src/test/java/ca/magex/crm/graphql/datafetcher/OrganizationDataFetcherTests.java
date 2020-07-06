package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.graphql.util.MapBuilder;

public class OrganizationDataFetcherTests extends AbstractDataFetcherTests {

	@Before
	public void createGroup() throws Exception {
//		execute(
//				"createGroup",
//				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
//				"DEV",
//				"developers",
//				"developeurs");
//
//		execute(
//				"createGroup",
//				"mutation { createGroup(code: %s, englishName: %s, frenchName: %s) { groupId code englishName frenchName status } }",
//				"OPS",
//				"operations",
//				"opèrations");
	}

	@Test
	public void organizationDataFetching() throws Exception {
		JSONObject johnnuy = executeWithVariables(
				"createOrganization",
				"mutation ($displayName: String!, $authenticationGroups: [String]!) { " + 
						"createOrganization(displayName: $displayName, authenticationGroups: $authenticationGroups) { " + 
							"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code } } } }",
				new MapBuilder().withEntry("displayName", "Johnnuy").withEntry("authenticationGroups", List.of("SYS", "ORG")).build()
				);
	
		OrganizationIdentifier johnnuyId = new OrganizationIdentifier(johnnuy.getString("organizationId"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));

//		/* activate already active organization */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"active");
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* inactivate active organization */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"inactive");
//		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* inactivate already inactive organization */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"inactive");
//		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* activate inactive organization */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"active");
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* pass invalid status */
//		try {
//			execute(
//					"updateOrganization",
//					"mutation { updateOrganization(organizationId: %s, status: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//					johnnuyId,
//					"suspended");
//			Assert.fail("Should have failed on bad status");
//		} catch (ApiException api) {
//			Assert.assertEquals("Errors encountered during updateOrganization - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
//		}
//
//		/* update display name */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, displayName: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"Johnnuy.org");
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update display name - no change */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, displayName: %s) { organizationId status displayName mainLocation { locationId } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				"Johnnuy.org");
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* set main location */
//		JSONObject hq = execute(
//				"createLocation",
//				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
//				johnnuyId,
//				"Head Quarters",
//				"HQ",
//				"123 Frank St",
//				"Ottawa",
//				"Ontario",
//				"CA",
//				"K5J9F4");
//		Identifier headQuartersId = new Identifier(hq.getString("locationId"));
//
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				headQuartersId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update location - no change */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				headQuartersId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update location - with change */
//		JSONObject hq2 = execute(
//				"createLocation",
//				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
//				johnnuyId,
//				"Head Quarters 2",
//				"HQ2",
//				"123 Frank St",
//				"Ottawa",
//				"Ontario",
//				"CA",
//				"K5J9F4");
//		Identifier headQuartersId2 = new Identifier(hq2.getString("locationId"));
//
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId } groups { englishName frenchName } } }",
//				johnnuyId,
//				headQuartersId2);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* set main contact */
//		JSONObject cio = execute(
//				"createPerson",
//				"mutation { createPerson(organizationId: %s, name: {firstName: %s, middleName: %s, lastName: %s, salutation: %s }, address: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}, communication: { jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s}, position: { sector: %s, unit: %s, classification: %s} ) { personId } }",
//				johnnuyId,
//				"Henry", "Peter", "Jones", "2",
//				"123 Frank St", "Ottawa", "Ontario", "CA", "K5J9F4",
//				"CIO", "EN", "cio@johnnuy.org", "613-555-5556", "97", "613-555-5557",
//				"IT", "Management", "CIO");
//		Identifier cioId = new Identifier(cio.getString("personId"));
//
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				cioId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(cioId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Jones, Henry Peter", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update contact - no change */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				cioId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(cioId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Jones, Henry Peter", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update contact - with change */
//		JSONObject ceo = execute(
//				"createPerson",
//				"mutation { createPerson(organizationId: %s, name: {firstName: %s, middleName: %s, lastName: %s, salutation: %s }, address: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}, communication: { jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s}, position: { sector: %s, unit: %s, classification: %s} ) { personId } }",
//				johnnuyId,
//				"Tommy", "Falls", "Narrow", "3",
//				"123 Frank St", "Ottawa", "Ontario", "CA", "K5J9F4",
//				"CIO", "EN", "cio@johnnuy.org", "613-555-5556", "97", "613-555-5557",
//				"IT", "Management", "CIO");
//		Identifier ceoId = new Identifier(ceo.getString("personId"));
//
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				ceoId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update groups - no change */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, groups: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				List.of("DEV", "OPS"));
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* update groups - remove group */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, groups: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				List.of("DEV"));
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(1, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//
//		/* update groups - change group */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, groups: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				List.of("OPS"));
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(1, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//
//		/* update groups - add groups */
//		johnnuy = execute(
//				"updateOrganization",
//				"mutation { updateOrganization(organizationId: %s, groups: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId,
//				List.of("DEV", "OPS"));
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//
//		/* find organization by id */
//		johnnuy = execute(
//				"findOrganization",
//				"{ findOrganization(organizationId: %s) { organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } groups { englishName frenchName } } }",
//				johnnuyId);
//		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
//		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
//		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
//		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
//		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
//		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
//		Assert.assertEquals(2, johnnuy.getJSONArray("groups").length());
//		Assert.assertEquals("developers", johnnuy.getJSONArray("groups").getJSONObject(0).get("englishName"));
//		Assert.assertEquals("developeurs", johnnuy.getJSONArray("groups").getJSONObject(0).get("frenchName"));
//		Assert.assertEquals("operations", johnnuy.getJSONArray("groups").getJSONObject(1).get("englishName"));
//		Assert.assertEquals("opèrations", johnnuy.getJSONArray("groups").getJSONObject(1).get("frenchName"));
//		
//		/* count orgs */
//		int orgCount = execute(
//				"countOrganizations",
//				"{ countOrganizations(filter: { status: %s } ) }",
//				"active");
//		Assert.assertEquals(2, orgCount);
//		
//		orgCount = execute(
//				"countOrganizations",
//				"{ countOrganizations(filter: { status: %s } ) }",
//				"inactive");
//		Assert.assertEquals(0, orgCount);
//		
//		/* find organization paging */
//		JSONObject organizations = execute(
//				"findOrganizations",
//				"{ findOrganizations(filter: {displayName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organizationId status displayName } } }",
//				"Johnnuy.org",
//				"active",
//				1,
//				5,
//				"englishName",
//				"ASC");
//		Assert.assertEquals(1, organizations.getInt("number"));
//		Assert.assertEquals(1, organizations.getInt("numberOfElements"));
//		Assert.assertEquals(5, organizations.getInt("size"));
//		Assert.assertEquals(1, organizations.getInt("totalPages"));
//		Assert.assertEquals(1, organizations.getInt("totalElements"));
//		JSONArray devContents = organizations.getJSONArray("content");
//		Assert.assertEquals(johnnuyId.toString(), devContents.getJSONObject(0).get("organizationId"));
//		Assert.assertEquals("Johnnuy.org", devContents.getJSONObject(0).get("displayName"));
//		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
