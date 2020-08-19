package ca.magex.crm.api.event;

import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * Represents a handler for an Observable Event within the CRM System
 * 
 * @author Jonny
 */
public interface CrmEventObserver {
	
	CrmEventObserver optionUpdated(Long timestamp, OptionIdentifier optionId);
	
	CrmEventObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId);
	
	CrmEventObserver locationUpdated(Long timestamp, LocationIdentifier locationId);

	CrmEventObserver personUpdated(Long timestamp, PersonIdentifier personId);

	CrmEventObserver userUpdated(Long timestamp, UserIdentifier userId);
		
}
