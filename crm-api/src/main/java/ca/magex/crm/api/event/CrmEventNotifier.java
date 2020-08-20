package ca.magex.crm.api.event;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

/**
 * A specialized implementation of an Event Observer that is used to chain the call to a set of delegated observers
 * 
 * @author Jonny
 */
public class CrmEventNotifier implements CrmEventObserver { 
	
	private List<CrmEventObserver> observers;
	
	public CrmEventNotifier() {
		this.observers = new ArrayList<>();
	}

	public CrmEventNotifier register(CrmEventObserver observer) {
		observers.add(observer);
		return this;
	}

	public CrmEventNotifier unregister(CrmEventObserver observer) {
		observers.remove(observer);
		return this;
	}
	
	public CrmEventNotifier clear() {
		observers.clear();
		return this;
	}

	@Override
	public CrmEventObserver optionUpdated(Long timestamp, OptionIdentifier optionId) {
		observers.forEach(o -> o.optionUpdated(timestamp, optionId));
		return this;
	}

	@Override
	public CrmEventObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId) {
		observers.forEach(o -> o.organizationUpdated(timestamp, organizationId));
		return this;
	}

	@Override
	public CrmEventObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		observers.forEach(o -> o.locationUpdated(timestamp, locationId));
		return this;
	}

	@Override
	public CrmEventObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		observers.forEach(o -> o.personUpdated(timestamp, personId));
		return this;
	}

	@Override
	public CrmEventObserver userUpdated(Long timestamp, UserIdentifier userId, String username) {
		observers.forEach(o -> o.userUpdated(timestamp, userId, username));
		return this;
	}

}
