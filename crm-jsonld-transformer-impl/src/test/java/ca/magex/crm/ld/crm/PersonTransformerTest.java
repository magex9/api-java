package ca.magex.crm.ld.crm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.crm.Person;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.ld.LinkedDataFormatter;
import ca.magex.crm.ld.data.DataObject;

@Ignore
public class PersonTransformerTest {

	@Test
	public void testOrganizationLinkedData() throws Exception {
		Identifier personId = new Identifier("abc");
		Identifier organizationId = new Identifier("xyz");
		Status status = Status.PENDING;
		String displayName = "Junit Test";
		PersonName legalName = new PersonName(new Salutation(1, "Mr"), "Chris", "P", "Bacon");
		Country canada = new Country("CA", "Canada");
		MailingAddress address = new MailingAddress("123 Main St", "Ottawa", "Ontario", canada, "K1K1K1");
		String email = "chris@bacon.com";
		String jobTitle = "Tester";
		Language language = new Language("en", "English");
		Telephone homePhone = new Telephone(2342342345L, null);
		Long faxNumber = 4564564565L;
		String userName = "chris";
		List<Role> roles = new ArrayList<Role>();
		roles.add(new Role(1, "A"));
		roles.add(new Role(2, "B"));
		roles.add(new Role(3, "C"));
		
		Person person = new Person(personId, organizationId, status, displayName, legalName, address, email, jobTitle, language, homePhone, faxNumber, userName, roles);
		
		DataObject obj = new PersonTransformer().format(person);
		
		System.out.println(obj.stringify(LinkedDataFormatter.basic()));

		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/schema/crm\",\n" + 
				"  \"@type\": \"Person\",\n" + 
				"  \"@value\": \"abc\",\n" + 
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" + 
				"  \"organization\": \"xyz\",\n" + 
				"  \"status\": \"pending\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"legalName\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"PersonName\",\n" + 
				"    \"salutation\": 1,\n" + 
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
				"    \"country\": \"CA\",\n" + 
				"    \"postalCode\": \"K1K1K1\"\n" + 
				"  },\n" + 
				"  \"email\": \"chris@bacon.com\",\n" + 
				"  \"jobTitle\": \"Tester\",\n" + 
				"  \"language\": \"en\",\n" + 
				"  \"homePhone\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"Telephone\",\n" + 
				"    \"number\": 2342342345,\n" + 
				"    \"extension\": null\n" + 
				"  },\n" + 
				"  \"faxNumber\": 4564564565,\n" + 
				"  \"userName\": \"chris\",\n" + 
				"  \"roles\": [\n" + 
				"    1,\n" + 
				"    2,\n" + 
				"    3\n" + 
				"  ]\n" + 
				"}", obj.stringify(LinkedDataFormatter.basic()));
		
		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/schema/crm\",\n" + 
				"  \"@type\": \"Person\",\n" + 
				"  \"@value\": \"abc\",\n" + 
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" + 
				"  \"organization\": {\n" + 
				"    \"@type\": \"Person\",\n" + 
				"    \"@value\": \"xyz\",\n" + 
				"    \"@id\": \"http://magex9.github.io/data/xyz\"\n" + 
				"  },\n" + 
				"  \"status\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"    \"@type\": \"Status\",\n" + 
				"    \"@value\": \"pending\"\n" + 
				"  },\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"legalName\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"PersonName\",\n" + 
				"    \"salutation\": {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/lookup\",\n" + 
				"      \"@type\": \"Salutation\",\n" + 
				"      \"@value\": 1,\n" + 
				"      \"name\": \"Mr\"\n" + 
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
				"      \"name\": \"Canada\"\n" + 
				"    },\n" + 
				"    \"postalCode\": \"K1K1K1\"\n" + 
				"  },\n" + 
				"  \"email\": \"chris@bacon.com\",\n" + 
				"  \"jobTitle\": \"Tester\",\n" + 
				"  \"language\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/lookup\",\n" + 
				"    \"@type\": \"Language\",\n" + 
				"    \"@value\": \"en\",\n" + 
				"    \"name\": \"English\"\n" + 
				"  },\n" + 
				"  \"homePhone\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/schema/common\",\n" + 
				"    \"@type\": \"Telephone\",\n" + 
				"    \"number\": 2342342345,\n" + 
				"    \"extension\": null\n" + 
				"  },\n" + 
				"  \"faxNumber\": 4564564565,\n" + 
				"  \"userName\": \"chris\",\n" + 
				"  \"roles\": [\n" + 
				"    {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"      \"@type\": \"Role\",\n" + 
				"      \"@value\": 1,\n" + 
				"      \"name\": \"A\"\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"      \"@type\": \"Role\",\n" + 
				"      \"@value\": 2,\n" + 
				"      \"name\": \"B\"\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"@context\": \"http://magex9.github.io/schema/system\",\n" + 
				"      \"@type\": \"Role\",\n" + 
				"      \"@value\": 3,\n" + 
				"      \"name\": \"C\"\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}", obj.formatted());
		
		Person reloaded = new PersonTransformer().parse(obj.formatted());
		
		assertEquals(person.getPersonId(), reloaded.getPersonId());
		assertEquals(person.getOrganizationId(), reloaded.getOrganizationId());
		assertEquals(person.getStatus(), reloaded.getStatus());
		assertEquals(person.getAddress(), reloaded.getAddress());
		assertEquals(person.getLanguage(), reloaded.getLanguage());
		assertEquals(person.getHomePhone(), reloaded.getHomePhone());
		assertEquals(person.getFaxNumber(), reloaded.getFaxNumber());
		assertEquals(person.getRoles(), reloaded.getRoles());
	}
	
}
