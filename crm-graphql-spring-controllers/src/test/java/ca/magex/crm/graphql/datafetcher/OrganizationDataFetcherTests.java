package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.graphql.util.MapBuilder;

/**
 * Tests for our Organization Mutations and Queries
 * 
 * @author Jonny
 */
public class OrganizationDataFetcherTests extends AbstractDataFetcherTests {

	@Test
	public void organizationDataFetching() throws Exception {
		JSONObject johnnuy = executeWithVariables(
				"createOrganization",
				"mutation ($displayName: String!, $authenticationGroups: [String]!, $businessGroups: [String]!) { " + 
						"createOrganization(displayName: $displayName, authenticationGroups: $authenticationGroups, businessGroups: $businessGroups) { " + 
							"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				new MapBuilder()
					.withEntry("displayName", "Johnnuy")
					.withEntry("authenticationGroups", List.of("SYS", "ORG"))
					.withEntry("businessGroups", List.of("IMIT", "IMIT/DEV")).build()
				);
	
		OrganizationIdentifier johnnuyId = new OrganizationIdentifier(johnnuy.getString("organizationId"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* activate already active organization */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, status: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"active");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* inactivate active organization */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, status: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"inactive");
		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* inactivate already inactive organization */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, status: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"inactive");
		Assert.assertEquals("INACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* activate inactive organization */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, status: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"active");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* pass invalid status */
		try {
			execute(
					"updateOrganization",
					"mutation { updateOrganization(organizationId: %s, status: %s) { " + 
							"organizationId status displayName mainLocation { locationId } mainContact { personId } } }",
					johnnuyId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updateOrganization - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}

		/* update display name */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, displayName: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"Johnnuy.org");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update display name - no change */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, displayName: %s) { " + 
						"organizationId status displayName mainLocation { locationId } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				"Johnnuy.org");
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* set main location */
		JSONObject hq = execute(
				"createLocation",
				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {" + 
						"street: %s, city: %s, province: {code: %s}, country: {code: %s}, postalCode: %s}) { locationId } }",
				johnnuyId,
				"Head Quarters",
				"HQ",
				"123 Frank St",
				"Ottawa",
				"CA/ON",
				"CA",
				"K5J9F4");
		LocationIdentifier headQuartersId = new LocationIdentifier(hq.getString("locationId"));

		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				headQuartersId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update location - no change */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				headQuartersId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update location - with change */
		JSONObject hq2 = execute(
				"createLocation",
				"mutation { createLocation(organizationId: %s, locationName: %s, locationReference: %s, locationAddress: {" + 
						"street: %s, city: %s, province: {code: %s}, country: {code: %s}, postalCode: %s}) { locationId } }",
				johnnuyId,
				"Head Quarters 2",
				"HQ2",
				"123 Frank St",
				"Ottawa",
				"CA/ON",
				"CA",
				"K5J9F4");
		LocationIdentifier headQuartersId2 = new LocationIdentifier(hq2.getString("locationId"));

		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainLocationId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				headQuartersId2);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainContact"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* set main contact */
		JSONObject cio = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, " + 
						"name: {firstName: %s, middleName: %s, lastName: %s, salutation: {code: %s} }, " + 
						"address: {street: %s, city: %s, province: {code: %s}, country: {code : %s}, postalCode: %s}, " + 
						"communication: { jobTitle: %s, language: {code: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s}, " + 
						"businessRoles: %s ) { personId } }",
				johnnuyId,
				"Henry", "Peter", "Jones", "MR",
				"123 Frank St", "Ottawa", "CA/ON", "CA", "K5J9F4",
				"CIO", "EN", "cio@johnnuy.org", "613-555-5556", "97", "613-555-5557",
				List.of("IMIT/DEV/MANAGER"));
		PersonIdentifier cioId = new PersonIdentifier(cio.getString("personId"));

		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				cioId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(cioId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Jones, Henry Peter", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update contact - no change */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				cioId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(cioId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Jones, Henry Peter", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update contact - with change */
		JSONObject ceo = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, " + 
						"name: {" + "firstName: %s, middleName: %s, lastName: %s, salutation: {code: %s} }, " + 
						"address: {street: %s, city: %s, province: {code: %s}, country: {code: %s}, postalCode: %s}, " + 
						"communication: { jobTitle: %s, language: {code: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s}, " + 
						"businessRoles: %s ) { personId } }",
				johnnuyId,
				"Tommy", "Falls", "Narrow", "MRS",
				"123 Frank St", "Ottawa", "CA/ON", "CA", "K5J9F4",
				"CIO", "EN", "cio@johnnuy.org", "613-555-5556", "97", "613-555-5557",
				List.of("IMIT/DEV/MANAGER"));
		PersonIdentifier ceoId = new PersonIdentifier(ceo.getString("personId"));

		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, mainContactId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				ceoId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update authentication groups - no change */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, authenticationGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("SYS", "ORG"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update groups - remove group */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, authenticationGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("SYS"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(1, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));		
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update groups - change group */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, authenticationGroups: %s) { " +
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("ORG"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(1, johnnuy.getJSONArray("authenticationGroups").length());		
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update groups - add groups */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, authenticationGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("SYS", "ORG"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* update authentication groups - no change */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, businessGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("IMIT", "IMIT/DEV"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));

		/* update groups - remove group */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, businessGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("IMIT"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(1, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));		

		/* update groups - change group */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, businessGroups: %s) { " +
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("IMIT/DEV"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(1, johnnuy.getJSONArray("businessGroups").length());		
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));

		/* update groups - add groups */
		johnnuy = execute(
				"updateOrganization",
				"mutation { updateOrganization(organizationId: %s, businessGroups: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId,
				List.of("IMIT", "IMIT/DEV"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* find organization by id */
		johnnuy = execute(
				"findOrganization",
				"{ findOrganization(organizationId: %s) { " + 
						"organizationId status displayName mainLocation { locationId reference } mainContact { personId displayName } authenticationGroups { name { code english french } } businessGroups { name { code english french } } } }",
				johnnuyId);
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy.org", johnnuy.get("displayName"));
		Assert.assertEquals(headQuartersId2.toString(), johnnuy.getJSONObject("mainLocation").getString("locationId"));
		Assert.assertEquals("HQ2", johnnuy.getJSONObject("mainLocation").getString("reference"));
		Assert.assertEquals(ceoId.toString(), johnnuy.getJSONObject("mainContact").getString("personId"));
		Assert.assertEquals("Narrow, Tommy Falls", johnnuy.getJSONObject("mainContact").getString("displayName"));
		Assert.assertEquals(2, johnnuy.getJSONArray("authenticationGroups").length());
		Assert.assertEquals("SYS", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("System", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Système", johnnuy.getJSONArray("authenticationGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("ORG", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Organization", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Organisation", johnnuy.getJSONArray("authenticationGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		Assert.assertEquals(2, johnnuy.getJSONArray("businessGroups").length());
		Assert.assertEquals("IMIT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("IM/IT", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("GI/TI", johnnuy.getJSONArray("businessGroups").getJSONObject(0).getJSONObject("name").getString("french"));
		Assert.assertEquals("IMIT/DEV", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("code"));
		Assert.assertEquals("Application Development", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("english"));
		Assert.assertEquals("Développement d'applications", johnnuy.getJSONArray("businessGroups").getJSONObject(1).getJSONObject("name").getString("french"));
		
		/* count orgs */
		int orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { status: %s } ) }",
				"active");
		Assert.assertEquals(2, orgCount);
		
		orgCount = execute(
				"countOrganizations",
				"{ countOrganizations(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, orgCount);
		
		/* find organization paging */
		JSONObject organizations = execute(
				"findOrganizations",
				"{ findOrganizations(filter: {displayName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organizationId status displayName } } }",
				"Johnnuy.org",
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
		Assert.assertEquals(johnnuyId.toString(), devContents.getJSONObject(0).get("organizationId"));
		Assert.assertEquals("Johnnuy.org", devContents.getJSONObject(0).get("displayName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
