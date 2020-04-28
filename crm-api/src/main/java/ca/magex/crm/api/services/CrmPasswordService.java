package ca.magex.crm.api.services;

import ca.magex.crm.api.system.Identifier;

public interface CrmPasswordService {

	public void setPassword(Identifier userId, String password);
	
	public String getPassword(Identifier userId);
}
