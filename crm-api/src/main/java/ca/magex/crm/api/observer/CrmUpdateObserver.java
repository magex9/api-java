package ca.magex.crm.api.observer;

import ca.magex.crm.api.system.Identifier;

public interface CrmUpdateObserver {

	CrmUpdateObserver lookupUpdated(Long timestamp, Identifier lookupId);
	
	CrmUpdateObserver optionUpdated(Long timestamp, Identifier optionId);
	
	CrmUpdateObserver groupUpdated(Long timestamp, Identifier groupId);
	
	CrmUpdateObserver roleUpdated(Long timestamp, Identifier roleId);
	
	CrmUpdateObserver organizationUpdated(Long timestamp, Identifier organizationId);
	
	CrmUpdateObserver locationUpdated(Long timestamp, Identifier locationId);

	CrmUpdateObserver personUpdated(Long timestamp, Identifier personId);

	CrmUpdateObserver userUpdated(Long timestamp, Identifier userId);
		
}
