package ca.magex.crm.api.services;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.roles.User;

public interface CrmInitializationService {

	boolean isInitialized();
	
	User initializeSystem(String organization, PersonName name, String email, String username, String password);
	
	boolean reset();
	    
}
