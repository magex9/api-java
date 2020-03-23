package ca.magex.crm.api.test;

import ca.magex.crm.api.services.OrganizationService;

public class OrganizationServiceTestDataPopulator {

	public OrganizationService populate(OrganizationService orgainzations) {
		orgainzations.createOrganization("Scott");
		orgainzations.createOrganization("Jonny");
		return orgainzations;
	}
	
}
