package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.id.OptionIdentifier;

public interface CrmOptionPolicy {

	boolean canCreateOption(String typeCode);
    boolean canViewOptions(String typeCode);
    boolean canViewOption(String typeCode, String optionCode);
    boolean canViewOption(OptionIdentifier optionId);
    boolean canUpdateOption(OptionIdentifier optionId);
    boolean canEnableOption(OptionIdentifier optionId);
    boolean canDisableOption(OptionIdentifier optionId);    
    
}
