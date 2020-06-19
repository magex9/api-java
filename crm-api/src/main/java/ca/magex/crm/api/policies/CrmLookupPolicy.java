package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmLookupPolicy {

	boolean canCreateLookup();
    boolean canViewLookup(String lookupCode);
    boolean canViewLookup(Identifier lookupId);
    boolean canUpdateLookup(Identifier lookupId);
    boolean canEnableLookup(Identifier lookupId);
    boolean canDisableLookup(Identifier lookupId);
    
}
