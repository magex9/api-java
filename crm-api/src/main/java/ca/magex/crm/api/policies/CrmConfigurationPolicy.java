package ca.magex.crm.api.policies;

public interface CrmConfigurationPolicy {

	boolean canInitialize();
	boolean canReset();

}
