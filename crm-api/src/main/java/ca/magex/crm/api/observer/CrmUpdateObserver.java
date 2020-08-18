package ca.magex.crm.api.observer;

import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public interface CrmUpdateObserver {
	
	CrmUpdateObserver optionUpdated(Long timestamp, OptionIdentifier optionId);
	
	CrmUpdateObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId);
	
	CrmUpdateObserver locationUpdated(Long timestamp, LocationIdentifier locationId);

	CrmUpdateObserver personUpdated(Long timestamp, PersonIdentifier personId);

	CrmUpdateObserver userUpdated(Long timestamp, UserIdentifier userId);
		
}
