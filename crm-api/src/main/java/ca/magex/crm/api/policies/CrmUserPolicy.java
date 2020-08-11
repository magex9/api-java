package ca.magex.crm.api.policies;

import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmUserPolicy {
	
	boolean canCreateUserForPerson(PersonIdentifier personId);
	boolean canViewUser(String username);
	boolean canViewUser(UserIdentifier userId);
	boolean canUpdateUserPassword(UserIdentifier userId);
	boolean canUpdateUserRole(UserIdentifier userId);
    boolean canEnableUser(UserIdentifier userId);
    boolean canDisableUser(UserIdentifier userId);  
    
}