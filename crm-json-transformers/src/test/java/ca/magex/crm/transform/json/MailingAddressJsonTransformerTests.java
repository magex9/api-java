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
import static ca.magex.crm.test.CrmAsserts.UNITED_STATES;
import static ca.magex.crm.test.CrmAsserts.US_ADDRESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.magex.crm.amnesia.services.AmnesiaCrm;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.transform.Transformer;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonObject;

public class MailingAddressJsonTransformerTests {
	
	private Crm crm;
	
	private Transformer<MailingAddress, JsonElement> transformer;
	
	@Before
	public void setup() {
		crm = new AmnesiaCrm();
		transformer = new MailingAddressJsonTransformer(crm,
			new CountryJsonTransformer(crm)
		);
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), linked.keys());
		assertEquals("MailingAddress", linked.getString("@type"));
		assertEquals("123 Main St", linked.getString("street"));
		assertEquals("Ottawa", linked.getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("province").keys());
		assertEquals("Province", linked.getObject("province").getString("@type"));
		assertEquals("QC", linked.getObject("province").getString("@value"));
		assertEquals("Quebec", linked.getObject("province").getString("@en"));
		assertEquals("Québec", linked.getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), linked.getObject("country").keys());
		assertEquals("Country", linked.getObject("country").getString("@type"));
		assertEquals("CA", linked.getObject("country").getString("@value"));
		assertEquals("Canada", linked.getObject("country").getString("@en"));
		assertEquals("Canada", linked.getObject("country").getString("@fr"));
		assertEquals("K1K1K1", linked.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(linked, null));
	}
	
	@Test
	public void testRootJson() throws Exception {
		JsonObject root = (JsonObject)transformer.format(MAILING_ADDRESS, Lang.ROOT);
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), root.keys());
		assertEquals("MailingAddress", root.getString("@type"));
		assertEquals("123 Main St", root.getString("street"));
		assertEquals("Ottawa", root.getString("city"));
		assertEquals("QC", root.getString("province"));
		assertEquals("CA", root.getString("country"));
		assertEquals("K1K1K1", root.getString("postalCode"));
		assertEquals(MAILING_ADDRESS, transformer.parse(root, Lang.ROOT));
	}
	
	@Test
	public void testEnglishJson() throws Exception {
		JsonObject english = (JsonObject)transformer.format(MAILING_ADDRESS, Lang.ENGLISH);
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), english.keys());
		assertEquals("MailingAddress", english.getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), french.keys());
		assertEquals("MailingAddress", french.getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), ca.keys());
		assertEquals("MailingAddress", ca.getString("@type"));
		assertEquals("111 Wellington Street", ca.getString("street"));
		assertEquals("Ottawa", ca.getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), ca.getObject("province").keys());
		assertEquals("Province", ca.getObject("province").getString("@type"));
		assertEquals("ON", ca.getObject("province").getString("@value"));
		assertEquals("Ontario", ca.getObject("province").getString("@en"));
		assertEquals("Ontario", ca.getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), ca.getObject("country").keys());
		assertEquals("Country", ca.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), nl.keys());
		assertEquals("MailingAddress", nl.getString("@type"));
		assertEquals("90 Avalon Drive", nl.getString("street"));
		assertEquals("Labrador City", nl.getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), nl.getObject("province").keys());
		assertEquals("Province", nl.getObject("province").getString("@type"));
		assertEquals("NL", nl.getObject("province").getString("@value"));
		assertEquals("Newfoundland and Labrador", nl.getObject("province").getString("@en"));
		assertEquals("Terre-Neuve et Labrador", nl.getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), nl.getObject("country").keys());
		assertEquals("Country", nl.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), us.keys());
		assertEquals("MailingAddress", us.getString("@type"));
		assertEquals("465 Huntington Ave", us.getString("street"));
		assertEquals("Boston", us.getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), us.getObject("province").keys());
		assertEquals("Province", us.getObject("province").getString("@type"));
		assertEquals("MA", us.getObject("province").getString("@value"));
		assertEquals("Massachusetts", us.getObject("province").getString("@en"));
		assertEquals("Massachusetts", us.getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), us.getObject("country").keys());
		assertEquals("Country", us.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), mx.keys());
		assertEquals("MailingAddress", mx.getString("@type"));
		assertEquals("120 Col. Hipodromo Condesa", mx.getString("street"));
		assertEquals("Monterrey", mx.getString("city"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), mx.getObject("province").keys());
		assertEquals("Province", mx.getObject("province").getString("@type"));
		assertEquals("NL", mx.getObject("province").getString("@value"));
		assertEquals("Nuevo Leon", mx.getObject("province").getString("@en"));
		assertEquals("Nuevo Leon", mx.getObject("province").getString("@fr"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), mx.getObject("country").keys());
		assertEquals("Country", mx.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), en.keys());
		assertEquals("MailingAddress", en.getString("@type"));
		assertEquals("35 Tower Hill", en.getString("street"));
		assertEquals("London", en.getString("city"));
		assertEquals("Greater London", en.getString("province"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), en.getObject("country").keys());
		assertEquals("Country", en.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), fr.keys());
		assertEquals("MailingAddress", fr.getString("@type"));
		assertEquals("5 Avenue Anatole", fr.getString("street"));
		assertEquals("Paris", fr.getString("city"));
		assertEquals("Île-de-France", fr.getString("province"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), fr.getObject("country").keys());
		assertEquals("Country", fr.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city", "province", "country", "postalCode"), de.keys());
		assertEquals("MailingAddress", de.getString("@type"));
		assertEquals("Porschepl. 1", de.getString("street"));
		assertEquals("Stuttgart", de.getString("city"));
		assertEquals("Midi-Pyrénées", de.getString("province"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), de.getObject("country").keys());
		assertEquals("Country", de.getObject("country").getString("@type"));
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
		assertEquals(List.of("@type", "street", "city"), json.keys());
		assertEquals("MailingAddress", json.getString("@type"));
		assertEquals("123 Main Street", json.getString("street"));
		assertEquals("Ottawa", json.getString("city"));
		assertEquals(address, transformer.parse(json, null));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testCountryOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, null, CANADA.getCode(), null);
		JsonObject json = (JsonObject)transformer.format(address, null);
		assertEquals(List.of("@type", "country"), json.keys());
		assertEquals("MailingAddress", json.getString("@type"));
		assertEquals(List.of("@type", "@value", "@en", "@fr"), json.getObject("country").keys());
		assertEquals("Country", json.getObject("country").getString("@type"));
		assertEquals("CA", json.getObject("country").getString("@value"));
		assertEquals("Canada", json.getObject("country").getString("@en"));
		assertEquals("Canada", json.getObject("country").getString("@fr"));
		assertEquals(address, transformer.parse(json, null));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testProvinceCodeOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, NEWFOUNDLAND.getCode(), null, null);
		JsonObject json = (JsonObject)transformer.format(address, null);
		assertEquals(List.of("@type", "province"), json.keys());
		assertEquals("MailingAddress", json.getString("@type"));
		assertEquals("NL", json.getString("province"));
		assertEquals(address, transformer.parse(json, null));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testProvinceNameOnly() throws Exception {
		MailingAddress address = new MailingAddress(null, null, NEWFOUNDLAND.getEnglishName(), null, null);
		JsonObject json = (JsonObject)transformer.format(address, Lang.ENGLISH);
		assertEquals(List.of("@type", "province"), json.keys());
		assertEquals("MailingAddress", json.getString("@type"));
		assertEquals("Newfoundland and Labrador", json.getString("province"));
		assertEquals(address, transformer.parse(json, null));
		assertEquals(address.toString(), transformer.parse(json, null).toString());
	}
	
	@Test
	public void testNewfoundlandNeuvoLeonConflicts() throws Exception {
		MailingAddress address = new MailingAddress(null, null, NEWFOUNDLAND.getCode(), null, null);
		JsonObject code = (JsonObject)transformer.format(address, Lang.ENGLISH);
		assertEquals(List.of("@type", "province"), code.keys());
		assertEquals("MailingAddress", code.getString("@type"));
		assertEquals("NL", code.getString("province"));
		assertEquals(address, transformer.parse(code, null));
		assertEquals(address.toString(), transformer.parse(code, null).toString());

		JsonObject ca = (JsonObject)transformer.format(address.withCountry(CANADA.getCode()), Lang.ENGLISH);
		assertEquals(List.of("@type", "province", "country"), ca.keys());
		assertEquals("MailingAddress", ca.getString("@type"));
		assertEquals("Newfoundland and Labrador", ca.getString("province"));

		JsonObject mx = (JsonObject)transformer.format(address.withCountry(MEXICO.getCode()), Lang.ENGLISH);
		assertEquals(List.of("@type", "province", "country"), ca.keys());
		assertEquals("MailingAddress", mx.getString("@type"));
		assertEquals("Nuevo Leon", mx.getString("province"));

		JsonObject us = (JsonObject)transformer.format(address.withCountry(UNITED_STATES.getCode()), Lang.ENGLISH);
		assertEquals(List.of("@type", "province", "country"), ca.keys());
		assertEquals("MailingAddress", us.getString("@type"));
		assertEquals("NL", us.getString("province"));

		JsonObject fr = (JsonObject)transformer.format(address.withCountry(FRANCE.getCode()), Lang.ENGLISH);
		assertEquals(List.of("@type", "province", "country"), ca.keys());
		assertEquals("MailingAddress", fr.getString("@type"));
		assertEquals("NL", fr.getString("province"));
	}
	
}
