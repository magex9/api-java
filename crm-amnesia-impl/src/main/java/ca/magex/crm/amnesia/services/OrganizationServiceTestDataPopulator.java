package ca.magex.crm.amnesia.services;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;

public class OrganizationServiceTestDataPopulator {

	public static OrganizationService populate(OrganizationService organizations) {
		
		OrganizationDetails gotham = organizations.createOrganization("Gotham Bank");
		LocationDetails gothamMain = organizations.createLocation(gotham.getOrganizationId(), "Alta Vista", "ALTAVISTA", new MailingAddress("1000 Alta Vista Drive", "Gotham", "Ontario", new Country("CA", "Canada"), "K1K1K1"));
		organizations.updateOrganizationMainLocation(gotham.getOrganizationId(), gothamMain.getLocationId());
		
		OrganizationDetails joker = organizations.createOrganization("Jokers Money");
		LocationDetails jokerMain = organizations.createLocation(gotham.getOrganizationId(), "Joker Nest", "JOKERNEST", new MailingAddress("234 Main Street", "Gotham", "Ontario", new Country("CA", "Canada"), "K1K4R4"));
		organizations.updateOrganizationMainLocation(joker.getOrganizationId(), jokerMain.getLocationId());
		
		OrganizationDetails shield = organizations.createOrganization("Shield");
		LocationDetails shieldMain = organizations.createLocation(shield.getOrganizationId(), "Head Quarters", "HQ", new MailingAddress("99 Blue Jays Way", "Gotham", "Ontario", new Country("CA", "Canada"), "J2N8A4"));
		organizations.updateOrganizationMainLocation(shield.getOrganizationId(), shieldMain.getLocationId());
		
		return organizations;
	}
	
}
