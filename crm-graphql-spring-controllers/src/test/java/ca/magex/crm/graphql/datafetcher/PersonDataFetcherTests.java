package ca.magex.crm.graphql.datafetcher;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.system.Identifier;

public class PersonDataFetcherTests extends AbstractDataFetcherTests {

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
	public void personDataFetching() throws Exception {
		JSONObject person = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, name: { "
				+ "firstName: %s, middleName: %s, lastName: %s, salutation: %s}, address: { "
				+ "street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }, communication: {"
				+ "jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, position: {"
				+ "sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				orgId,
				"Jonny", "Michael", "Bigford", "3",
				"99 Blue Jays Way", "Toronto", "Ontario", "CA", "L9K5I9",
				"Developer", "English", "jonny.bigford@johnnuy.org", "6135551212", "97", "6135551213",
				"IT", "Solutions", "Senior Developer");
		Identifier personId = new Identifier(person.getString("personId"));
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("3", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* activate already active person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"active");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("3", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* inactivate active person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"inactive");
		Assert.assertEquals("INACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("3", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* inactivate inactive person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"inactive");
		Assert.assertEquals("INACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("3", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* activate inactive person */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"active");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", person.getString("displayName"));
		Assert.assertEquals("Jonny", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("3", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* pass invalid status */
		try {
			execute(
					"updatePerson",
					"mutation { updatePerson(personId: %s, status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
					personId,
					"suspended");
			Assert.fail("Should have failed on bad status");
		} catch (ApiException api) {
			Assert.assertEquals("Errors encountered during updatePerson - Invalid status 'SUSPENDED', one of {ACTIVE, INACTIVE} expected", api.getMessage());
		}
		
		/* update name with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, name: { firstName: %s, middleName: %s, lastName: %s, salutation: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"Timothy",
				"Baller",
				"McNugget",
				"2");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update name without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, name: { firstName: %s, middleName: %s, lastName: %s, salutation: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"Timothy",
				"Baller",
				"McNugget",
				"2");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update address with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, address: { street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"911 Sky Lane",
				"Peterborough",
				"ON",
				"CA",
				"G5K9R4");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update address without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, address: { street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"911 Sky Lane",
				"Peterborough",
				"ON",
				"CA",
				"G5K9R4");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update communication with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, communication: { jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"minion",
				"French",
				"minion@johnnuy.org",
				"6139995555",
				"",
				"6139995556");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update communication without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, communication: { jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"minion",
				"French",
				"minion@johnnuy.org",
				"6139995555",
				"",
				"6139995556");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", person.getJSONObject("position").getString("classification"));
		
		/* update position with change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, position: { sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"BUS",
				"Service",
				"Junior");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("BUS", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Service", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Junior", person.getJSONObject("position").getString("classification"));
		
		/* update position without change */
		person = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s, position: { sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId,
				"BUS",
				"Service",
				"Junior");
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("BUS", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Service", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Junior", person.getJSONObject("position").getString("classification"));
		
		/* find organization by id */
		person = execute(
				"findPerson",
				"{ findPerson(personId: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				personId);
		Assert.assertEquals("ACTIVE", person.getString("status"));
		Assert.assertEquals("McNugget, Timothy Baller", person.getString("displayName"));
		Assert.assertEquals("Timothy", person.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Baller", person.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("McNugget", person.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("2", person.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("911 Sky Lane", person.getJSONObject("address").getString("street"));
		Assert.assertEquals("Peterborough", person.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", person.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", person.getJSONObject("address").getString("country"));
		Assert.assertEquals("G5K9R4", person.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("minion", person.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", person.getJSONObject("communication").getString("language"));
		Assert.assertEquals("minion@johnnuy.org", person.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6139995555", person.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("", person.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6139995556", person.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("BUS", person.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Service", person.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Junior", person.getJSONObject("position").getString("classification"));
		
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
				"{ findPersons(filter: {organizationId: %s, displayName: %s, status: %s} paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { organizationId status displayName } } }",
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
		Assert.assertEquals(orgId.toString(), devContents.getJSONObject(0).get("organizationId"));
		Assert.assertEquals("McNugget, Timothy Baller", devContents.getJSONObject(0).get("displayName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}
}
