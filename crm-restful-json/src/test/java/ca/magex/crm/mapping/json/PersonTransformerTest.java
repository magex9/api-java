package ca.magex.crm.mapping.json;

import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.ENGLISH;
import static ca.magex.crm.test.CrmAsserts.ONTARIO;
import static ca.magex.crm.test.CrmAsserts.ORG_ADMIN;
import static ca.magex.crm.test.CrmAsserts.SYS;
import static ca.magex.crm.test.CrmAsserts.SYS_ADMIN;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.secured.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.rest.transformers.JsonTransformer;
import ca.magex.crm.test.TestConfig;
import ca.magex.json.model.JsonFormatter;
import ca.magex.json.model.JsonObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles(value = {
		MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED,
		MagexCrmProfiles.CRM_NO_AUTH
})
public class PersonTransformerTest {

	@Autowired private SecuredCrmServices crm;

	@Test
	public void testPersonJson() throws Exception {
		Locale locale = Lang.ENGLISH;
		Identifier personId = new Identifier("abc");
		Identifier organizationId = new Identifier("xyz");
		Status status = Status.PENDING;
		String displayName = "Junit Test";
		PersonName legalName = new PersonName(crm.findSalutationByCode("3").getName(locale), "Chris", "P", "Bacon");
		MailingAddress address = new MailingAddress("123 Main St", "Ottawa", ONTARIO.get(locale), CANADA.get(locale), "K1K1K1");
		String email = "chris@bacon.com";
		String jobTitle = "Tester";
		Telephone homePhone = new Telephone("2342342345", null);
		String faxNumber = "4564564565";
		Communication communication = new Communication(jobTitle, ENGLISH.get(locale), email, homePhone, faxNumber);
		BusinessPosition unit = new BusinessPosition(crm.findBusinessSectors().get(0).getName(locale), null, null);
		List<String> roles = new ArrayList<String>();
		Group group = crm.createGroup(SYS);
		roles.add(crm.createRole(group.getGroupId(), SYS_ADMIN).getRoleId().toString());
		roles.add(crm.createRole(group.getGroupId(), ORG_ADMIN).getRoleId().toString());
		
		PersonDetails person = new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit);
		JsonTransformer transformer = new JsonTransformer(crm, Lang.ENGLISH, false);
		
		JsonObject obj = transformer.formatPersonDetails(person);
		String json = JsonFormatter.formatted(obj);
		
		assertEquals("{\n" + 
				"  \"personId\": \"abc\",\n" + 
				"  \"organizationId\": \"xyz\",\n" + 
				"  \"status\": \"Pending\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"legalName\": {\n" + 
				"    \"salutation\": \"Mr.\",\n" + 
				"    \"firstName\": \"Chris\",\n" + 
				"    \"middleName\": \"P\",\n" + 
				"    \"lastName\": \"Bacon\"\n" + 
				"  },\n" + 
				"  \"address\": {\n" + 
				"    \"street\": \"123 Main St\",\n" + 
				"    \"city\": \"Ottawa\",\n" + 
				"    \"province\": \"Ontario\",\n" + 
				"    \"country\": \"Canada\",\n" + 
				"    \"postalCode\": \"K1K1K1\"\n" + 
				"  },\n" + 
				"  \"communication\": {\n" + 
				"    \"email\": \"chris@bacon.com\",\n" + 
				"    \"jobTitle\": \"Tester\",\n" + 
				"    \"language\": \"English\",\n" + 
				"    \"homePhone\": {\"number\": \"2342342345\"},\n" + 
				"    \"faxNumber\": \"4564564565\"\n" + 
				"  },\n" + 
				"  \"position\": {\"sector\": \"External\"}\n" + 
				"}", json);
		
		PersonDetails reloaded = transformer.parsePersonDetails(obj);
		
		assertEquals(person.getPersonId(), reloaded.getPersonId());
		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(person.getStatus(), reloaded.getStatus());
		assertEquals(person.getAddress(), reloaded.getAddress());
		assertEquals(person.getCommunication(), reloaded.getCommunication());
		assertEquals(person.getPosition(), reloaded.getPosition());
	}
	
	@Test
	public void testPersonLinkedJson() throws Exception {
		Locale locale = Lang.ENGLISH;
		Identifier personId = new Identifier("abc");
		Identifier organizationId = new Identifier("xyz");
		Status status = Status.PENDING;
		String displayName = "Junit Test";
		PersonName legalName = new PersonName("Mr.", "Chris", "P", "Bacon");
		MailingAddress address = new MailingAddress("123 Main St", "Ottawa", ONTARIO.get(locale), CANADA.get(locale), "K1K1K1");
		String email = "chris@bacon.com";
		String jobTitle = "Tester";
		Telephone homePhone = new Telephone("2342342345", null);
		String faxNumber = "4564564565";
		Communication communication = new Communication(jobTitle, ENGLISH.get(locale), email, homePhone, faxNumber);
		BusinessPosition unit = new BusinessPosition("Information Technology", null, null);
		
		PersonDetails person = new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit);
		JsonTransformer transformer = new JsonTransformer(crm, locale, true);
		
		JsonObject obj = transformer.formatPersonDetails(person);
		String json = JsonFormatter.formatted(obj);
		
		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/api/\",\n" + 
				"  \"@type\": \"PersonDetails\",\n" + 
				"  \"@id\": \"abc\",\n" + 
				"  \"organizationId\": {\n" + 
				"    \"@type\": \"OrganizationSummary\",\n" + 
				"    \"@id\": \"xyz\"\n" + 
				"  },\n" + 
				"  \"status\": {\n" + 
				"    \"@type\": \"Status\",\n" + 
				"    \"@value\": \"pending\",\n" + 
				"    \"@en\": \"Pending\",\n" + 
				"    \"@fr\": \"En attente\"\n" + 
				"  },\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"legalName\": {\n" + 
				"    \"@type\": \"PersonName\",\n" + 
				"    \"salutation\": {\n" + 
				"      \"@type\": \"Salutation\",\n" + 
				"      \"@value\": \"3\",\n" + 
				"      \"@en\": \"Mr.\",\n" + 
				"      \"@fr\": \"M.\"\n" + 
				"    },\n" + 
				"    \"firstName\": \"Chris\",\n" + 
				"    \"middleName\": \"P\",\n" + 
				"    \"lastName\": \"Bacon\"\n" + 
				"  },\n" + 
				"  \"address\": {\n" + 
				"    \"@type\": \"MailingAddress\",\n" + 
				"    \"street\": \"123 Main St\",\n" + 
				"    \"city\": \"Ottawa\",\n" + 
				"    \"province\": \"Ontario\",\n" + 
				"    \"country\": {\n" + 
				"      \"@type\": \"Country\",\n" + 
				"      \"@value\": \"CA\",\n" + 
				"      \"@en\": \"Canada\",\n" + 
				"      \"@fr\": \"Canada\"\n" + 
				"    },\n" + 
				"    \"postalCode\": \"K1K1K1\"\n" + 
				"  },\n" + 
				"  \"communication\": {\n" + 
				"    \"@type\": \"Communication\",\n" + 
				"    \"email\": \"chris@bacon.com\",\n" + 
				"    \"jobTitle\": \"Tester\",\n" + 
				"    \"language\": {\n" + 
				"      \"@type\": \"Language\",\n" + 
				"      \"@value\": \"en\",\n" + 
				"      \"@en\": \"English\",\n" + 
				"      \"@fr\": \"Anglais\"\n" + 
				"    },\n" + 
				"    \"homePhone\": {\n" + 
				"      \"@type\": \"Telephone\",\n" + 
				"      \"number\": \"2342342345\"\n" + 
				"    },\n" + 
				"    \"faxNumber\": \"4564564565\"\n" + 
				"  },\n" + 
				"  \"position\": {\n" + 
				"    \"@type\": \"BusinessPosition\",\n" + 
				"    \"sector\": {\n" + 
				"      \"@type\": \"BusinessSector\",\n" + 
				"      \"@value\": \"4\",\n" + 
				"      \"@en\": \"Information Technology\",\n" + 
				"      \"@fr\": \"Technologie Informatique\"\n" + 
				"    }\n" + 
				"  }\n" + 
				"}", json);
		
		PersonDetails reloaded = transformer.parsePersonDetails(obj);
		
		assertEquals(person.getPersonId(), reloaded.getPersonId());
		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(person.getStatus(), reloaded.getStatus());
		assertEquals(person.getAddress(), reloaded.getAddress());
		assertEquals(person.getCommunication(), reloaded.getCommunication());
		assertEquals(person.getPosition(), reloaded.getPosition());
	}
	
}
