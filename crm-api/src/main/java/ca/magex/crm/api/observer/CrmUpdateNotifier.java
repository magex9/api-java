package ca.magex.crm.api.observer;

import java.util.ArrayList;
import java.util.List;

import ca.magex.crm.api.system.Identifier;

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

	@Override
	public CrmUpdateObserver lookupUpdated(Long timestamp, Identifier lookupId) {
		observers.forEach(o -> o.lookupUpdated(timestamp, lookupId));
		return this;
	}

	@Override
	public CrmUpdateObserver optionUpdated(Long timestamp, Identifier optionId) {
		observers.forEach(o -> o.optionUpdated(timestamp, optionId));
		return this;
	}

	@Override
	public CrmUpdateObserver groupUpdated(Long timestamp, Identifier groupId) {
		observers.forEach(o -> o.groupUpdated(timestamp, groupId));
		return this;
	}

	@Override
	public CrmUpdateObserver roleUpdated(Long timestamp, Identifier roleId) {
		observers.forEach(o -> o.roleUpdated(timestamp, roleId));
		return this;
	}

	@Override
	public CrmUpdateObserver organizationUpdated(Long timestamp, Identifier organizationId) {
		observers.forEach(o -> o.organizationUpdated(timestamp, organizationId));
		return this;
	}

	@Override
	public CrmUpdateObserver locationUpdated(Long timestamp, Identifier locationId) {
		observers.forEach(o -> o.locationUpdated(timestamp, locationId));
		return this;
	}

	@Override
	public CrmUpdateObserver personUpdated(Long timestamp, Identifier personId) {
		observers.forEach(o -> o.personUpdated(timestamp, personId));
		return this;
	}

	@Override
	public CrmUpdateObserver userUpdated(Long timestamp, Identifier userId) {
		observers.forEach(o -> o.userUpdated(timestamp, userId));
		return this;
	}

}
