package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.COMMUNICATIONS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.PERSON_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class PersonDetailsJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<PersonDetails, JsonElement> transformer;
	
	private PersonDetails person;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new PersonDetailsJsonTransformer(crm);
		BusinessPosition position = new BusinessPosition(
			crm.findBusinessSectorByLocalizedName(Lang.ENGLISH, "Information Technology").getCode(),
			crm.findBusinessUnitByLocalizedName(Lang.ENGLISH, "Solutions").getCode(),
			crm.findBusinessClassificationByLocalizedName(Lang.ENGLISH, "Developer").getCode()
		);
		person = new PersonDetails(new Identifier("prsn"), new Identifier("org"), Status.ACTIVE, 
				PERSON_NAME.getDisplayName(), PERSON_NAME, MAILING_ADDRESS, COMMUNICATIONS, position);
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
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), linked.keys());
		assertEquals("PersonDetails", linked.getString("@type"));
		assertEquals(List.of("@type", "@id"), linked.getObject("personId").keys());
		assertEquals("Identifier", linked.getObject("personId").getString("@type"));
		assertEquals("prsn", linked.getObject("personId").getString("@id"));
		assertEquals(List.of("@type", "@id"), linked.getObject("organizationId").keys());
		assertEquals("Identifier", linked.getObject("organizationId").getString("@type"));
		assertEquals("org", linked.getObject("organizationId").getString("@id"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("status").keys());
		assertEquals("Status", linked.getObject("status").getString("@type"));
		assertEquals("active", linked.getObject("status").getString("@value"));
		assertEquals("Active", linked.getObject("status").getString("@en"));
		assertEquals("Actif", linked.getObject("status").getString("@fr"));
		assertEquals("Bacon, Chris P", linked.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), linked.getObject("legalName").keys());
		assertEquals("PersonName", linked.getObject("legalName").getString("@type"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("legalName").getObject("salutation").keys());
		assertEquals("Salutation", linked.getObject("legalName").getObject("salutation").getString("@type"));
		assertEquals("3", linked.getObject("legalName").getObject("salutation").getString("@value"));
		assertEquals("Mr.", linked.getObject("legalName").getObject("salutation").getString("@en"));
		assertEquals("M.", linked.getObject("legalName").getObject("salutation").getString("@fr"));
		assertEquals("Chris", linked.getObject("legalName").getString("firstName"));
		assertEquals("P", linked.getObject("legalName").getString("middleName"));
		assertEquals("Bacon", linked.getObject("legalName").getString("lastName"));
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), linked.getObject("address").keys());
		assertEquals("MailingAddress", linked.getObject("address").getString("@type"));
		assertEquals("123 Main St", linked.getObject("address").getString("street"));
		assertEquals("Ottawa", linked.getObject("address").getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("address").getObject("province").keys());
		assertEquals("Province", linked.getObject("address").getObject("province").getString("@type"));
		assertEquals("QC", linked.getObject("address").getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("address").getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("address").getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("address").getObject("country").keys());
		assertEquals("Country", linked.getObject("address").getObject("country").getString("@type"));
		assertEquals("CA", linked.getObject("address").getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("address").getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getObject("address").getString("postalCode"));
		assertEquals(List.of("@type", "jobTitle", "language", "email", "homePhone", "faxNumber"), linked.getObject("communication").keys());
		assertEquals("Communication", linked.getObject("communication").getString("@type"));
		assertEquals("Developer", linked.getObject("communication").getString("jobTitle"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("communication").getObject("language").keys());
		assertEquals("Language", linked.getObject("communication").getObject("language").getString("@type"));
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
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("position").getObject("sector").keys());
		assertEquals("BusinessSector", linked.getObject("position").getObject("sector").getString("@type"));
		assertEquals("4", linked.getObject("position").getObject("sector").getString("@value"));
		assertEquals("Information Technology", linked.getObject("position").getObject("sector").getString("@en"));
		assertEquals("Technologie Informatique", linked.getObject("position").getObject("sector").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("position").getObject("unit").keys());
		assertEquals("BusinessUnit", linked.getObject("position").getObject("unit").getString("@type"));
		assertEquals("1", linked.getObject("position").getObject("unit").getString("@value"));
		assertEquals("Solutions", linked.getObject("position").getObject("unit").getString("@en"));
		assertEquals("Solutions", linked.getObject("position").getObject("unit").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("position").getObject("classification").keys());
		assertEquals("BusinessClassification", linked.getObject("position").getObject("classification").getString("@type"));
		assertEquals("1", linked.getObject("position").getObject("classification").getString("@value"));
		assertEquals("Developer", linked.getObject("position").getObject("classification").getString("@en"));
		assertEquals("Développeur", linked.getObject("position").getObject("classification").getString("@fr"));
		assertEquals(person, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(person, Lang.ROOT);
		assertEquals(List.of("@type", "personId", "organizationId", "status", "displayName", "legalName", "address", "communication", "position"), root.keys());
		assertEquals("PersonDetails", root.getString("@type"));
		assertEquals("prsn", root.getString("personId"));
		assertEquals("org", root.getString("organizationId"));
		assertEquals("active", root.getString("status"));
		assertEquals("Bacon, Chris P", root.getString("displayName"));
		assertEquals(List.of("@type", "salutation", "firstName", "middleName", "lastName"), root.getObject("legalName").keys());
		assertEquals("PersonName", root.getObject("legalName").getString("@type"));
		assertEquals("3", root.getObject("legalName").getString("salutation"));
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
		assertEquals("4", root.getObject("position").getString("sector"));
		assertEquals("1", root.getObject("position").getString("unit"));
		assertEquals("1", root.getObject("position").getString("classification"));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(person, Lang.ENGLISH);
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
		assertEquals("Information Technology", english.getObject("position").getString("sector"));
		assertEquals("Solutions", english.getObject("position").getString("unit"));
		assertEquals("Developer", english.getObject("position").getString("classification"));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(person, Lang.FRENCH);
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
		assertEquals("Technologie Informatique", french.getObject("position").getString("sector"));
		assertEquals("Solutions", french.getObject("position").getString("unit"));
		assertEquals("Développeur", french.getObject("position").getString("classification"));
	}
	
}
