package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.PERSON_DISPLAY_NAME;
import static ca.magex.crm.test.CrmAsserts.PERSON_LEGAL_NAME;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.WORK_COMMUNICATIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.BusinessRoleIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<PersonDetails, JsonElement> transformer;
	
	private PersonIdentifier personId;
	
	private OrganizationIdentifier organizationId;
	
	private PersonDetails person;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new PersonDetailsJsonTransformer(crm);
		personId = new PersonIdentifier("bN2ifcbtzA");
		organizationId = new OrganizationIdentifier("Q2Tf7v7tzJ");
		List<BusinessRoleIdentifier> roleIds = List.of(
			crm.findOptionByCode(Type.BUSINESS_ROLE, "IMIT/DEV/APPS/DEV").getOptionId()
		);
		person = new PersonDetails(personId, organizationId, Status.ACTIVE, 
				PERSON_DISPLAY_NAME, PERSON_LEGAL_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, roleIds);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(PersonDetails.class, transformer.getSourceType());
	}

	@Test
	public void testFormatNull() throws Exception {
		assertNull(transformer.format(null, null));
		assertNull(transformer.format(null, Lang.ROOT));
		assertNull(transformer.format(null, Lang.ENGLISH));
		assertNull(transformer.format(null, Lang.FRENCH));
	}
	
	@Test
	public void testLinkedJson() throws Exception {
		JsonObject linked = (JsonObject)transformer.format(person, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/organization/PersonDetails", linked.getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/persons/" + personId.getCode(), linked.getString("personId"));
		assertEquals("http://api.magex.ca/crm/rest/organizations/" + organizationId.getCode(), linked.getString("organizationId"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Statuses", linked.getObject("status").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/statuses/active", linked.getObject("status").getString("@id"));
		assertEquals("ACTIVE", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(List.of("@context", "salutation", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/PersonName", linked.getObject("legalName").getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("legalName").getObject("salutation").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Salutations", linked.getObject("legalName").getObject("salutation").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/salutations/mr", linked.getObject("legalName").getObject("salutation").getString("@id"));
		assertEquals("MR", linked.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Chris", linked.getObject("legalName").getString("firstName"));
		assertEquals("P", linked.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getObject("address").getString("@context"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("address").getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/qc", linked.getObject("address").getObject("province").getString("@id"));
		assertEquals("CA/QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("address").getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", linked.getObject("address").getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(List.of("@context", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.getObject("communication").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Communication", linked.getObject("communication").getString("@context"));
		assertEquals("Developer", linked.getObject("communication").getString("jobTitle"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("communication").getObject("language").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Languages", linked.getObject("communication").getObject("language").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/languages/en", linked.getObject("communication").getObject("language").getString("@id"));
		assertEquals("EN", linked.getObject("communication").getObject("language").getString("@value"));
		assertEquals("English", linked.getObject("communication").getObject("language").getString("@en"));
		assertEquals("Anglais", linked.getObject("communication").getObject("language").getString("@fr"));
		assertEquals("user@work.ca", linked.getObject("communication").getString("email"));
		assertEquals(List.of("@context", "number", "extension"), linked.getObject("communication").getObject("homePhone").keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/Telephone", linked.getObject("communication").getObject("homePhone").getString("@context"));
		assertEquals("5551234567", linked.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", linked.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", linked.getObject("communication").getString("faxNumber"));
		assertEquals(1, linked.getArray("businessRoleIds").size());
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getArray("businessRoleIds").getObject(0).keys());
		assertEquals("http://api.magex.ca/crm/schema/options/BusinessRoles", linked.getArray("businessRoleIds").getObject(0).getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/business-roles/imit/dev/apps/dev", linked.getArray("businessRoleIds").getObject(0).getString("@id"));
		assertEquals("IMIT/DEV/APPS/DEV", linked.getArray("businessRoleIds").getObject(0).getString("@value"));
		assertEquals("Developer", linked.getArray("businessRoleIds").getObject(0).getString("@en"));
		assertEquals("Développeur", linked.getArray("businessRoleIds").getObject(0).getString("@fr"));
		assertEquals(person, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(person, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), root.keys());
		assertEquals(personId.getCode(), root.getString("personId"));
		assertEquals(organizationId.getCode(), root.getString("organizationId"));
		assertEquals("ACTIVE", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("MR", root.getObject("legalName").getString("salutation"));
		assertEquals("Chris", root.getObject("legalName").getString("firstName"));
		assertEquals("P", root.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", root.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("123 Main St", root.getObject("address").getString("street"));
		assertEquals("Ottawa", root.getObject("address").getString("city"));
		assertEquals("CA/QC", root.getObject("address").getString("province"));
		assertEquals("CA", root.getObject("address").getString("country"));
		assertEquals("K1K1K1", root.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), root.getObject("communication").keys());
		assertEquals("Developer", root.getObject("communication").getString("jobTitle"));
		assertEquals("EN", root.getObject("communication").getString("language"));
		assertEquals("user@work.ca", root.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), root.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", root.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getObject("communication").getString("faxNumber"));
		assertEquals(1, root.getArray("businessRoleIds").size());
		assertEquals("IMIT/DEV/APPS/DEV", root.getArray("businessRoleIds").getString(0));
		assertEquals(person, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(person, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), english.keys());
		assertEquals(personId.getCode(), english.getString("personId"));
		assertEquals(organizationId.getCode(), english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Chris", english.getObject("legalName").getString("firstName"));
		assertEquals("P", english.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(1, english.getArray("businessRoleIds").size());
		assertEquals("Developer", english.getArray("businessRoleIds").getString(0));
		assertEquals(person, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(person, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "businessRoleIds"), french.keys());
		assertEquals(personId.getCode(), french.getString("personId"));
		assertEquals(organizationId.getCode(), french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
		assertEquals(List.of("salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Chris", french.getObject("legalName").getString("firstName"));
		assertEquals("P", french.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(1, french.getArray("businessRoleIds").size());
		assertEquals("Développeur", french.getArray("businessRoleIds").getString(0));
		assertEquals(person, transformer.parse(french, Lang.FRENCH));
	}
	
}
