package ca.magex.crm.api.authentication;

import java.util.List;

import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;

public class CrmSetup {

	public static void initializeLookups(CrmLookupService lookups) {
		
	}
	
	public static void initializeRoles(CrmPermissionService permissions) {
		Group systemRoles = permissions.createGroup("SYS", new Localized("System", "Systeme"));
		// application shutdown (os user)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ADMIN", new Localized("System Admin", "Administrateur du systeme"));
		// monitoring and services (monitoring tool)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ACTUATOR", new Localized("System Actuator", "Actuator du systeme"));
		// managing groups and roles (service desk)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ACCESS", new Localized("System Access", "Access du systeme"));

		Group applicationRoles = permissions.createGroup("APP", new Localized("Application", "Application"));
		// token verification (application background user)
		permissions.createRole(applicationRoles.getGroupId(), "APP_AUTH_REQUEST", new Localized("Authorization Requestor", "Demandeur d'Autorisation"));

		Group crmRoles = permissions.createGroup("CRM", new Localized("Customer Relationship Management"));
		// create and management all organizations
		permissions.createRole(crmRoles.getGroupId(), "CRM_ADMIN", new Localized("CRM Admin", "Administrateur GRC"));
		// user of a single organization
		permissions.createRole(crmRoles.getGroupId(), "CRM_USER", new Localized("CRM Viewer", "Visionneuse GRC"));
		
		Group orgRoles = permissions.createGroup("ORG", new Localized("Organization Management"));
		// create and management all organizations
		permissions.createRole(orgRoles.getGroupId(), "ORG_ADMIN", new Localized("Organization Admin", "Administrateur GRC"));
		permissions.createRole(orgRoles.getGroupId(), "ORG_USER", new Localized("Organization Viewer", "Visionneuse GRC"));
	}
		
	public static User initializeSystemUser(CrmServices crm, CrmPasswordService passwords, String username, String email, PersonName name) {
		Identifier systemOrganizationId = crm.createOrganization("System", List.of("SYS")).getOrganizationId();
		Identifier systemPersonId = crm.createPerson(systemOrganizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
		User user = crm.createUser(systemPersonId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN"));
		passwords.updatePassword(user.getUserId().toString(), username);
		return user;
	}
	
	public static boolean verify(CrmServices crm, String username) {
		// Verify SYSTEM and CRM Role exist
		
		// Verify active user with SYS_* roles exist
		
		return true;
	}
	
}
