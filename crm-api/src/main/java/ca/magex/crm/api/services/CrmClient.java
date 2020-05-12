package ca.magex.crm.api.services;

public interface CrmClient extends Crm {
	
	boolean login(String username, String password);
	
	boolean logout();

}
