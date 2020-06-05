package ca.magex.crm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.filters.LocalizedFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Message;
import ca.magex.json.model.JsonArray;
import ca.magex.json.model.JsonBoolean;
import ca.magex.json.model.JsonElement;
import ca.magex.json.model.JsonNumber;
import ca.magex.json.model.JsonObject;
import ca.magex.json.model.JsonText;
import ca.magex.json.util.FormattedStringBuilder;

public class CrmAsserts {
	
	public static final Localized GROUP = new Localized("GRP", "Group", "Groupe");
	
	public static final Localized ADMIN = new Localized("ADM", "Admin", "Admin");
	
	public static final Localized SYS = new Localized("SYS", "System", "Système");
	
	public static final Localized SYS_ADMIN = new Localized("SYS_ADMIN", "System Administrator", "Adminstrator du système");
	
	public static final Localized ORG = new Localized("ORG", "Organization", "Organisation");
	
	public static final Localized ORG_NAME = new Localized("ORG", "Organization", "Organisation");
	
	public static final Localized ORG_ADMIN = new Localized("ORG_ADMIN", "Organization Administrator", "Adminstrator du l'organization");
	
	public static final Localized ORG_ASSISTANT = new Localized("ORG_ASSISTANT", "Organization Assistant", "Assistant du l'organization");
	
	public static final Localized ENGLISH = new Localized("EN", "English", "Anglais");
	
	public static final Localized FRENCH = new Localized("FR", "French", "Français");
	
	public static final Localized CANADA = new Localized("CA", "Canada", "Canada");
	
	public static final Localized ALBERTA = new Localized("AB", "Alberta", "Alberta");
	
	public static final Localized BRITISH_COLUMBIA = new Localized("BC", "British Columbia", "Colombie britannique");
	
	public static final Localized ONTARIO = new Localized("ON", "Ontario", "Ontario");
	
	public static final Localized QUEBEC = new Localized("QC", "Quebec", "Québec");
	
	public static final Localized NEWFOUNDLAND = new Localized("NL", "Newfoundland and Labrador", "Terre-Neuve et Labrador");
	
	public static final Localized UNITED_STATES = new Localized("US", "United States", "États Unis");
	
	public static final Localized NEW_YORK = new Localized("NY", "United States", "États Unis");
	
	public static final Localized MASSACHUSETTS = new Localized("MA", "Massachusetts", "Massachusetts");
	
	public static final Localized ILLINOIS = new Localized("IL", "Illinois", "Illinois");
	
	public static final Localized MEXICO = new Localized("MX", "Mexico", "Mexique");
	
	public static final Localized NUEVO_LEON = new Localized("NL", "Nuevo Leon", "Nuevo Leon");
	
	public static final Localized ENGLAND = new Localized("GB", "England", "Angleterre");
	
	public static final Localized LONDON = new Localized("JS", "Greater London", "Le Grand Londres");
	
	public static final Localized CHESHIRE = new Localized("CH", "Cheshire", "Cheshire");
	
	public static final Localized FRANCE = new Localized("FR", "France", "Franche");
	
	public static final Localized ILE_DE_FRANCE = new Localized("IF", "Île-de-France", "Île-de-France");
	
	public static final Localized MIDI_PYRENESE = new Localized("MD", "Midi-Pyrénées", "Midi-Pyrénées");
	
	public static final Localized GERMANY = new Localized("DE", "Germany", "Allemagne");
	
	public static final Localized RHINE = new Localized("RH", "North Rhine-Westphalia", "Rhénanie du Nord-Westphalie");
	
	public static final MailingAddress MAILING_ADDRESS = new MailingAddress("123 Main St", "Ottawa", QUEBEC.getCode(), CANADA.getCode(), "K1K1K1");

	public static final MailingAddress CA_ADDRESS = new MailingAddress("111 Wellington Street", "Ottawa", ONTARIO.getCode(), CANADA.getCode(), "K1A 0A9");
	
	public static final MailingAddress NL_ADDRESS = new MailingAddress("90 Avalon Drive", "Labrador City", NEWFOUNDLAND.getCode(), CANADA.getCode(), "A2V 2Y2");

	public static final MailingAddress US_ADDRESS = new MailingAddress("465 Huntington Ave", "Boston", MASSACHUSETTS.getCode(), UNITED_STATES.getCode(), "02115");
	
	public static final MailingAddress MX_ADDRESS = new MailingAddress("120 Col. Hipodromo Condesa", "Monterrey", NUEVO_LEON.getCode(), MEXICO.getCode(), "06100");
	
	public static final MailingAddress EN_ADDRESS = new MailingAddress("35 Tower Hill", "London", LONDON.getEnglishName(), ENGLAND.getCode(), "EC3N 4DR");
	
	public static final MailingAddress FR_ADDRESS = new MailingAddress("5 Avenue Anatole", "Paris", ILE_DE_FRANCE.getEnglishName(), FRANCE.getCode(), "75007");
	
	public static final MailingAddress DE_ADDRESS = new MailingAddress("Porschepl. 1", "Stuttgart", MIDI_PYRENESE.getEnglishName(), GERMANY.getCode(), "70435");
	
	public static final PersonName PERSON_NAME = new PersonName("3", "Chris", "P", "Bacon");
	
	public static final String PERSON_EMAIL = "crhis@bacon.com";
	
	public static final String PERSON_TITLE = "Professional Tester";
	
	public static final Telephone PERSON_TELEPHONE = new Telephone("6135551234", "42");
	
	public static final String PERSON_FAX = "6138884567";
	
	public static final Communication COMMUNICATIONS = new Communication("Developer", ENGLISH.getCode(), "user@work.ca", new Telephone("5551234567", "42"), "8881234567");
	
	public static final BusinessSector BUSINESS_SECTOR = new BusinessSector("1", "External", "External");
	
	public static final BusinessUnit BUSINESS_UNIT = new BusinessUnit("1", "Solutions", "Solutions");
	
	public static final BusinessClassification BUSINESS_CLASSIFICATION = new BusinessClassification("1", "Developer", "Développeur");
	
	public static final BusinessPosition BUSINESS_POSITION = new BusinessPosition(BUSINESS_SECTOR.getCode(), BUSINESS_UNIT.getCode(), BUSINESS_CLASSIFICATION.getCode());
	
	public static final List<Localized> LOCALIZED_SORTING_OPTIONS = List.of(
		new Localized("A", "A", "A"),
		new Localized("B", "$", " space first"),
		new Localized("C", "$ Store", "/ divide"),
		new Localized("D", "_", "+ plus"),
		new Localized("E", "æther", "se dépêcher"),
		new Localized("F", "Montreal", "À"),
		new Localized("G", "Montréal", "ÂÂ"),
		new Localized("H", "aaa", "A"),
		new Localized("I", "aaAB", "a"),
		new Localized("J", "AAAA", "ÄAÄ"),
		new Localized("K", "AaaB", "äaÄ"),
		new Localized("L", "AaAb", "à"),
		new Localized("M", "{ mustashe }", "Ÿ"),
		new Localized("N", " space first", "énorme"),
		new Localized("O", "a", "Tout"),
		new Localized("P", "1 First", "tout à fait"),
		new Localized("Q", "Java", "Tout le"),
		new Localized("R", "AA", "être"),
		new Localized("S", "0 Zero", "garçon"),
		new Localized("T", "Zzzz", "était"),
		new Localized("U", "Æther", "Île"),
		new Localized("V", "a", "mère"),
		new Localized("W", "resume", "Où"),
		new Localized("X", "Résumé", "français"),
		new Localized("Y", "résumé", "% percent"),
		new Localized("Z", "[ brackets ]", "* astrix"),
		new Localized("A_B", "Boy", "Garçon"),
		new Localized("A_B_", "** Footnote", "Jusqu’à ce que"),
		new Localized("AB_C", "* Astrix", "œil"),
		new Localized("XYZ", "XYZ", "XYZ"),
		ENGLISH, FRENCH
	);
	
	public static final List<String> LOCALIZED_SORTED_ENGLISH_ASC = List.of(
		"_",
		"[ brackets ]",
		"{ mustashe }",		
		"$",
		"$ Store",		
		"** Footnote",
		"* Astrix",		
		"0 Zero",
		"1 First",
		"A",
		"a",
		"a",
		"AA",
		"aaa",
		"AAAA",
		"AaAb",
		"AaaB",
		"aaAB",
		"Æther",
		"æther",
		"Boy",
		"English",
		"French",
		"Java",
		"Montreal",
		"Montréal",
		"Résumé",
		"resume",
		"résumé",
		" space first",
		"XYZ",
		"Zzzz"
	);
	
	public static final List<String> LOCALIZED_SORTED_ENGLISH_DESC = reverse(LOCALIZED_SORTED_ENGLISH_ASC);
	
	public static final List<String> LOCALIZED_SORTED_FRENCH_ASC = List.of(		
		"/ divide",
		"* astrix",
		"% percent",
		"+ plus",						
		"A",
		"A",
		"a",
		"À",
		"à",
		"ÂÂ",
		"ÄAÄ",
		"äaÄ",
		"Anglais",
		"énorme",
		"était",
		"être",		
		"Français",
		"français",
		"Garçon",
		"garçon",
		"Île",
		"Jusqu’à ce que",
		"mère",
		"œil",
		"Où",
		"se dépêcher",
		" space first",		
		"Tout",
		"tout à fait",
		"Tout le",
		"XYZ",
		"Ÿ"
	);
	
	public static final List<String> LOCALIZED_SORTED_FRENCH_DESC = reverse(LOCALIZED_SORTED_FRENCH_ASC);
	
	public Stream<Localized> apply(LocalizedFilter filter) {
		return LOCALIZED_SORTING_OPTIONS.stream()
			.filter(g -> filter.apply(g));
	}
	
	public long countLocalizedNames(LocalizedFilter filter) {
		return apply(filter).count();
	}
	
	public FilteredPage<String> findLocalizedNames(LocalizedFilter filter, Paging paging, Locale locale) {
		List<String> matching = apply(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.map(i -> i.get(locale))
			.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, matching, paging);
	}
	
	@Test
	public void testLocalizedSortingEnglishAscending() throws Exception {
		Paging paging = LocalizedFilter.getDefaultPaging().withSort(LocalizedFilter.SORT_ENGLISH_ASC).allItems();
		List<String> names = findLocalizedNames(new LocalizedFilter(), paging, Lang.ENGLISH).getContent();
		assertEquals(LOCALIZED_SORTING_OPTIONS.size(), names.size());
		assertEquals(LOCALIZED_SORTED_ENGLISH_ASC, names);
	}
	
	@Test
	public void testLocalizedSortingEnglishDescending() throws Exception {
		Paging paging = LocalizedFilter.getDefaultPaging().withSort(LocalizedFilter.SORT_ENGLISH_DESC).allItems();
		List<String> names = findLocalizedNames(new LocalizedFilter(), paging, Lang.ENGLISH).getContent();
		assertEquals(LOCALIZED_SORTING_OPTIONS.size(), names.size());
		assertEquals(LOCALIZED_SORTED_ENGLISH_DESC, names);
	}
	
	@Test
	public void testLocalizedSortingFrenchAscending() throws Exception {
		Paging paging = LocalizedFilter.getDefaultPaging().withSort(LocalizedFilter.SORT_FRENCH_ASC).allItems();
		List<String> names = findLocalizedNames(new LocalizedFilter(), paging, Lang.FRENCH).getContent();
		assertEquals(LOCALIZED_SORTING_OPTIONS.size(), names.size());
		assertEquals(LOCALIZED_SORTED_FRENCH_ASC, names);
	}
	
	@Test
	public void testLocalizedSortingFrenchDescending() throws Exception {
		Paging paging = LocalizedFilter.getDefaultPaging().withSort(LocalizedFilter.SORT_FRENCH_DESC).allItems();
		List<String> names = findLocalizedNames(new LocalizedFilter(), paging, Lang.FRENCH).getContent();
		assertEquals(LOCALIZED_SORTING_OPTIONS.size(), names.size());
		assertEquals(LOCALIZED_SORTED_FRENCH_DESC, names);
	}
	
	public static <T> List<T> reverse(List<T> list) {
		List<T> reversed = new ArrayList<T>(list);
		Collections.reverse(reversed);
		return reversed;
	}
	
	public static <T> void printList(List<T> list, Class<T> type) {
		StringBuilder sb = new StringBuilder();
		sb.append("List<" + type.getSimpleName() + "> list = List.of(\n");
		for (int i = 0; i < list.size(); i++) {
			T item = list.get(i);
			if (i == list.size() - 1) {
				sb.append("\t\"" + item + "\"\n");
			} else {
				sb.append("\t\"" + item + "\",\n");
			}
		}
		sb.append(");\n");
		System.out.println("\n");
		System.out.println(sb.toString());
	}
	
	public static <T> void assertSinglePage(Page<T> page, int totalElements) {
		assertPage(page, totalElements, totalElements, 1, false, false, false, false);
	}
	
	public static <T> void assertPage(Page<T> page, int totalElements, int pageSize, int pageNumber, boolean first, boolean previous, boolean next, boolean last) {
		assertEquals(totalElements, page.getTotalElements());
		assertEquals(pageNumber, page.getNumber());
		assertEquals(pageSize, page.getContent().size());
		assertEquals(first, page.isFirst());
		assertEquals(previous, page.hasPrevious());
		assertEquals(next, page.hasNext());
		assertEquals(last, page.isLast());
	}
	
	public static void assertMessage(Message message, Identifier identifier, String type, String path, String reason) {
		if (identifier != null)
			assertEquals(identifier, message.getIdentifier());
		assertEquals(type, message.getType());
		assertEquals(path, message.getPath());
		assertTrue(message.getReason().get(Lang.ENGLISH) + " !~ " + reason, message.getReason().get(Lang.ENGLISH).matches(reason));
	}

	public static void assertBadRequestMessage(BadRequestException e, Identifier identifier, String type, String path, String reason) {
		assertEquals(e.getMessages().stream().map((m) -> m.toString()).collect(Collectors.joining()), 1, e.getMessages().size());
		assertMessage(e.getMessages().get(0), identifier, type, path, reason);
	}
	
	public void printLookupAsserts(JsonObject json) {
		System.out.println("====================================================");
		System.out.println("\t\t//printLookupAsserts(json);");
		System.out.println("\t\tassertEquals(" + json.getArray("content").size() + ", json.getInt(\"total\"));");
		System.out.println("\t\tassertEquals(JsonArray.class, json.get(\"content\").getClass());");
		System.out.println("\t\tassertEquals(" + json.getArray("content").size() + ", json.getArray(\"content\").size());");
		for (int i = 0; i < json.getArray("content").size(); i++) {
			System.out.println("\t\tassertEquals(\"" + json.getArray("content").getString(i) + "\", json.getArray(\"content\").getString(" + i + "));");
		}
		System.out.println("====================================================");
	}
	
	public static void assertSingleJsonMessage(JsonArray json, Identifier identifier, String type, String path,
			String reason) {
		assertEquals(1, json.size());
		if (identifier == null) {
			assertEquals(List.of("type", "path", "reason"), json.getObject(0).keys());
		} else {
			assertEquals(List.of("identifier", "type", "path", "reason"), json.getObject(0).keys());
			assertEquals(identifier, new Identifier(json.getObject(0).getString("identifier")));
		}
		assertEquals(type, json.getObject(0).getString("type"));
		assertEquals(path, json.getObject(0).getString("path"));
		assertEquals(reason, json.getObject(0).getString("reason"));
	}

}
