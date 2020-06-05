package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.BUSINESS_POSITION;
import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.ONTARIO;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaServices;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.transform.json.JsonTransformer;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

public class PersonTransformerTest {

//	@Test
//	public void testPersonDetailFormatter() throws Exception {
//		CrmServices crm = new AmnesiaServices();
//		Identifier personId = new Identifier("abc");
//		Identifier organizationId = new Identifier("xyz");
//		Status status = Status.PENDING;
//		List<String> roles = new ArrayList<String>();
//		Group group = crm.createGroup(SYS);
//		roles.add(crm.createRole(group.getGroupId(), SYS_ADMIN).getRoleId().toString());
//		roles.add(crm.createRole(group.getGroupId(), ORG_ADMIN).getRoleId().toString());
//		
//		PersonDetails person = new PersonDetails(personId, organizationId, status, PERSON_NAME.getDisplayName(), 
//				PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, BUSINESS_POSITION);
//		
//		JsonObject english = new JsonTransformer(crm, Lang.ENGLISH, false).formatPersonDetails(person);
//		
//		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication"), english.keys());
//		assertEquals("abc", english.getString("personId"));
//		assertEquals("xyz", english.getString("organizationId"));
//		assertEquals("Pending", english.getString("status"));
//		assertEquals("Bacon, Chris P", english.getString("displayName"));
//		
//		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
//		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
//		assertEquals("Chris", english.getObject("legalName").getString("firstName"));
//		assertEquals("P", english.getObject("legalName").getString("middleName"));
//		assertEquals("Bacon", english.getObject("legalName").getString("lastName"));
//		
//		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
//		assertEquals("123 Main St", english.getObject("address").getString("street"));
//		assertEquals("Ottawa", english.getObject("address").getString("city"));
//		assertEquals("Quebec", english.getObject("address").getString("province"));
//		assertEquals("Canada", english.getObject("address").getString("country"));
//		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
//
//		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
//		assertEquals("chris@bacon.com", english.getObject("communication").getString("email"));
//		assertEquals("Tester", english.getObject("communication").getString("jobTitle"));
//		assertEquals("English", english.getObject("communication").getString("language"));
//		assertEquals("4564564565", english.getObject("communication").getString("faxNumber"));
//		
//		assertEquals(List.of("number", "extension"), english.getObject("communication").getObject("homePhone").keys());
//		assertEquals("2342342345", english.getObject("communication").getObject("homePhone").getString("number"));
//		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
//
//		assertEquals(List.of("sector", "firstName", "middleName", "lastName"), english.getObject("position").keys());
//		assertEquals("External", english.getObject("position").getString("sector"));
//
//		PersonDetails reloaded = new JsonTransformer(crm, Lang.ENGLISH, false)
//				.parsePersonDetails(new JsonObject(JsonFormatter.formatted(english)));
//		
//		assertEquals(person.getPersonId(), reloaded.getPersonId());
//		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
//		assertEquals(person.getStatus(), reloaded.getStatus());
//		assertEquals(person.getAddress(), reloaded.getAddress());
//		assertEquals(person.getCommunication(), reloaded.getCommunication());
//		assertEquals(person.getPosition(), reloaded.getPosition());
//	}
//	
//	@Test
//	public void testPersonLinkedJson() throws Exception {
//		CrmServices crm = new AmnesiaServices();
//		Locale locale = Lang.ENGLISH;
//		Identifier personId = new Identifier("abc");
//		Identifier organizationId = new Identifier("xyz");
//		Status status = Status.PENDING;
//		String displayName = "Junit Test";
//		PersonName legalName = new PersonName("Mr.", "Chris", "P", "Bacon");
//		MailingAddress address = new MailingAddress("123 Main St", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K1K1K1");
//		String email = "chris@bacon.com";
//		String jobTitle = "Tester";
//		Telephone homePhone = new Telephone("2342342345", null);
//		String faxNumber = "4564564565";
//		Communication communication = new Communication(jobTitle, ENGLISH.get(locale), email, homePhone, faxNumber);
//		BusinessPosition unit = new BusinessPosition("Information Technology", null, null);
//		
//		PersonDetails person = new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit);
//		JsonTransformer transformer = new JsonTransformer(crm, locale, true);
//		
//		JsonObject obj = transformer.formatPersonDetails(person);
//		String json = JsonFormatter.formatted(obj);
//		
//		assertEquals("{\n" + 
//				"  \"@context\": \"http://magex9.github.io/api/\",\n" + 
//				"  \"@type\": \"PersonDetails\",\n" + 
//				"  \"@id\": \"abc\",\n" + 
//				"  \"organizationId\": {\n" + 
//				"    \"@type\": \"OrganizationSummary\",\n" + 
//				"    \"@id\": \"xyz\"\n" + 
//				"  },\n" + 
//				"  \"status\": {\n" + 
//				"    \"@type\": \"Status\",\n" + 
//				"    \"@value\": \"pending\",\n" + 
//				"    \"@en\": \"Pending\",\n" + 
//				"    \"@fr\": \"En attente\"\n" + 
//				"  },\n" + 
//				"  \"displayName\": \"Junit Test\",\n" + 
//				"  \"legalName\": {\n" + 
//				"    \"@type\": \"PersonName\",\n" + 
//				"    \"salutation\": {\n" + 
//				"      \"@type\": \"Salutation\",\n" + 
//				"      \"@value\": \"3\",\n" + 
//				"      \"@en\": \"Mr.\",\n" + 
//				"      \"@fr\": \"M.\"\n" + 
//				"    },\n" + 
//				"    \"firstName\": \"Chris\",\n" + 
//				"    \"middleName\": \"P\",\n" + 
//				"    \"lastName\": \"Bacon\"\n" + 
//				"  },\n" + 
//				"  \"address\": {\n" + 
//				"    \"@type\": \"MailingAddress\",\n" + 
//				"    \"street\": \"123 Main St\",\n" + 
//				"    \"city\": \"Ottawa\",\n" + 
//				"    \"province\": \"Ontario\",\n" + 
//				"    \"country\": {\n" + 
//				"      \"@type\": \"Country\",\n" + 
//				"      \"@value\": \"CA\",\n" + 
//				"      \"@en\": \"Canada\",\n" + 
//				"      \"@fr\": \"Canada\"\n" + 
//				"    },\n" + 
//				"    \"postalCode\": \"K1K1K1\"\n" + 
//				"  },\n" + 
//				"  \"communication\": {\n" + 
//				"    \"@type\": \"Communication\",\n" + 
//				"    \"email\": \"chris@bacon.com\",\n" + 
//				"    \"jobTitle\": \"Tester\",\n" + 
//				"    \"language\": {\n" + 
//				"      \"@type\": \"Language\",\n" + 
//				"      \"@value\": \"en\",\n" + 
//				"      \"@en\": \"English\",\n" + 
//				"      \"@fr\": \"Anglais\"\n" + 
//				"    },\n" + 
//				"    \"homePhone\": {\n" + 
//				"      \"@type\": \"Telephone\",\n" + 
//				"      \"number\": \"2342342345\"\n" + 
//				"    },\n" + 
//				"    \"faxNumber\": \"4564564565\"\n" + 
//				"  },\n" + 
//				"  \"position\": {\n" + 
//				"    \"@type\": \"BusinessPosition\",\n" + 
//				"    \"sector\": {\n" + 
//				"      \"@type\": \"BusinessSector\",\n" + 
//				"      \"@value\": \"4\",\n" + 
//				"      \"@en\": \"Information Technology\",\n" + 
//				"      \"@fr\": \"Technologie Informatique\"\n" + 
//				"    }\n" + 
//				"  }\n" + 
//				"}", json);
//		
//		PersonDetails reloaded = transformer.parsePersonDetails(obj);
//		
//		assertEquals(person.getPersonId(), reloaded.getPersonId());
//		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
//		assertEquals(person.getStatus(), reloaded.getStatus());
//		assertEquals(person.getAddress(), reloaded.getAddress());
//		assertEquals(person.getCommunication(), reloaded.getCommunication());
//		assertEquals(person.getPosition(), reloaded.getPosition());
//	}
//	
}
