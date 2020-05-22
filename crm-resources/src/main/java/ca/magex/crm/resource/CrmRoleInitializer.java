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
		Group systemRoles = permissions.createGroup(new Localized("SYS", "System", "Systeme"));
		// application shutdown (os user)
		permissions.createRole(systemRoles.getGroupId(), new Localized("SYS_ADMIN", "System Admin", "Administrateur du systeme"));
		// monitoring and services (monitoring tool)
		permissions.createRole(systemRoles.getGroupId(), new Localized("SYS_ACTUATOR", "System Actuator", "Actuator du systeme"));
		// managing groups and roles (service desk)
		permissions.createRole(systemRoles.getGroupId(), new Localized("SYS_ACCESS", "System Access", "Access du systeme"));

		LOG.info("Creating APP Roles");
		Group applicationRoles = permissions.createGroup(new Localized("APP", "Application", "Application"));
		// token verification (application background user)
		permissions.createRole(applicationRoles.getGroupId(), new Localized("APP_AUTH_REQUEST", "Authorization Requestor", "Demandeur d'Autorisation"));

		LOG.info("Creating CRM Roles");
		Group crmRoles = permissions.createGroup(new Localized("CRM", "Customer Relationship Management", "Gestion de la relation client"));
		// create and management all organizations
		permissions.createRole(crmRoles.getGroupId(), new Localized("CRM_ADMIN", "CRM Admin", "Administrateur GRC"));
		// user of a single organization
		permissions.createRole(crmRoles.getGroupId(), new Localized("CRM_USER", "CRM Viewer", "Visionneuse GRC"));
		
		LOG.info("Creating ORG Roles");
		Group orgRoles = permissions.createGroup(new Localized("ORG", "Organization Management", "Gestion de l'organisation"));
		// create and management all organizations
		permissions.createRole(orgRoles.getGroupId(), new Localized("ORG_ADMIN", "Organization Admin", "Administrateur GRC"));
		permissions.createRole(orgRoles.getGroupId(), new Localized("ORG_USER", "Organization Viewer", "Visionneuse GRC"));
	}
	
}
