package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmRolePolicy {
    boolean canCreateRole(Identifier groupId);
    boolean canViewRoles();
    boolean canViewRole(String code);
    boolean canViewRole(Identifier roleId);
    boolean canUpdateRole(Identifier roleId);
    boolean canEnableRole(Identifier roleId);
    boolean canDisableRole(Identifier roleId);    
}