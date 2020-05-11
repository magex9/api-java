package ca.magex.crm.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Localized;

// TODO Delete file?
public class CrmRoleInitializer {

//	private static final Logger LOG = LoggerFactory.getLogger(CrmRoleInitializer.class);
//
//	public static void initialize(CrmPermissionService permissionService) {
//
//		LOG.info("Creating Base Roles");
//		Group systemRoles = permissionService.createGroup(new Localized("System", "Systeme"));
//		permissionService.createRole(systemRoles.getGroupId(), "SYS_ADMIN", new Localized("System Admin", "Administrateur du systeme"));
//		permissionService.createRole(systemRoles.getGroupId(), "SYS_ACTUATOR", new Localized("System Actuator", "Actuator du systeme"));
//		permissionService.createRole(systemRoles.getGroupId(), "AUTH_REQUEST", new Localized("Authorization Requestor", "Demandeur d'Autorisation"));
//
//		Group umaRoles = permissionService.createGroup(new Localized("Permission Management"));
//		permissionService.createRole(umaRoles.getGroupId(), "UMA_ADMIN", new Localized("User Management Admin", "Administrateur de gestion des utilisateurs"));
//		permissionService.createRole(umaRoles.getGroupId(), "UMA_VIEWER", new Localized("User Management Viewer", "Visionneuse de gestion des utilisateurs"));
//		
//		Group crmRoles = permissionService.createGroup(new Localized("Customer Relationship Management"));
//		permissionService.createRole(crmRoles.getGroupId(), "CRM_ADMIN", new Localized("CRM Admin", "Administrateur GRC"));
//		permissionService.createRole(crmRoles.getGroupId(), "CRM_VIEWER", new Localized("CRM Viewer", "Visionneuse GRC"));
//		
////		Group reRoles = permissionService.createGroup(new Localized("Reporting Entity", "Entités déclarantes"));
////		permissionService.createRole(reRoles.getGroupId(), "RE_ADMIN", new Localized("Reporting Admin", "Administrateur de rapports"));
////		permissionService.createRole(reRoles.getGroupId(), "RE_ASSISTANT", new Localized("Reporting Admin Assistant", "Assistant administrateur de rapports"));
////		permissionService.createRole(reRoles.getGroupId(), "RE_DEO", new Localized("Reporting Data Entry Officer", "Responsable de la saisie des rapports"));
////		permissionService.createRole(reRoles.getGroupId(), "RE_VSO", new Localized("Reporting Verification Officer", "Agent de verification des rapports"));
//	}
	
}
