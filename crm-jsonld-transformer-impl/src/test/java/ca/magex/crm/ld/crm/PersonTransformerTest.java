package ca.magex.crm.ld.crm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/apis/crm\",\n" + 
				"  \"@type\": \"organization\",\n" + 
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"status\": active,\n" + 
				"  \"mainLocation\": {\n" + 
				"    \"@type\": \"location\",\n" + 
				"    \"@id\": \"http://magex9.github.io/data/xyz\"\n" + 
				"  }\n" + 
				"}", obj.stringify(LinkedDataFormatter.basic()));
		
		assertEquals("{\n" + 
				"  \"@context\": \"http://magex9.github.io/apis/crm\",\n" + 
				"  \"@type\": \"organization\",\n" + 
				"  \"@id\": \"http://magex9.github.io/data/abc\",\n" + 
				"  \"displayName\": \"Junit Test\",\n" + 
				"  \"status\": {\n" + 
				"    \"@context\": \"http://magex9.github.io/apis/system\",\n" + 
				"    \"@type\": \"status\",\n" + 
				"    \"@value\": \"active\"\n" + 
				"  },\n" + 
				"  \"mainLocation\": {\n" + 
				"    \"@type\": \"location\",\n" + 
				"    \"@id\": \"http://magex9.github.io/data/xyz\"\n" + 
				"  }\n" + 
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
