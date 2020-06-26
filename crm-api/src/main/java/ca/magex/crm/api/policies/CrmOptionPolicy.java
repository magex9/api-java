package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmOptionPolicy {

	boolean canCreateOption(String typeCode);
    boolean canViewOptions(String typeCode);
    boolean canViewOption(String typeCode, String optionCode);
    boolean canViewOption(Identifier optionId);
    boolean canUpdateOption(Identifier optionId);
    boolean canEnableOption(Identifier optionId);
    boolean canDisableOption(Identifier optionId);    
    
}
