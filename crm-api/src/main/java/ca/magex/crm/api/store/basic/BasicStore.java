package ca.magex.crm.api.store.basic;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.store.CrmStore;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;

/**
 * Simple In memory implementation of the Datastore using ConcurrentHashMap
 * 
 * @author Scott
 */
public class BasicStore implements CrmStore {
	
	private CrmUpdateNotifier notifier; 
	
	private Map<Identifier, Serializable> configurations;
	
	private Map<Identifier, Lookup> lookups;
	
	private Map<Identifier, Option> options;
	
	private Map<Identifier, Group> groups;
	
	private Map<Identifier, Role> roles;
	
	private Map<Identifier, OrganizationDetails> organizations;
	
	private Map<Identifier, LocationDetails> locations;
	
	private Map<Identifier, PersonDetails> persons;
	
	private Map<Identifier, User> users;
	
	/**
	 * Creates a new Basic Store with no data associated to it
	 */
	public BasicStore() {
		notifier = new CrmUpdateNotifier();
		configurations = new ConcurrentHashMap<Identifier, Serializable>();
		lookups = new ConcurrentHashMap<Identifier, Lookup>();
		options = new ConcurrentHashMap<Identifier, Option>();
		groups = new ConcurrentHashMap<Identifier, Group>();
		roles = new ConcurrentHashMap<Identifier, Role>();
		organizations = new ConcurrentHashMap<Identifier, OrganizationDetails>();
		locations = new ConcurrentHashMap<Identifier, LocationDetails>();
		persons = new ConcurrentHashMap<Identifier, PersonDetails>();
		users = new ConcurrentHashMap<Identifier, User>();
	}
	
	@Override
	public CrmUpdateNotifier getNotifier() {
		return notifier;
	}
	
	@Override
	public Map<Identifier, Serializable> getConfigurations() {
		return configurations;
	}
	
	@Override
	public Map<Identifier, Lookup> getLookups() {
		return lookups;
	}
	
	@Override
	public Map<Identifier, Option> getOptions() {
		return options;
	}
	
	@Override
	public Map<Identifier, Group> getGroups() {
		return groups;
	}

	@Override
	public Map<Identifier, Role> getRoles() {
		return roles;
	}

	@Override
	public Map<Identifier, OrganizationDetails> getOrganizations() {
		return organizations;
	}

	@Override
	public Map<Identifier, LocationDetails> getLocations() {
		return locations;
	}

	@Override
	public Map<Identifier, PersonDetails> getPersons() {
		return persons;
	}

	@Override
	public Map<Identifier, User> getUsers() {
		return users;
	}
}