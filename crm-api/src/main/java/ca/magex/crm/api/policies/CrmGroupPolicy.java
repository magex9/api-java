package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmGroupPolicy {

    boolean canCreateGroup();
    boolean canViewGroup(String group);
    boolean canViewGroup(Identifier groupId);
    boolean canUpdateGroup(Identifier groupId);
    boolean canEnableGroup(Identifier groupId);
    boolean canDisableGroup(Identifier groupId);

}