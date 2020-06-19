package ca.magex.crm.api.services.basic;

import ca.magex.crm.api.filters.LookupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;
import ca.magex.crm.api.system.Status;

public class BasicLookupService implements CrmLookupService {
	
	private CrmRepositories repos;

	public BasicLookupService(CrmRepositories repos) {
		this.repos = repos;
	}

	@Override
	public Lookup createLookup(Localized name, Option parent) {
		return repos.saveLookup(new Lookup(repos.generateId(), Status.ACTIVE, true, name, parent));
	}

	@Override
	public Lookup findLookup(Identifier lookupId) {
		return repos.findLookup(lookupId);
	}

	@Override
	public Lookup updateLookupName(Identifier lookupId, Localized name) {
		Lookup lookup = repos.findLookup(lookupId);
		if (lookup == null) {
			return null;
		}
		return repos.saveLookup(lookup.withName(name));
	}

	@Override
	public Lookup enableLookup(Identifier lookupId) {
		Lookup lookup = repos.findLookup(lookupId);
		if (lookup == null) {
			return null;
		}
		return repos.saveLookup(lookup.withStatus(Status.ACTIVE));
	}

	@Override
	public Lookup disableLookup(Identifier lookupId) {
		Lookup lookup = repos.findLookup(lookupId);
		if (lookup == null) {
			return null;
		}
		return repos.saveLookup(lookup.withStatus(Status.INACTIVE));
	}

	@Override
	public FilteredPage<Lookup> findLookups(LookupsFilter filter, Paging paging) {
		return findLookups(filter, paging);
	}
	
}