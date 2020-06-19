package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Status;

public class BasicLookupPolicy implements CrmLookupPolicy {

	private CrmLookupService lookups;
	
	/**
	 * Basic Lookup Policy handles presence and status checks require for policy approval
	 * 
	 * @param organizations
	 */
	public BasicLookupPolicy(CrmLookupService lookups) {
		this.lookups = lookups;
	}


	@Override
	public boolean canCreateLookup() {
		// TODO should be able to create new business units and province lists.
		//if (province or business unit sublist)
		//	return true;
		return false;
	}

	@Override
	public boolean canViewLookup(String lookup) {
		/* can view a lookup if it exists */
		if (lookups.findLookupByCode(lookup) == null) {
			throw new ItemNotFoundException("Lookup Code '" + lookup + "'");
		}
		return true;
	}

	@Override
	public boolean canViewLookup(Identifier lookupId) {
		/* can view a lookup if it exists */
		if (lookups.findLookup(lookupId) == null) {
			throw new ItemNotFoundException("Lookup ID '" + lookupId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateLookup(Identifier lookupId) {
		/* can update a lookup if it exists and is active */
		Lookup lookup = lookups.findLookup(lookupId);
		if (lookup == null) {
			throw new ItemNotFoundException("Lookup ID '" + lookupId + "'");
		}
		return lookup.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableLookup(Identifier lookupId) {
		/* can enable a lookup if it exists */
		if (lookups.findLookup(lookupId) == null) {
			throw new ItemNotFoundException("Lookup ID '" + lookupId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableLookup(Identifier lookupId) {
		/* can disable a lookup if it exists */
		if (lookups.findLookup(lookupId) == null) {
			throw new ItemNotFoundException("Lookup ID '" + lookupId + "'");
		}
		return true;
	}
	
}