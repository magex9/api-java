package ca.magex.crm.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Localized;

public class CrmRoleInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CrmRoleInitializer.class);

	public static void initialize(CrmPermissionService permissions) {
		LOG.info("Creating SYS Roles");
		Group systemRoles = permissions.createGroup("SYS", new Localized("System", "Systeme"));
		// application shutdown (os user)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ADMIN", new Localized("System Admin", "Administrateur du systeme"));
		// monitoring and services (monitoring tool)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ACTUATOR", new Localized("System Actuator", "Actuator du systeme"));
		// managing groups and roles (service desk)
		permissions.createRole(systemRoles.getGroupId(), "SYS_ACCESS", new Localized("System Access", "Access du systeme"));

		LOG.info("Creating APP Roles");
		Group applicationRoles = permissions.createGroup("APP", new Localized("Application", "Application"));
		// token verification (application background user)
		permissions.createRole(applicationRoles.getGroupId(), "APP_AUTH_REQUEST", new Localized("Authorization Requestor", "Demandeur d'Autorisation"));

		LOG.info("Creating CRM Roles");
		Group crmRoles = permissions.createGroup("CRM", new Localized("Customer Relationship Management"));
		// create and management all organizations
		permissions.createRole(crmRoles.getGroupId(), "CRM_ADMIN", new Localized("CRM Admin", "Administrateur GRC"));
		// user of a single organization
		permissions.createRole(crmRoles.getGroupId(), "CRM_USER", new Localized("CRM Viewer", "Visionneuse GRC"));
		
		LOG.info("Creating ORG Roles");
		Group orgRoles = permissions.createGroup("ORG", new Localized("Organization Management"));
		// create and management all organizations
		permissions.createRole(orgRoles.getGroupId(), "ORG_ADMIN", new Localized("Organization Admin", "Administrateur GRC"));
		permissions.createRole(orgRoles.getGroupId(), "ORG_USER", new Localized("Organization Viewer", "Visionneuse GRC"));
	}
	
}
