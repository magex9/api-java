package ca.magex.crm.api.repositories;

import java.io.OutputStream;

public interface CrmRepositories extends CrmConfigurationRepository, CrmLookupRepository, CrmOptionRepository, CrmOrganizationRepository, CrmLocationRepository, CrmPersonRepository, CrmUserRepository, CrmRoleRepository, CrmGroupRepository {

	public void reset();
	
	public void dump(OutputStream os);
	
}
