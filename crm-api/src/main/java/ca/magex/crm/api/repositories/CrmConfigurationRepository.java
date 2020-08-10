package ca.magex.crm.api.repositories;

public interface CrmConfigurationRepository {
	
	boolean isInitialized();
	
	public boolean prepareInitialize();
	
	public void setInitialized();

}
