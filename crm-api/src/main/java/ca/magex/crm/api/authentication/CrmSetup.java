package ca.magex.crm.api.authentication;

import java.util.List;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public class CrmSetup {

	public static User initialize(CrmServices crm, String username, String email, PersonName name) {
		Group systemRoles = crm.createGroup(new Localized("System", "Systeme"));
		// application shutdown (os user)
		crm.createRole(systemRoles.getGroupId(), "SYS_ADMIN", new Localized("System Admin", "Administrateur du systeme"));
		// monitoring and services (monitoring tool)
		crm.createRole(systemRoles.getGroupId(), "SYS_ACTUATOR", new Localized("System Actuator", "Actuator du systeme"));
		// managing groups and roles (service desk)
		crm.createRole(systemRoles.getGroupId(), "SYS_ACCESS", new Localized("System Access", "Access du systeme"));

		Group applicationRoles = crm.createGroup(new Localized("Application", "Application"));
		// token verification (application background user)
		crm.createRole(applicationRoles.getGroupId(), "APP_AUTH_REQUEST", new Localized("Authorization Requestor", "Demandeur d'Autorisation"));

		Group crmRoles = crm.createGroup(new Localized("Customer Relationship Management"));
		// create and management all organizations
		crm.createRole(crmRoles.getGroupId(), "CRM_ADMIN", new Localized("CRM Admin", "Administrateur GRC"));
		// user of a single organization
		crm.createRole(crmRoles.getGroupId(), "CRM_USER", new Localized("CRM Viewer", "Visionneuse GRC"));
		
		Identifier systemOrganizationId = crm.createOrganization("System").getOrganizationId();
		Identifier systemPersonId = crm.createPerson(systemOrganizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
		return crm.createUser(systemPersonId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN"));
	}
	
	public static boolean verify(CrmServices crm, String username) {
		// Verify SYSTEM and CRM Role exist
		
		// Verify active user with SYS_* roles exist
		
		
		
		return true;
	}
	
}
