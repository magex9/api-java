package ca.magex.crm.api.observer.basic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ca.magex.crm.api.observer.CrmUpdateObserver;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.id.LocationIdentifier;
import ca.magex.crm.api.system.id.OptionIdentifier;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class BasicUpdateObserver implements CrmUpdateObserver {
	
	List<Pair<Identifier, Long>> updates;
	
	public BasicUpdateObserver() {
		updates = new ArrayList<>();
	}

	@Override
	public CrmUpdateObserver optionUpdated(Long timestamp, OptionIdentifier optionId) {
		updates.add(Pair.of(optionId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver organizationUpdated(Long timestamp, OrganizationIdentifier organizationId) {
		updates.add(Pair.of(organizationId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver locationUpdated(Long timestamp, LocationIdentifier locationId) {
		updates.add(Pair.of(locationId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver personUpdated(Long timestamp, PersonIdentifier personId) {
		updates.add(Pair.of(personId, timestamp));
		return this;
	}

	@Override
	public CrmUpdateObserver userUpdated(Long timestamp, UserIdentifier userId) {
		updates.add(Pair.of(userId, timestamp));
		return this;
	}

}
