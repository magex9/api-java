package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
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
import ca.magex.json.model.JsonAsserts;
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
			PERSON_NAME.getDisplayName(), PERSON_NAME, MAILING_ADDRESS, WORK_COMMUNICATIONS, roleIds);
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
		JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), linked.keys());
		assertEquals("PersonDetails", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("personId").keys());
		assertEquals("Identifier", linked.getObject("personId").getString("@type"));
		assertEquals("prsn", linked.getObject("personId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("PersonName", linked.getObject("legalName").getString("@type"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("legalName").getObject("salutation").keys());
		assertEquals("SALUTATION", linked.getObject("legalName").getObject("salutation").getString("@type"));
		assertEquals("MR", linked.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Chris", linked.getObject("legalName").getString("firstName"));
		assertEquals("P", linked.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("MailingAddress", linked.getObject("address").getString("@type"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("PROVINCE", linked.getObject("address").getObject("province").getString("@type"));
		assertEquals("QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("COUNTRY", linked.getObject("address").getObject("country").getString("@type"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.getObject("communication").keys());
		assertEquals("Communication", linked.getObject("communication").getString("@type"));
		assertEquals("Developer", linked.getObject("communication").getString("jobTitle"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("communication").getObject("language").keys());
		assertEquals("LANGUAGE", linked.getObject("communication").getObject("language").getString("@type"));
		assertEquals("EN", linked.getObject("communication").getObject("language").getString("@value"));
		assertEquals("English", linked.getObject("communication").getObject("language").getString("@en"));
		assertEquals("Anglais", linked.getObject("communication").getObject("language").getString("@fr"));
		assertEquals("user@work.ca", linked.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), linked.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", linked.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", linked.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", linked.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", linked.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), linked.getObject("position").keys());
		assertEquals("BusinessPosition", linked.getObject("position").getString("@type"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("position").getObject("sector").keys());
		assertEquals("BUSINESS_SECTOR", linked.getObject("position").getObject("sector").getString("@type"));
		assertEquals("IMIT", linked.getObject("position").getObject("sector").getString("@value"));
		assertEquals("IM/IT", linked.getObject("position").getObject("sector").getString("@en"));
		assertEquals("GI / TI", linked.getObject("position").getObject("sector").getString("@fr"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("position").getObject("unit").keys());
		assertEquals("BUSINESS_UNIT", linked.getObject("position").getObject("unit").getString("@type"));
		assertEquals("OPS", linked.getObject("position").getObject("unit").getString("@value"));
		assertEquals("Operations", linked.getObject("position").getObject("unit").getString("@en"));
		assertEquals("Operations", linked.getObject("position").getObject("unit").getString("@fr"));
		assertEquals(List.of("@type", "@lookup", "@value", "@en", "@fr"), linked.getObject("position").getObject("classification").keys());
		assertEquals("BUSINESS_CLASSIFICATION", linked.getObject("position").getObject("classification").getString("@type"));
		assertEquals("SYS_ADMIN", linked.getObject("position").getObject("classification").getString("@value"));
		assertEquals("System Administrator", linked.getObject("position").getObject("classification").getString("@en"));
		assertEquals("Administrateur du système", linked.getObject("position").getObject("classification").getString("@fr"));
		assertEquals(person, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(person, Lang.ROOT);
		System.out.println(root);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), root.keys());
		assertEquals("PersonDetails", root.getString("@type"));
		assertEquals("prsn", root.getString("personId"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("PersonName", root.getObject("legalName").getString("@type"));
		assertEquals("MR", root.getObject("legalName").getString("salutation"));
		assertEquals("Chris", root.getObject("legalName").getString("firstName"));
		assertEquals("P", root.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", root.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), root.getObject("address").keys());
		assertEquals("MailingAddress", root.getObject("address").getString("@type"));
		assertEquals("123 Main St", root.getObject("address").getString("street"));
		assertEquals("Ottawa", root.getObject("address").getString("city"));
		assertEquals("QC", root.getObject("address").getString("province"));
		assertEquals("CA", root.getObject("address").getString("country"));
		assertEquals("K1K1K1", root.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), root.getObject("communication").keys());
		assertEquals("Communication", root.getObject("communication").getString("@type"));
		assertEquals("Developer", root.getObject("communication").getString("jobTitle"));
		assertEquals("EN", root.getObject("communication").getString("language"));
		assertEquals("user@work.ca", root.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), root.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", root.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", root.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", root.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", root.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), root.getObject("position").keys());
		assertEquals("BusinessPosition", root.getObject("position").getString("@type"));
		assertEquals("IMIT", root.getObject("position").getString("sector"));
		assertEquals("OPS", root.getObject("position").getString("unit"));
		assertEquals("SYS_ADMIN", root.getObject("position").getString("classification"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(person, Lang.ENGLISH);
		System.out.println(english);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), english.keys());
		assertEquals("PersonDetails", english.getString("@type"));
		assertEquals("prsn", english.getString("personId"));
		assertEquals("org", english.getString("organizationId"));
		assertEquals("Active", english.getString("status"));
		assertEquals("Bacon, Chris P", english.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), english.getObject("legalName").keys());
		assertEquals("PersonName", english.getObject("legalName").getString("@type"));
		assertEquals("Mr.", english.getObject("legalName").getString("salutation"));
		assertEquals("Chris", english.getObject("legalName").getString("firstName"));
		assertEquals("P", english.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", english.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.getObject("address").keys());
		assertEquals("MailingAddress", english.getObject("address").getString("@type"));
		assertEquals("123 Main St", english.getObject("address").getString("street"));
		assertEquals("Ottawa", english.getObject("address").getString("city"));
		assertEquals("Quebec", english.getObject("address").getString("province"));
		assertEquals("Canada", english.getObject("address").getString("country"));
		assertEquals("K1K1K1", english.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), english.getObject("communication").keys());
		assertEquals("Communication", english.getObject("communication").getString("@type"));
		assertEquals("Developer", english.getObject("communication").getString("jobTitle"));
		assertEquals("English", english.getObject("communication").getString("language"));
		assertEquals("user@work.ca", english.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), english.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", english.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", english.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", english.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", english.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), english.getObject("position").keys());
		assertEquals("BusinessPosition", english.getObject("position").getString("@type"));
		assertEquals("IM/IT", english.getObject("position").getString("sector"));
		assertEquals("Operations", english.getObject("position").getString("unit"));
		assertEquals("System Administrator", english.getObject("position").getString("classification"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(person, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), french.keys());
		assertEquals("PersonDetails", french.getString("@type"));
		assertEquals("prsn", french.getString("personId"));
		assertEquals("org", french.getString("organizationId"));
		assertEquals("Actif", french.getString("status"));
		assertEquals("Bacon, Chris P", french.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), french.getObject("legalName").keys());
		assertEquals("PersonName", french.getObject("legalName").getString("@type"));
		assertEquals("M.", french.getObject("legalName").getString("salutation"));
		assertEquals("Chris", french.getObject("legalName").getString("firstName"));
		assertEquals("P", french.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", french.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.getObject("address").keys());
		assertEquals("MailingAddress", french.getObject("address").getString("@type"));
		assertEquals("123 Main St", french.getObject("address").getString("street"));
		assertEquals("Ottawa", french.getObject("address").getString("city"));
		assertEquals("Québec", french.getObject("address").getString("province"));
		assertEquals("Canada", french.getObject("address").getString("country"));
		assertEquals("K1K1K1", french.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), french.getObject("communication").keys());
		assertEquals("Communication", french.getObject("communication").getString("@type"));
		assertEquals("Developer", french.getObject("communication").getString("jobTitle"));
		assertEquals("Anglais", french.getObject("communication").getString("language"));
		assertEquals("user@work.ca", french.getObject("communication").getString("email"));
		assertEquals(List.of("@type", "number", "extension"), french.getObject("communication").getObject("homePhone").keys());
		assertEquals("Telephone", french.getObject("communication").getObject("homePhone").getString("@type"));
		assertEquals("5551234567", french.getObject("communication").getObject("homePhone").getString("number"));
		assertEquals("42", french.getObject("communication").getObject("homePhone").getString("extension"));
		assertEquals("8881234567", french.getObject("communication").getString("faxNumber"));
		assertEquals(List.of("@type", "sector", "unit", "classification"), french.getObject("position").keys());
		assertEquals("BusinessPosition", french.getObject("position").getString("@type"));
		assertEquals("GI / TI", french.getObject("position").getString("sector"));
		assertEquals("Operations", french.getObject("position").getString("unit"));
		assertEquals("Administrateur du système", french.getObject("position").getString("classification"));
	}
	
}
