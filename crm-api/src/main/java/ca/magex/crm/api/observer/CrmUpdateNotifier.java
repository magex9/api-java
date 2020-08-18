package ca.magex.crm.api.observer;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class CrmUpdateNotifier implements CrmUpdateObserver { 
 	// Publisher<CrmUpdateObserver> {
	
	private List<CrmUpdateObserver> observers;
	
//	@Override
//	public void subscribe(Subscriber<? super CrmUpdateObserver> subscriber) {
//		register(subscriber.);
//	}
	
	public CrmUpdateNotifier() {
		this.observers = new ArrayList<>();
	}

	public void register(CrmUpdateObserver observer) {
		observers.add(observer);
	}

	public void unregister(CrmUpdateObserver observer) {
		observers.remove(observer);
	}
	
	public void clear() {
		observers.clear();
	}

	@Override
	public CrmUpdateObserver optionUpdated(Long timestamp, OptionIdentifier optionId) {
		observers.forEach(o -> o.optionUpdated(timestamp, optionId));
		return this;
	}

	@Override
	public CrmUpdateObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId) {
		observers.forEach(o -> o.organizationUpdated(timestamp, organizationId));
		return this;
	}

	@Override
	public CrmUpdateObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		observers.forEach(o -> o.locationUpdated(timestamp, locationId));
		return this;
	}

	@Override
	public CrmUpdateObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		observers.forEach(o -> o.personUpdated(timestamp, personId));
		return this;
	}

	@Override
	public CrmUpdateObserver userUpdated(Long timestamp, UserIdentifier userId) {
		observers.forEach(o -> o.userUpdated(timestamp, userId));
		return this;
	}

}
