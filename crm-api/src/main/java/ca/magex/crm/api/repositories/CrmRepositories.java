package ca.magex.crm.api.repositories;

import java.io.OutputStream;

public interface CrmRepositories extends CrmConfigurationRepository, CrmOptionRepository, CrmOrganizationRepository, CrmLocationRepository, CrmPersonRepository, CrmUserRepository {
	
	public void reset();
	
	public void dump(OutputStream os);
	
}
