package ca.magex.crm.api.observer;

import ca.magex.crm.api.system.Identifier;

public interface CrmDataObserver {

	CrmDataObserver groupUpdate(Long timestamp, String groupCode);
	
	CrmDataObserver roleUpdate(Long timestamp, String roleCode);
	
	CrmDataObserver organizationUpdate(Long timestamp, Identifier organizationId);
	
	CrmDataObserver locationUpdate(Long timestamp, Identifier locationId);

	CrmDataObserver personUpdate(Long timestamp, Identifier personId);
		
}
