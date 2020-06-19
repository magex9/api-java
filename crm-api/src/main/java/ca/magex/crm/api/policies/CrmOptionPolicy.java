package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmOptionPolicy {

	boolean canCreateOption(Identifier lookupId);
    boolean canViewOptions(Identifier lookupId);
    boolean canViewOption(Identifier optionId);
    boolean canUpdateOption(Identifier optionId);
    boolean canEnableOption(Identifier optionId);
    boolean canDisableOption(Identifier optionId);    
    
}
