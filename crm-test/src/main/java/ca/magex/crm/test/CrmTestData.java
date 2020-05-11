package ca.magex.crm.test;

import java.util.Arrays;
import java.util.List;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.common.Telephone;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

public class CrmTestData {

	/**
	 * Create the main administrator org thats has access to all organizations
	 */
	public static Identifier createSysAdmin(CrmOrganizationService orgs, CrmLocationService locations, CrmPersonService persons, CrmUserService users, CrmPasswordService passwords) {
		Identifier organizationId = orgs.createOrganization("MageX", List.of("CRM")).getOrganizationId();

		MailingAddress address = new MailingAddress("1234 Alta Vista Drive", "Ottawa", "Ontario", "Canada", "K3J 3I3");
		Identifier mainLocationId = locations.createLocation(organizationId, "Headquarters", "HQ", address).getLocationId();
		orgs.updateOrganizationMainLocation(organizationId, mainLocationId);
		
		PersonName scottName = new PersonName("Mr.", "Scott", null, "Finlay");
		Communication scottComm = new Communication("Developer", "English", "scott@work.ca", new Telephone("6132345535"), null);
		BusinessPosition scottJob = new BusinessPosition("IM/IT", "Development", "Developer");
		Identifier scottId = persons.createPerson(organizationId, scottName, address, scottComm, scottJob).getPersonId();
		users.createUser(scottId, "finlays", Arrays.asList("ORG_ADMIN", "CRM_ADMIN"));
		orgs.updateOrganizationMainContact(organizationId, scottId);
		
		return organizationId;
	}
	
	/**
	 * Create a sample organization with a regular user.
	 */
	public static Identifier createOmniTech(CrmOrganizationService orgs, CrmLocationService locations, CrmPersonService persons, CrmUserService users, CrmPasswordService passwords) {
		Identifier organizationId = orgs.createOrganization("Omni Tech", List.of("ORG")).getOrganizationId();
		
		MailingAddress mainAddress = new MailingAddress("1761 Township Road", "Leduc", "Alberta", "Canada", "T9E 2X2");
		Identifier mainLocationId = locations.createLocation(organizationId, "HQ", "HQ", mainAddress).getLocationId();
		orgs.updateOrganizationMainLocation(organizationId, mainLocationId);
		
		PersonName jennaName = new PersonName("Mrs.", "Jenna", "J", "Dunn");
		Communication jennaComm = new Communication("Chief Technology Officer", "French", "Jenna@omnitech.com", new Telephone("4168814588"), "4169985565");
		BusinessPosition jennaJob = new BusinessPosition("Corporate Services", "Technology", "Executive");
		Identifier jennaId = persons.createPerson(organizationId, jennaName, mainAddress, jennaComm, jennaJob).getPersonId();
		users.createUser(jennaId, "jenna", List.of("ORG_ADMIN"));
		
		PersonName chaseName = new PersonName("Mr.", "Chase", "L", "Montgomery");
		Communication chaseComm = new Communication("Financial Advisor", "English", "chase@omnitech.com", new Telephone("4187786566"), "4169985565");
		BusinessPosition chaseJob = new BusinessPosition("Corporate Services", "Finance", "Advisor");
		Identifier chaseId = persons.createPerson(organizationId, chaseName, mainAddress, chaseComm, chaseJob).getPersonId();
		users.createUser(chaseId, "chase", Arrays.asList("ORG_USER"));
		
		return organizationId;
	}
	
}
