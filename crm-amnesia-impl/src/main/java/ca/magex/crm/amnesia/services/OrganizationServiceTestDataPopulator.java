package ca.magex.crm.amnesia.services;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.Location;
import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.services.OrganizationService;

public class OrganizationServiceTestDataPopulator {

	public static OrganizationService populate(OrganizationService organizations) {
		
		Organization gotham = organizations.createOrganization("Gotham Bank");
		Location gothamMain = organizations.createLocation(gotham.getOrganizationId(), "Alta Vista", "ALTAVISTA", new MailingAddress("1000 Alta Vista Drive", "Gotham", "Ontario", new Country("CA", "Canada"), "K1K1K1"));
		organizations.updateMainLocation(gotham.getOrganizationId(), gothamMain.getLocationId());
		
		Organization joker = organizations.createOrganization("Jokers Money");
		Location jokerMain = organizations.createLocation(gotham.getOrganizationId(), "Alta Vista", "ALTAVISTA", new MailingAddress("234 Main Street", "Gotham", "Ontario", new Country("CA", "Canada"), "K1K4R4"));
		organizations.updateMainLocation(joker.getOrganizationId(), jokerMain.getLocationId());
		
		return organizations;
	}
	
}