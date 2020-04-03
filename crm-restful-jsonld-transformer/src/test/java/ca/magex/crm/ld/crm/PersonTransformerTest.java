package ca.magex.crm.ld.crm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaFactory;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;

public class PersonTransformerTest {

	@Test
	public void testOrganizationLinkedData() throws Exception {
		SecuredCrmServices service = AmnesiaFactory.getAnonymousService();
		Identifier personId = new Identifier("abc");
		Identifier organizationId = new Identifier("xyz");
		Status status = Status.PENDING;
		String displayName = "Junit Test";
		PersonName legalName = new PersonName(service.findSalutationByCode(1), "Chris", "P", "Bacon");
		Country canada = service.findCountryByCode("CA");
		MailingAddress address = new MailingAddress("123 Main St", "Ottawa", "Ontario", canada, "K1K1K1");
		String email = "chris@bacon.com";
		String jobTitle = "Tester";
		Language language = new Language("en", "English");
		Telephone homePhone = new Telephone("2342342345", null);
		String faxNumber = "4564564565";
		Communication communication = new Communication(jobTitle, language, email, homePhone, faxNumber);
		BusinessPosition unit = new BusinessPosition(null, null, null);
		String userName = "chris";
		List<Role> roles = new ArrayList<Role>();
		User user = new User(userName, roles);
		roles.add(service.findRoleByCode("SYS_AMDIN"));
		roles.add(service.findRoleByCode("RE_ADMIN"));
		
		PersonDetails person = new PersonDetails(personId, organizationId, status, displayName, legalName, address, communication, unit, user);
		
		DataObject obj = new PersonDetailsTransformer(service).format(person);
		
		assertEquals("{\r\n" + 
				"  \"organization\": \"xyz\",\r\n" + 
				"  \"status\": \"pending\",\r\n" + 
				"  \"displayName\": \"Junit Test\",\r\n" + 
				"  \"legalName\": {\r\n" + 
				"    \"salutation\": 1,\r\n" + 
				"    \"firstName\": \"Chris\",\r\n" + 
				"    \"middleName\": \"P\",\r\n" + 
				"    \"lastName\": \"Bacon\"\r\n" + 
				"  },\r\n" + 
				"  \"address\": {\r\n" + 
				"    \"street\": \"123 Main St\",\r\n" + 
				"    \"city\": \"Ottawa\",\r\n" + 
				"    \"province\": \"Ontario\",\r\n" + 
				"    \"country\": \"CA\",\r\n" + 
				"    \"postalCode\": \"K1K1K1\"\r\n" + 
				"  },\r\n" + 
				"  \"communication\": {\r\n" + 
				"    \"email\": \"chris@bacon.com\",\r\n" + 
				"    \"jobTitle\": \"Tester\",\r\n" + 
				"    \"language\": \"en\",\r\n" + 
				"    \"homePhone\": {\r\n" + 
				"      \"number\": \"2342342345\",\r\n" + 
				"      \"extension\": null\r\n" + 
				"    },\r\n" + 
				"    \"faxNumber\": \"4564564565\"\r\n" + 
				"  },\r\n" + 
				"  \"unit\": {\r\n" + 
				"    \"sector\": null,\r\n" + 
				"    \"unit\": null,\r\n" + 
				"    \"classification\": null\r\n" + 
				"  },\r\n" + 
				"  \"user\": {\r\n" + 
				"    \"userName\": \"chris\",\r\n" + 
				"    \"roles\": [\r\n" + 
				"      \"SYS_AMDIN\",\r\n" + 
				"      \"RE_ADMIN\"\r\n" + 
				"    ]\r\n" + 
				"  }\r\n" + 
				"}".replaceAll("\r", ""), obj.stringify(LinkedDataFormatter.json()));
		
		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/schema/crm\",\n" + 
				"  \"@type\": \"PersonDetails\",\n" + 
				"  \"@value\": \"abc\",\n" + 
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" + 
				"  \"organization\": {\n" + 
				"    \"@type\": \"PersonDetails\",\n" + 
				"    \"@value\": \"xyz\",\n" + 
				"    \"@id\": \"http://magex9.github.io/data/xyz\"\n" + 
				"  },\n" + 
				"  \"status\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"    \"@type\": \"Status\",\n" + 
				"    \"@value\": \"pending\",\n" + 
				"    \"@en\": \"Pending\",\n" + 
				"    \"@fr\": \"En attente\"\n" + 
				"  },\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"legalName\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"PersonName\",\n" + 
				"    \"salutation\": {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/lookup\",\n" + 
				"      \"@type\": \"Salutation\",\n" + 
				"      \"@value\": 1,\n" + 
				"      \"@en\": \"Miss\",\n" + 
				"      \"@fr\": \"Mlle.\"\n" + 
				"    },\n" + 
				"    \"firstName\": \"Chris\",\n" + 
				"    \"middleName\": \"P\",\n" + 
				"    \"lastName\": \"Bacon\"\n" + 
				"  },\n" + 
				"  \"address\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"MailingAddress\",\n" + 
				"    \"street\": \"123 Main St\",\n" + 
				"    \"city\": \"Ottawa\",\n" + 
				"    \"province\": \"Ontario\",\n" + 
				"    \"country\": {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/lookup\",\n" + 
				"      \"@type\": \"Country\",\n" + 
				"      \"@value\": \"CA\",\n" + 
				"      \"@en\": \"Canada\",\n" + 
				"      \"@fr\": \"Canada\"\n" + 
				"    },\n" + 
				"    \"postalCode\": \"K1K1K1\"\n" + 
				"  },\n" + 
				"  \"communication\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"Communication\",\n" + 
				"    \"email\": \"chris@bacon.com\",\n" + 
				"    \"jobTitle\": \"Tester\",\n" + 
				"    \"language\": {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/lookup\",\n" + 
				"      \"@type\": \"Language\",\n" + 
				"      \"@value\": \"en\",\n" + 
				"      \"name\": \"English\"\n" + 
				"    },\n" + 
				"    \"homePhone\": {\n" + 
				"      \"@type\": \"Telephone\",\n" + 
				"      \"number\": 2342342345,\n" + 
				"      \"extension\": null\n" + 
				"    },\n" + 
				"    \"faxNumber\": 4564564565\n" + 
				"  },\n" + 
				"  \"unit\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"BusinessPosition\",\n" + 
				"    \"sector\": null,\n" + 
				"    \"unit\": null,\n" + 
				"    \"classification\": null\n" + 
				"  },\n" + 
				"  \"user\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"User\",\n" + 
				"    \"userName\": \"chris\",\n" + 
				"    \"roles\": [\n" + 
				"      {\n" + 
				"        \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"        \"@type\": \"Role\",\n" + 
				"        \"@value\": \"SYS_AMDIN\",\n" + 
				"        \"@en\": \"System Admin\",\n" + 
				"        \"@fr\": \"Administrateur du systeme\"\n" + 
				"      },\n" + 
				"      {\n" + 
				"        \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"        \"@type\": \"Role\",\n" + 
				"        \"@value\": \"RE_ADMIN\",\n" + 
				"        \"@en\": \"Reporting Admin\",\n" + 
				"        \"@fr\": \"Administrateur de rapports\"\n" + 
				"      }\n" + 
				"    ]\n" + 
				"  }\n" + 
				"}", obj.formatted());
		
		PersonDetails reloaded = new PersonDetailsTransformer(service).parse(obj.formatted());
		
		assertEquals(person.getPersonId(), reloaded.getPersonId());
		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(person.getStatus(), reloaded.getStatus());
		assertEquals(person.getAddress(), reloaded.getAddress());
		assertEquals(person.getCommunication(), reloaded.getCommunication());
		assertEquals(person.getPosition(), reloaded.getPosition());
		assertEquals(person.getUser(), reloaded.getUser());
	}
	
}
