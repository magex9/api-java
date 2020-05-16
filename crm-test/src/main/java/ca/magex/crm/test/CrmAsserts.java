package ca.magex.crm.test;

import static org.junit.Assert.assertEquals;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

public class CrmAsserts {
	
	public static final Localized GROUP = new Localized("GRP", "Group", "Groupe");
	
	public static final Localized ADMIN = new Localized("ADM", "Admin", "Admin");
	
	public static final Localized SYS = new Localized("SYS", "System", "Systeme");
	
	public static final Localized SYS_ADMIN = new Localized("SYS_ADMIN", "System Administrator", "Adminstrator du system");
	
	public static final Localized ORG = new Localized("ORG", "Organization", "Organisation");
	
	public static final Localized ORG_ADMIN = new Localized("ORG_ADMIN", "Organization Administrator", "Adminstrator du l'organization");
	
	public static final Localized ORG_LEAD = new Localized("ORG_LEAD", "Organization Lead", "Chef du l'organisation");
	
	public static final Localized ORG_OFFICER = new Localized("ORG_OFFICER", "Organization Officer", "Agent d'organisation");
	
	public static final Localized ENGLISH = new Localized("EN", "English", "Anglais");
	
	public static final Localized FRENCH = new Localized("FR", "French", "Français");
	
	public static final Localized CANADA = new Localized("CA", "Canada", "Canada");
	
	public static final Localized ALBERTA = new Localized("AB", "Alberta", "Alberta");
	
	public static final Localized BRITISH_COLUMBIA = new Localized("BC", "British Columbia", "Colombie britannique");
	
	public static final Localized ONTARIO = new Localized("ON", "Ontario", "Ontario");
	
	public static final Localized QUEBEC = new Localized("QC", "Quebec", "Québec");
	
	public static final Localized UNITED_STATES = new Localized("US", "United States", "États Unis");
	
	public static final Localized NEW_YORK = new Localized("NY", "United States", "États Unis");
	
	public static final Localized MASSACHUSETTS = new Localized("MA", "Massachusetts", "Massachusetts");
	
	public static final Localized ILLINOIS = new Localized("IL", "Illinois", "Illinois");
	
	public static final Localized ENGLAND = new Localized("GB", "England", "Angleterre");
	
	public static final Localized LONDON = new Localized("JS", "Greater London", "Le Grand Londres");
	
	public static final Localized CHESHIRE = new Localized("CH", "Cheshire", "Cheshire");
	
	public static final Localized FRANCE = new Localized("FR", "France", "Franche");
	
	public static final Localized ILE_DE_FRANCE = new Localized("IF", "Île-de-France", "Île-de-France");
	
	public static final Localized MIDI_PYRENESE = new Localized("MD", "Midi-Pyrénées", "Midi-Pyrénées");
	
	public static final Localized GERMANY = new Localized("DE", "Germany", "Allemagne");
	
	public static final Localized RHINE = new Localized("RH", "North Rhine-Westphalia", "Rhénanie du Nord-Westphalie");
	
	public static final MailingAddress MAILING_ADDRESS = new MailingAddress("123 Main St", "Ottawa", QUEBEC, CANADA, "K1K1K1");
	
	public static final PersonName PERSON_NAME = new PersonName("Mr.", "Chris", "P", "Bacon");
	
	public static final Communication COMMUNICATIONS = new Communication("Developer", ENGLISH, "user@work.ca", new Telephone("5551234567"), null);
	
	public static final BusinessPosition BUSINESS_POSITION = new BusinessPosition("Corporate Services", "Development", "Developer");
	
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

	public static void assertBadRequestMessage(BadRequestException e, Identifier identifier, String type, String path, String message) {
		assertEquals(1, e.getMessages().size());
		if (identifier != null)
			assertEquals(identifier, e.getMessages().get(0).getIdentifier());
		assertEquals(type, e.getMessages().get(0).getType());
		assertEquals(path, e.getMessages().get(0).getPath());
		assertEquals(message, e.getMessages().get(0).getReason().get(Lang.ENGLISH));
	}
	
}
