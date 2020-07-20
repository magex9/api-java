package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.graphql.util.MapBuilder;

public class PersonDataFetcherTests extends AbstractDataFetcherTests {

	private OrganizationIdentifier orgId;
	
	@Before
	public void setup() throws Exception {		
		JSONObject johnnuy = executeWithVariables(
				"createOrganization",
				"mutation ($displayName: String!, $authenticationGroupIds: [String]!, $businessGroupIds: [String]!) { " + 
						"createOrganization(displayName: $displayName, authenticationGroupIds: $authenticationGroupIds, businessGroupIds: $businessGroupIds) { " + 
							"organizationId } }",
				new MapBuilder()
					.withEntry("displayName", "Johnnuy")
					.withEntry("authenticationGroupIds", List.of("SYS", "CRM"))
					.withEntry("businessGroupIds", List.of("IMIT", "IMIT/DEV")).build()
				);
		orgId = new OrganizationIdentifier(johnnuy.getString("organizationId"));
	}
	
	@Test
	public void personDataFetching() throws Exception {
		JSONObject person = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, " + 
						"name: {firstName: %s, middleName: %s, lastName: %s, salutation: {identifier: %s} }, " + 
						"address: {street: %s, city: %s, province: {identifier: %s}, country: {identifier: %s}, postalCode: %s }, " + 
						"communication: {jobTitle: %s, language: {identifier: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, " +
						"businessRoleIds: %s ) " + 
						"{ personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				orgId,
				"Jonny", "Michael", "Bigford", "MR",
				"99 Blue Jays Way", "Toronto", "CA/ON", "CA", "L9K5I9",
				"Developer", "EN", "jonny.bigford@johnnuy.org", "6135551212", "97", "613-555-1213",
				List.of("IMIT/DEV/MANAGER"));
		PersonIdentifier personId = new PersonIdentifier(person.getString("personId"));
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MR", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* activate already active person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"active");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MR", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* inactivate active person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"inactive");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("INACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MR", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* inactivate inactive person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"inactive");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("INACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MR", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* activate inactive person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"active");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MR", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* pass invalid status */
		try {
			execute(
					"updatePerson",
					"mutation { updatePerson(personId: %s, status: %s) { personId } }",
					personId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updatePerson - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update name with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, name: { firstName: %s, middleName: %s, lastName: %s, salutation: {identifier: %s} }) { " + 
							"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"Timothy",
				"Baller",
				"McNugget",
				"MRS");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update name without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, name: { firstName: %s, middleName: %s, lastName: %s, salutation: {identifier: %s} }) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"Timothy",
				"Baller",
				"McNugget",
				"MRS");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update address with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, address: { street: %s, city: %s, province: {identifier: %s}, country: {identifier: %s}, postalCode: %s }) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"911 Sky Lane",
				"Peterborough",
				"CA/ON",
				"CA",
				"G5K9R4");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update address without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, address: { street: %s, city: %s, province: {identifier: %s}, country: {identifier: %s}, postalCode: %s }) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"911 Sky Lane",
				"Peterborough",
				"CA/ON",
				"CA",
				"G5K9R4");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("EN", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("613-555-1213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update communication with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, communication: { jobTitle: %s, language: {identifier: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"minion",
				"FR",
				"minion@johnnuy.org",
				"6139995555",
				"",
				"6139995556");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("FR", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update communication without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, communication: { jobTitle: %s, language: {identifier: %s}, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				"minion",
				"FR",
				"minion@johnnuy.org",
				"6139995555",
				"",
				"6139995556");
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("FR", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/MANAGER", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Manager", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Gestionnaire", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update position with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, businessRoleIds: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				List.of("IMIT/DEV/QA/TEAMLEAD"));
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("FR", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/QA/TEAMLEAD", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Team Lead", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Chef d'équipe", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* update position without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, businessRoleIds: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId,
				List.of("IMIT/DEV/QA/TEAMLEAD"));
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("FR", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/QA/TEAMLEAD", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Team Lead", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Chef d'équipe", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* find organization by id */
		person = execute(
				"findPerson",
				"{ findPerson(personId: %s) { " + 
						"personId organization { organizationId } status displayName legalName { firstName middleName lastName salutation { identifier } } address { street city province { identifier } country { identifier } postalCode } communication { jobTitle language { identifier } email homePhone { number extension } faxNumber } businessRoles { name { code english french } } } }",
				personId);
		Assert.assertEquals(orgId.toString(), person.getJSONObject("organization").getString("organizationId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("MRS", person.getJSONObject("legalName").getJSONObject("salutation").getString("identifier"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("CA/ON", person.getJSONObject("address").getJSONObject("province").getString("identifier"));
		Assert.assertEquals("CA", person.getJSONObject("address").getJSONObject("country").getString("identifier"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("FR", person.getJSONObject("communication").getJSONObject("language").getString("identifier"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals(1, person.getJSONArray("businessRoles").length());
		Assert.assertEquals("IMIT/DEV/QA/TEAMLEAD", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("code"));
		Assert.assertEquals("Team Lead", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("english"));
		Assert.assertEquals("Chef d'équipe", person.getJSONArray("businessRoles").getJSONObject(0).getJSONObject("name").getString("french"));
		
		/* count persons */
		int personCount = execute(
				"countPersons",
				"{ countPersons(filter: { status: %s } ) }",
				"active");
		Assert.assertEquals(2, personCount);
		
		personCount = execute(
				"countPersons",
				"{ countPersons(filter: { status: %s } ) }",
				"inactive");
		Assert.assertEquals(0, personCount);
		
		/* find organization paging */
		JSONObject persons = execute(
				"findPersons",
				"{ findPersons(filter: {organizationId: %s, displayName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organization { organizationId } status displayName } } }",
				orgId,
				"Baller",
				"active",
				1,
				5,
				"englishName",
				"ASC");
		Assert.assertEquals(1, persons.getInt("number"));
		Assert.assertEquals(1, persons.getInt("numberOfElements"));
		Assert.assertEquals(5, persons.getInt("size"));
		Assert.assertEquals(1, persons.getInt("totalPages"));
		Assert.assertEquals(1, persons.getInt("totalElements"));
		JSONArray devContents = persons.getJSONArray("content");
		Assert.assertEquals(orgId.toString(), devContents.getJSONObject(0).getJSONObject("organization").get("organizationId"));
		Assert.assertEquals("McNugget, Timothy Baller", devContents.getJSONObject(0).get("displayName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
