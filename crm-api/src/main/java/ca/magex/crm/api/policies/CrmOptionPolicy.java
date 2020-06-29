package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Type;
import ca.magex.crm.api.system.id.OptionIdentifier;

public interface CrmOptionPolicy {

	boolean canCreateOption(Type type);
    boolean canViewOptions(Type type);
    boolean canViewOption(Type type, String optionCode);
    boolean canViewOption(OptionIdentifier optionId);
    boolean canUpdateOption(OptionIdentifier optionId);
    boolean canEnableOption(OptionIdentifier optionId);
    boolean canDisableOption(OptionIdentifier optionId);    
    
}
