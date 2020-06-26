package ca.magex.crm.api.observer;

import ca.magex.crm.api.system.Identifier;

public interface CrmUpdateObserver {

	CrmUpdateObserver optionUpdated(Long timestamp, Identifier optionId);
	
	CrmUpdateObserver organizationUpdated(Long timestamp, Identifier organizationId);
	
	CrmUpdateObserver locationUpdated(Long timestamp, Identifier locationId);

	CrmUpdateObserver personUpdated(Long timestamp, Identifier personId);

	CrmUpdateObserver userUpdated(Long timestamp, Identifier userId);
		
}
