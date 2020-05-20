package ca.magex.crm.graphql.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.util.buf.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.graphql.TestConfig;
import graphql.ExecutionResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		TestConfig.class
})
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
@Ignore
public class GraphQLCrmServicesTests {

	private Logger log = LoggerFactory.getLogger(getClass());
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired private GraphQLCrmServices graphQl;
	@Autowired private AmnesiaDB amnesiaDb;

	@Before
	public void before() {
		amnesiaDb.reset();
	}

	@SuppressWarnings("unchecked")
	private <T> T execute(String queryName, String query, Object... args) throws Exception {
		String formattedQuery = String.format(query, Arrays.asList(args).stream()				
				.map((arg) -> (arg instanceof List) ? StringUtils.join((List<String>) arg) : arg)				
				.map((arg) -> (arg instanceof String || arg instanceof Identifier) ? ("\"" + arg + "\"") : arg)
				.map((arg) -> arg == null ? "" : arg)
				.collect(Collectors.toList()).toArray());
		ExecutionResult result = graphQl.getGraphQL().execute(formattedQuery);
		Assert.assertEquals(result.getErrors().toString(), 0, result.getErrors().size());
		String resultAsJsonString = objectMapper.writeValueAsString(result.getData());
		log.info(formattedQuery + " --> " + resultAsJsonString);
		JSONObject json = new JSONObject(resultAsJsonString);
		Object o = json.get(queryName);
		if (o == JSONObject.NULL) {
			return null;
		}
		return (T) o;
	}

	@Test
	public void testGroups() throws Exception {
		/* create new group */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(englishName: %s, frenchName: %s) { groupId status englishName frenchName } }",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));

		/* update status with no change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId status englishName frenchName } }",
				devId,
				"active");
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));

		/* update english name and status */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, englishName: %s, status: %s) { groupId status englishName frenchName } }",
				devId,
				"the devs",
				"inactive");
		Assert.assertEquals("the devs", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("INACTIVE", developers.get("status"));

		/* update status with no change */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, status: %s) { groupId status englishName frenchName } }",
				devId,
				"inactive");
		Assert.assertEquals("the devs", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("INACTIVE", developers.get("status"));

		/* update french name only */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, frenchName: %s) { groupId status englishName frenchName } }",
				devId,
				"les devs");
		Assert.assertEquals("the devs", developers.get("englishName"));
		Assert.assertEquals("les devs", developers.get("frenchName"));
		Assert.assertEquals("INACTIVE", developers.get("status"));

		/* update both names and status */
		developers = execute(
				"updateGroup",
				"mutation { updateGroup(groupId: %s, englishName: %s, frenchName: %s, status: %s) { groupId status englishName frenchName } }",
				devId,
				"developers",
				"developeurs",
				"active");
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));

		/* find the group by id */
		developers = execute(
				"findGroup",
				"{ findGroup(groupId: %s) { groupId status englishName frenchName } }",
				devId);
		Assert.assertEquals("developers", developers.get("englishName"));
		Assert.assertEquals("developeurs", developers.get("frenchName"));
		Assert.assertEquals("ACTIVE", developers.get("status"));

		/* find the groups with paging */
		developers = execute(
				"findGroups",
				"{ findGroups(paging: {pageNumber: %d, pageSize: %d, sortField: [%s], sortOrder: [%s]}) { number numberOfElements size totalPages totalElements content { groupId status englishName frenchName } } }",
				1,
				5,
				"englishName",
				"ASC");
		Assert.assertEquals(1, developers.getInt("number"));
		Assert.assertEquals(1, developers.getInt("numberOfElements"));
		Assert.assertEquals(5, developers.getInt("size"));
		Assert.assertEquals(1, developers.getInt("totalPages"));
		Assert.assertEquals(1, developers.getInt("totalElements"));
		JSONArray devContents = developers.getJSONArray("content");
		Assert.assertEquals("developers", devContents.getJSONObject(0).get("englishName"));
		Assert.assertEquals("developeurs", devContents.getJSONObject(0).get("frenchName"));
		Assert.assertEquals("ACTIVE", devContents.getJSONObject(0).get("status"));
	}

	@Test
	public void testRoles() throws Exception {
		/* create new group */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(englishName: %s, frenchName: %s) { groupId status englishName frenchName } }",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));

		JSONObject admin = execute(
				"createRole",
				"mutation { createRole(groupId: %s, code: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				devId,
				"ADM",
				"administrator",
				"administrateur");
		Identifier adminId = new Identifier(admin.getString("roleId"));
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));

		/* update status with no change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"active");
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));

		/* update english name and status */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, englishName: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"the admins",
				"inactive");
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("the admins", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("INACTIVE", admin.get("status"));

		/* update status with no change */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"inactive");
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("the admins", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("INACTIVE", admin.get("status"));

		/* update french name only */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"les admins");
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("the admins", admin.get("englishName"));
		Assert.assertEquals("les admins", admin.get("frenchName"));
		Assert.assertEquals("INACTIVE", admin.get("status"));

		/* update both names and status */
		admin = execute(
				"updateRole",
				"mutation { updateRole(roleId: %s, englishName: %s, frenchName: %s, status: %s) { roleId code groupId status englishName frenchName } }",
				adminId,
				"administrator",
				"administrateur",
				"active");
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));

		/* find the role by id */
		admin = execute(
				"findRole",
				"{ findRole(roleId: %s) { roleId code groupId status englishName frenchName } }",
				adminId);
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
	}

	@Test
	public void testLocations() throws Exception {
		JSONObject johnnuy = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s) { organizationId status displayName groups { englishName frenchName } } }",
				"Johnnuy");
		Identifier johnnuyId = new Identifier(johnnuy.getString("organizationId"));

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
		Assert.assertEquals(johnnuyId.toString(), hq.getString("organizationId"));
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("123 Frank St", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Ottawa", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("CA", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9F4", hq.getJSONObject("address").getString("postalCode"));

		/* update location information */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, locationName: %s, locationAddress: {street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s}, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,
				"Johnnuy Head Quarters",
				"234 George Av",
				"Nepean",
				"ON",
				"Canada", 
				"K5J9E9",
				"active");
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Johnnuy Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("234 George Av", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Nepean", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9E9", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location status */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,				
				"inactive");
		Assert.assertEquals("INACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Johnnuy Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("234 George Av", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Nepean", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9E9", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location status no change */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,				
				"inactive");
		Assert.assertEquals("INACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Johnnuy Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("234 George Av", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Nepean", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9E9", hq.getJSONObject("address").getString("postalCode"));
		
		/* update location status back to active */
		hq = execute(
				"updateLocation",
				"mutation { updateLocation(locationId: %s, status: %s) { locationId organizationId status reference displayName address { street city province country postalCode } } }",
				headQuartersId,				
				"active");
		Assert.assertEquals("ACTIVE", hq.getString("status"));
		Assert.assertEquals("HQ", hq.getString("reference"));
		Assert.assertEquals("Johnnuy Head Quarters", hq.getString("displayName"));
		Assert.assertEquals("234 George Av", hq.getJSONObject("address").getString("street"));
		Assert.assertEquals("Nepean", hq.getJSONObject("address").getString("city"));
		Assert.assertEquals("ON", hq.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", hq.getJSONObject("address").getString("country"));
		Assert.assertEquals("K5J9E9", hq.getJSONObject("address").getString("postalCode"));

	}

	@Test
	public void testOrganizations() throws Exception {
		JSONObject johnnuy = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				"Johnnuy");
		Identifier johnnuyId = new Identifier(johnnuy.getString("organizationId"));
		Assert.assertEquals("ACTIVE", johnnuy.get("status"));
		Assert.assertEquals("Johnnuy", johnnuy.get("displayName"));
		Assert.assertEquals(JSONObject.NULL, johnnuy.get("mainLocation"));
		Assert.assertEquals(0, johnnuy.getJSONArray("groups").length());

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

	@Test
	public void testPersons() throws Exception {
		JSONObject johnnuy = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				"Johnnuy");
		Identifier johnnuyId = new Identifier(johnnuy.getString("organizationId"));
		
		JSONObject jonny = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, name: { "
				+ "firstName: %s, middleName: %s, lastName: %s, salutation: %s}, address: { "
				+ "street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }, communication: {"
				+ "jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, position: {"
				+ "sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				johnnuyId,
				"Jonny", "Michael", "Bigford", "Mr",
				"99 Blue Jays Way", "Toronto", "Ontario", "Canada", "L9K5I9",
				"Developer", "English", "jonny.bigford@johnnuy.org", "6135551212", "97", "6135551213",
				"IT", "Solutions", "Senior Developer");
		
		Identifier jonnyId = new Identifier(jonny.getString("personId"));
		Assert.assertEquals("ACTIVE", jonny.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", jonny.getString("displayName"));
		Assert.assertEquals("Jonny", jonny.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Michael", jonny.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", jonny.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("Mr", jonny.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("99 Blue Jays Way", jonny.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", jonny.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", jonny.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", jonny.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", jonny.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Developer", jonny.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("English", jonny.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", jonny.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551212", jonny.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("97", jonny.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551213", jonny.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IT", jonny.getJSONObject("position").getString("sector"));
		Assert.assertEquals("Solutions", jonny.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Developer", jonny.getJSONObject("position").getString("classification"));
		
		jonny = execute(
				"updatePerson",
				"mutation { updatePerson(personId: %s,  name: { "
				+ "firstName: %s, middleName: %s, lastName: %s, salutation: %s}, address: { "
				+ "street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }, communication: {"
				+ "jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, position: {"
				+ "sector: %s, unit: %s, classification: %s } status: %s) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				jonnyId,
				"Jonny", "Adams", "Bigford", "Mrs",
				"98 Blue Jays Way", "Toronto", "Ontario", "Canada", "L9K5I9",
				"Tester", "French", "jonny.bigford2@johnnuy.org", "6135551211", "96", "6135551212",
				"IM", "QA", "Senior Tester", "active");
		Assert.assertEquals("ACTIVE", jonny.getString("status"));
		Assert.assertEquals("Bigford, Jonny Adams", jonny.getString("displayName"));
		Assert.assertEquals("Jonny", jonny.getJSONObject("legalName").getString("firstName"));
		Assert.assertEquals("Adams", jonny.getJSONObject("legalName").getString("middleName"));
		Assert.assertEquals("Bigford", jonny.getJSONObject("legalName").getString("lastName"));		
		Assert.assertEquals("Mrs", jonny.getJSONObject("legalName").getString("salutation"));
		Assert.assertEquals("98 Blue Jays Way", jonny.getJSONObject("address").getString("street"));
		Assert.assertEquals("Toronto", jonny.getJSONObject("address").getString("city"));
		Assert.assertEquals("Ontario", jonny.getJSONObject("address").getString("province"));
		Assert.assertEquals("Canada", jonny.getJSONObject("address").getString("country"));
		Assert.assertEquals("L9K5I9", jonny.getJSONObject("address").getString("postalCode"));
		Assert.assertEquals("Tester", jonny.getJSONObject("communication").getString("jobTitle"));
		Assert.assertEquals("French", jonny.getJSONObject("communication").getString("language"));
		Assert.assertEquals("jonny.bigford2@johnnuy.org", jonny.getJSONObject("communication").getString("email"));
		Assert.assertEquals("6135551211", jonny.getJSONObject("communication").getJSONObject("homePhone").getString("number"));
		Assert.assertEquals("96", jonny.getJSONObject("communication").getJSONObject("homePhone").getString("extension"));
		Assert.assertEquals("6135551212", jonny.getJSONObject("communication").getString("faxNumber"));
		Assert.assertEquals("IM", jonny.getJSONObject("position").getString("sector"));
		Assert.assertEquals("QA", jonny.getJSONObject("position").getString("unit"));
		Assert.assertEquals("Senior Tester", jonny.getJSONObject("position").getString("classification"));
	}

	@Test
	public void testUsers() throws Exception {
		/* create new group */
		JSONObject developers = execute(
				"createGroup",
				"mutation { createGroup(englishName: %s, frenchName: %s) { groupId status englishName frenchName } }",
				"developers",
				"developeurs");
		Identifier devId = new Identifier(developers.getString("groupId"));

		JSONObject admin = execute(
				"createRole",
				"mutation { createRole(groupId: %s, code: %s, englishName: %s, frenchName: %s) { roleId code groupId status englishName frenchName } }",
				devId,
				"ADM",
				"administrator",
				"administrateur");		
		Assert.assertEquals("ADM", admin.get("code"));
		Assert.assertEquals(devId.toString(), admin.get("groupId"));
		Assert.assertEquals("administrator", admin.get("englishName"));
		Assert.assertEquals("administrateur", admin.get("frenchName"));
		Assert.assertEquals("ACTIVE", admin.get("status"));
		
		JSONObject johnnuy = execute(
				"createOrganization",
				"mutation { createOrganization(displayName: %s) { organizationId status displayName mainLocation { locationId } groups { englishName frenchName } } }",
				"Johnnuy");
		Identifier johnnuyId = new Identifier(johnnuy.getString("organizationId"));
		
		JSONObject jonny = execute(
				"createPerson",
				"mutation { createPerson(organizationId: %s, name: { "
				+ "firstName: %s, middleName: %s, lastName: %s, salutation: %s}, address: { "
				+ "street: %s, city: %s, province: %s, countryCode: %s, postalCode: %s }, communication: {"
				+ "jobTitle: %s, language: %s, email: %s, phoneNumber: %s, phoneExtension: %s, faxNumber: %s }, position: {"
				+ "sector: %s, unit: %s, classification: %s }) { personId organizationId status displayName legalName { firstName middleName lastName salutation } address { street city province country postalCode } communication { jobTitle language email homePhone { number extension } faxNumber } position { sector unit classification} } }",
				johnnuyId,
				"Jonny", "Michael", "Bigford", "Mr",
				"99 Blue Jays Way", "Toronto", "Ontario", "Canada", "L9K5I9",
				"Developer", "English", "jonny.bigford@johnnuy.org", "6135551212", "97", "6135551213",
				"IT", "Solutions", "Senior Developer");		
		Identifier jonnyId = new Identifier(jonny.getString("personId"));
		
		JSONObject user = execute(
				"createUser",
				"mutation { createUser(personId: %s, username: %s, roles: [%s]) { userId status person { displayName communication { email } } } }",
				jonnyId,
				"jbigford",
				Arrays.asList(admin.getString("code")));
		Identifier userId = new Identifier(user.getString("userId"));
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
		
		user = execute(
				"updateUser",
				"mutation { updateUser(userId: %s, roles: [%s], status: %s) { userId status person { displayName communication { email } } } }",
				userId,
				null,
				"active");
		Assert.assertEquals("ACTIVE", user.getString("status"));
		Assert.assertEquals("Bigford, Jonny Michael", user.getJSONObject("person").getString("displayName"));
		Assert.assertEquals("jonny.bigford@johnnuy.org", user.getJSONObject("person").getJSONObject("communication").getString("email"));
	}

}
