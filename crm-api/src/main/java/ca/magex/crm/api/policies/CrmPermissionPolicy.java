package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmPermissionPolicy {

    boolean canCreateGroup();
    boolean canViewGroup(String group);
    boolean canViewGroup(Identifier groupId);
    boolean canUpdateGroup(Identifier groupId);
    boolean canEnableGroup(Identifier groupId);
    boolean canDisableGroup(Identifier groupId);

    boolean canCreateRole(Identifier groupId);
    boolean canViewRoles();
    boolean canViewRole(String code);
    boolean canViewRole(Identifier roleId);
    boolean canUpdateRole(Identifier roleId);
    boolean canEnableRole(Identifier roleId);
    boolean canDisableRole(Identifier roleId);    
}