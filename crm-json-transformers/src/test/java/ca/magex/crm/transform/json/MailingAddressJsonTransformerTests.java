package ca.magex.crm.transform.json;

import static ca.magex.crm.test.CrmAsserts.CANADA;
import static ca.magex.crm.test.CrmAsserts.CA_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.DE_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.EN_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.FRANCE;
import static ca.magex.crm.test.CrmAsserts.FR_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MAILING_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.MEXICO;
import static ca.magex.crm.test.CrmAsserts.MX_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.NEWFOUNDLAND;
import static ca.magex.crm.test.CrmAsserts.NL_ADDRESS;
import static ca.magex.crm.test.CrmAsserts.NUEVO_LEON;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_EMAIL;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_ORG;
import static ca.magex.crm.test.CrmAsserts.SYSTEM_PERSON;
import static ca.magex.crm.test.CrmAsserts.UNITED_STATES;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.system.Choice;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.crm.transform.TestCrm;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class MailingAddressJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<MailingAddress, JsonElement> transformer;
	
	@Before
	public void setup() {
		crm = TestCrm.build();
		crm.initializeSystem(SYSTEM_ORG, SYSTEM_PERSON, SYSTEM_EMAIL, "admin", "admin");
		transformer = new MailingAddressJsonTransformer(crm);
	}
	
	@Test
	public void testTransformerType() throws Exception {
		assertEquals(MailingAddress.class, transformer.getSourceType());
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
		JsonObject linked = (JsonObject)transformer.format(MAILING_ADDRESS, null);
		//JsonAsserts.print(linked, "linked");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), linked.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", linked.getString("@context"));
		assertEquals("123 Main St", linked.getString("street"));
		assertEquals("Ottawa", linked.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", linked.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/qc", linked.getObject("province").getString("@id"));
		assertEquals("CA/QC", linked.getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), linked.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", linked.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", linked.getObject("country").getString("@id"));
		assertEquals("CA", linked.getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(MAILING_ADDRESS, Lang.ROOT);
		//JsonAsserts.print(root, "root");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), root.keys());
		assertEquals("123 Main St", root.getString("street"));
		assertEquals("Ottawa", root.getString("city"));
		assertEquals("CA/QC", root.getString("province"));
		assertEquals("CA", root.getString("country"));
		assertEquals("K1K1K1", root.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(MAILING_ADDRESS, Lang.ENGLISH);
		//JsonAsserts.print(english, "english");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("123 Main St", english.getString("street"));
		assertEquals("Ottawa", english.getString("city"));
		assertEquals("Quebec", english.getString("province"));
		assertEquals("Canada", english.getString("country"));
		assertEquals("K1K1K1", english.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(english, Lang.ENGLISH));
	}
	
	@Test
	public void testFrenchJson() throws Exception {
		JsonObject french = (JsonObject)transformer.format(MAILING_ADDRESS, Lang.FRENCH);
		//JsonAsserts.print(french, "french");
		assertEquals(List.of("street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("123 Main St", french.getString("street"));
		assertEquals("Ottawa", french.getString("city"));
		assertEquals("Québec", french.getString("province"));
		assertEquals("Canada", french.getString("country"));
		assertEquals("K1K1K1", french.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(french, Lang.FRENCH));
	}
	
	@Test
	public void testLinkedJsonCanadianAddress() throws Exception {
		JsonObject ca = (JsonObject)transformer.format(CA_ADDRESS, null);
		//JsonAsserts.print(ca, "ca");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), ca.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", ca.getString("@context"));
		assertEquals("111 Wellington Street", ca.getString("street"));
		assertEquals("Ottawa", ca.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), ca.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", ca.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/on", ca.getObject("province").getString("@id"));
		assertEquals("CA/ON", ca.getObject("province").getString("@value"));
		assertEquals("Ontario", ca.getObject("province").getString("@en"));
		assertEquals("Ontario", ca.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), ca.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", ca.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", ca.getObject("country").getString("@id"));
		assertEquals("CA", ca.getObject("country").getString("@value"));
		assertEquals("Canada", ca.getObject("country").getString("@en"));
		assertEquals("Canada", ca.getObject("country").getString("@fr"));
		assertEquals("K1A 0A9", ca.getString("postalCode"));
		assertEquals(CA_ADDRESS, transformer.parse(ca, null));
		assertEquals(CA_ADDRESS.toString(), transformer.parse(ca, null).toString());
	}
	
	@Test
	public void testLinkedJsonNewfoundlandAddress() throws Exception {
		JsonObject nl = (JsonObject)transformer.format(NL_ADDRESS, null);
		//JsonAsserts.print(nl, "nl");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), nl.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", nl.getString("@context"));
		assertEquals("90 Avalon Drive", nl.getString("street"));
		assertEquals("Labrador City", nl.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), nl.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", nl.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/nl", nl.getObject("province").getString("@id"));
		assertEquals("CA/NL", nl.getObject("province").getString("@value"));
		assertEquals("Newfoundland and Labrador", nl.getObject("province").getString("@en"));
		assertEquals("Terre-Neuve et Labrador", nl.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), nl.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", nl.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", nl.getObject("country").getString("@id"));
		assertEquals("CA", nl.getObject("country").getString("@value"));
		assertEquals("Canada", nl.getObject("country").getString("@en"));
		assertEquals("Canada", nl.getObject("country").getString("@fr"));
		assertEquals("A2V 2Y2", nl.getString("postalCode"));
		assertEquals(NL_ADDRESS, transformer.parse(nl, null));
		assertEquals(NL_ADDRESS.toString(), transformer.parse(nl, null).toString());
	}
	
	@Test
	public void testLinkedJsonAmericanAddress() throws Exception {
		JsonObject us = (JsonObject)transformer.format(US_ADDRESS, null);
		//JsonAsserts.print(us, "us");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), us.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", us.getString("@context"));
		assertEquals("465 Huntington Ave", us.getString("street"));
		assertEquals("Boston", us.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), us.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", us.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/us/ma", us.getObject("province").getString("@id"));
		assertEquals("US/MA", us.getObject("province").getString("@value"));
		assertEquals("Massachusetts", us.getObject("province").getString("@en"));
		assertEquals("Massachusetts", us.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), us.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", us.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/us", us.getObject("country").getString("@id"));
		assertEquals("US", us.getObject("country").getString("@value"));
		assertEquals("United States", us.getObject("country").getString("@en"));
		assertEquals("États-Unis d'Amérique", us.getObject("country").getString("@fr"));
		assertEquals("02115", us.getString("postalCode"));
		assertEquals(US_ADDRESS, transformer.parse(us, null));
		assertEquals(US_ADDRESS.toString(), transformer.parse(us, null).toString());
	}
	
	@Test
	public void testLinkedJsonMexicanAddress() throws Exception {
		JsonObject mx = (JsonObject)transformer.format(MX_ADDRESS, null);
		//JsonAsserts.print(mx, "mx");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), mx.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", mx.getString("@context"));
		assertEquals("120 Col. Hipodromo Condesa", mx.getString("street"));
		assertEquals("Monterrey", mx.getString("city"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), mx.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", mx.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/mx/nl", mx.getObject("province").getString("@id"));
		assertEquals("MX/NL", mx.getObject("province").getString("@value"));
		assertEquals("Nuevo Leon", mx.getObject("province").getString("@en"));
		assertEquals("Nuevo Leon", mx.getObject("province").getString("@fr"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), mx.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", mx.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/mx", mx.getObject("country").getString("@id"));
		assertEquals("MX", mx.getObject("country").getString("@value"));
		assertEquals("Mexico", mx.getObject("country").getString("@en"));
		assertEquals("Mexique", mx.getObject("country").getString("@fr"));
		assertEquals("06100", mx.getString("postalCode"));
		assertEquals(MX_ADDRESS, transformer.parse(mx, null));
		assertEquals(MX_ADDRESS.toString(), transformer.parse(mx, null).toString());
	}
	
	@Test
	public void testLinkedJsonBritishAddress() throws Exception {
		JsonObject en = (JsonObject)transformer.format(EN_ADDRESS, null);
		//JsonAsserts.print(en, "en");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), en.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", en.getString("@context"));
		assertEquals("35 Tower Hill", en.getString("street"));
		assertEquals("London", en.getString("city"));
		assertEquals("Greater London", en.getString("province"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), en.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", en.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/gb", en.getObject("country").getString("@id"));
		assertEquals("GB", en.getObject("country").getString("@value"));
		assertEquals("United Kingdom", en.getObject("country").getString("@en"));
		assertEquals("Royaume-Uni", en.getObject("country").getString("@fr"));
		assertEquals("EC3N 4DR", en.getString("postalCode"));
		assertEquals(EN_ADDRESS, transformer.parse(en, null));
		assertEquals(EN_ADDRESS.toString(), transformer.parse(en, null).toString());
	}
	
	@Test
	public void testLinkedJsonFranceAddress() throws Exception {
		JsonObject fr = (JsonObject)transformer.format(FR_ADDRESS, null);
		//JsonAsserts.print(fr, "fr");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), fr.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", fr.getString("@context"));
		assertEquals("5 Avenue Anatole", fr.getString("street"));
		assertEquals("Paris", fr.getString("city"));
		assertEquals("Île-de-France", fr.getString("province"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), fr.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", fr.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/fr", fr.getObject("country").getString("@id"));
		assertEquals("FR", fr.getObject("country").getString("@value"));
		assertEquals("France", fr.getObject("country").getString("@en"));
		assertEquals("France", fr.getObject("country").getString("@fr"));
		assertEquals("75007", fr.getString("postalCode"));
		assertEquals(FR_ADDRESS, transformer.parse(fr, null));
		assertEquals(FR_ADDRESS.toString(), transformer.parse(fr, null).toString());
	}

	@Test
	public void testLinkedJsonGermanAddress() throws Exception {
		JsonObject de = (JsonObject)transformer.format(DE_ADDRESS, null);
		//JsonAsserts.print(de, "de");
		assertEquals(List.of("@context", "street", "city", "province", "country", "postalCode"), de.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", de.getString("@context"));
		assertEquals("Porschepl. 1", de.getString("street"));
		assertEquals("Stuttgart", de.getString("city"));
		assertEquals("Midi-Pyrénées", de.getString("province"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), de.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", de.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/de", de.getObject("country").getString("@id"));
		assertEquals("DE", de.getObject("country").getString("@value"));
		assertEquals("Germany", de.getObject("country").getString("@en"));
		assertEquals("Allemagne", de.getObject("country").getString("@fr"));
		assertEquals("70435", de.getString("postalCode"));
		assertEquals(DE_ADDRESS, transformer.parse(de, null));
		assertEquals(DE_ADDRESS.toString(), transformer.parse(de, null).toString());
	}
	
	@Test
	public void testProvinceCountryNull() throws Exception {
		MailingAddress address = new MailingAddress("123 Main Street", "Ottawa", null, null, null);
		JsonObject json = (JsonObject)transformer.format(address, null);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "street", "city"), json.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", json.getString("@context"));
		assertEquals("123 Main Street", json.getString("street"));
		assertEquals("Ottawa", json.getString("city"));
		assertEquals(address, transformer.parse(json, null));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testCountryOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, null, CANADA, null);
		JsonObject json = (JsonObject)transformer.format(address, null);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "country"), json.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", json.getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getObject("country").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Countries", json.getObject("country").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/countries/ca", json.getObject("country").getString("@id"));
		assertEquals("CA", json.getObject("country").getString("@value"));
		assertEquals("Canada", json.getObject("country").getString("@en"));
		assertEquals("Canada", json.getObject("country").getString("@fr"));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testProvinceCodeOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, NEWFOUNDLAND, null, null);
		JsonObject json = (JsonObject)transformer.format(address, null);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("@context", "province"), json.keys());
		assertEquals("http://api.magex.ca/crm/rest/schema/common/MailingAddress", json.getString("@context"));
		assertEquals(List.of("@context", "@id", "@value", "@en", "@fr"), json.getObject("province").keys());
		assertEquals("http://api.magex.ca/crm/schema/options/Provinces", json.getObject("province").getString("@context"));
		assertEquals("http://api.magex.ca/crm/rest/options/provinces/ca/nl", json.getObject("province").getString("@id"));
		assertEquals("CA/NL", json.getObject("province").getString("@value"));
		assertEquals("Newfoundland and Labrador", json.getObject("province").getString("@en"));
		assertEquals("Terre-Neuve et Labrador", json.getObject("province").getString("@fr"));
	}
	
	@Test
	public void testProvinceNameOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, new Choice<>("Ontario"), null, null);
		JsonObject json = (JsonObject)transformer.format(address, Lang.ENGLISH);
		//JsonAsserts.print(json, "json");
		assertEquals(List.of("province"), json.keys());
		assertEquals("Ontario", json.getString("province"));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testNewfoundlandNeuvoLeonConflicts() throws Exception {
		MailingAddress address = new MailingAddress(null, null, NEWFOUNDLAND, null, null);
		JsonObject code = (JsonObject)transformer.format(address, Lang.ROOT);
		//JsonAsserts.print(code, "code");
		assertEquals(List.of("province"), code.keys());
		assertEquals("CA/NL", code.getString("province"));
		assertEquals(address.toString(), transformer.parse(code, Lang.ROOT).toString());

		JsonObject ca = (JsonObject)transformer.format(address.withCountry(CANADA), Lang.ENGLISH);
		//JsonAsserts.print(ca, "ca");
		assertEquals(List.of("province", "country"), ca.keys());
		assertEquals("Newfoundland and Labrador", ca.getString("province"));
		assertEquals("Canada", ca.getString("country"));
		assertEquals(address.withCountry(CANADA).toString(), transformer.parse(ca, Lang.ENGLISH).toString());

		JsonObject mx = (JsonObject)transformer.format(address.withCountry(MEXICO).withProvince(NUEVO_LEON), Lang.ENGLISH);
		//JsonAsserts.print(mx, "mx");
		assertEquals(List.of("province", "country"), mx.keys());
		assertEquals("Nuevo Leon", mx.getString("province"));
		assertEquals("Mexico", mx.getString("country"));
		assertEquals(address.withCountry(MEXICO).withProvince(NUEVO_LEON).toString(), transformer.parse(mx, Lang.ENGLISH).toString());

		JsonObject us = (JsonObject)transformer.format(address.withCountry(UNITED_STATES), Lang.ENGLISH);
		//JsonAsserts.print(us, "us");
		assertEquals(List.of("province", "country"), us.keys());
		assertEquals("Newfoundland and Labrador", us.getString("province"));
		assertEquals("United States", us.getString("country"));
		assertEquals(address.withCountry(UNITED_STATES).toString(), transformer.parse(us, Lang.ENGLISH).toString());

		JsonObject fr = (JsonObject)transformer.format(address.withCountry(FRANCE), Lang.ENGLISH);
		//JsonAsserts.print(fr, "fr");
		assertEquals(List.of("province", "country"), fr.keys());
		assertEquals("Newfoundland and Labrador", fr.getString("province"));
		assertEquals("France", fr.getString("country"));
		assertEquals(address.withCountry(FRANCE).toString(), transformer.parse(fr, Lang.ENGLISH).toString());
	}
	
}
