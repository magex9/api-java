package ca.magex.crm.amnesia.services;

import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.services.SecuredCrmServices;
import ca.magex.crm.api.system.Lang;

public class AmnesiaTestDataPopulator {

	public static SecuredCrmServices populate(SecuredCrmServices service) {
		
		OrganizationDetails gotham = service.createOrganization("Gotham Bank");
		LocationDetails gothamMain = service.createLocation(gotham.getOrganizationId(), "Alta Vista", "ALTAVISTA", new MailingAddress("1000 Alta Vista Drive", "Gotham", "Ontario", service.findCountryByCode("CA").getName(Lang.ENGLISH), "K1K1K1"));
		service.updateOrganizationMainLocation(gotham.getOrganizationId(), gothamMain.getLocationId());
		
		OrganizationDetails joker = service.createOrganization("Jokers Money");
		LocationDetails jokerMain = service.createLocation(gotham.getOrganizationId(), "Joker Nest", "JOKERNEST", new MailingAddress("234 Main Street", "Gotham", "Ontario", service.findCountryByCode("CA").getName(Lang.ENGLISH), "K1K4R4"));
		service.updateOrganizationMainLocation(joker.getOrganizationId(), jokerMain.getLocationId());
		service.disableOrganization(joker.getOrganizationId());
		
		OrganizationDetails shield = service.createOrganization("Shield");
		LocationDetails shieldMain = service.createLocation(shield.getOrganizationId(), "Head Quarters", "HQ", new MailingAddress("99 Blue Jays Way", "Gotham", "Ontario", service.findCountryByCode("CA").getName(Lang.ENGLISH), "J2N8A4"));
		service.updateOrganizationMainLocation(shield.getOrganizationId(), shieldMain.getLocationId());
		
		return service;
	}
	
}
