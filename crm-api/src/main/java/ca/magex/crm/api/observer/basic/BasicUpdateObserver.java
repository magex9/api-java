package ca.magex.crm.api.observer.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.observer.CrmUpdateObserver;
import ca.magex.crm.api.system.Identifier;

public class BasicUpdateObserver implements CrmUpdateObserver {
	
	List<Pair<Identifier, Long>> updates;
	
	public BasicUpdateObserver() {
		updates = new ArrayList<>();
	}

	@Override
	public CrmUpdateObserver lookupUpdated(Long timestamp, Identifier lookupId) {
		updates.add(Pair.of(lookupId, timestamp));
		return this;
	}
	
	@Override
	public CrmUpdateObserver optionUpdated(Long timestamp, Identifier optionId) {
		updates.add(Pair.of(optionId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver groupUpdated(Long timestamp, Identifier groupId) {
		updates.add(Pair.of(groupId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver roleUpdated(Long timestamp, Identifier roleId) {
		updates.add(Pair.of(roleId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver organizationUpdated(Long timestamp, Identifier organizationId) {
		updates.add(Pair.of(organizationId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver locationUpdated(Long timestamp, Identifier locationId) {
		updates.add(Pair.of(locationId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver personUpdated(Long timestamp, Identifier personId) {
		updates.add(Pair.of(personId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver userUpdated(Long timestamp, Identifier userId) {
		updates.add(Pair.of(userId, timestamp));
		return this;
	}

}
