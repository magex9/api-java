package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.Identifier;

public interface CrmUserPolicy {
	
	boolean canCreateUserForPerson(Identifier personId);
	boolean canViewUser(Identifier userId);
	boolean canUpdateUserPassword(Identifier userId);
	boolean canUpdateUserRole(Identifier userId);
    boolean canEnableUser(Identifier userId);
    boolean canDisableUser(Identifier userId);  
    
}