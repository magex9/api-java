package ca.magex.crm.api.repositories.basic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.repositories.CrmStore;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lookup;
import ca.magex.crm.api.system.Option;

public class BasicStore implements CrmStore {
	
	private Map<Identifier, Serializable> configurations;
	
	private Map<Identifier, Lookup> lookups;
	
	private Map<Identifier, Option> options;
	
	private Map<Identifier, Group> groups;
	
	private Map<Identifier, Role> roles;
	
	private Map<Identifier, OrganizationDetails> organizations;
	
	private Map<Identifier, LocationDetails> locations;
	
	private Map<Identifier, PersonDetails> persons;
	
	private Map<Identifier, User> users;
	
	public BasicStore() {
		configurations = new HashMap<Identifier, Serializable>();
		lookups = new HashMap<Identifier, Lookup>();
		options = new HashMap<Identifier, Option>();
		groups = new HashMap<Identifier, Group>();
		roles = new HashMap<Identifier, Role>();
		organizations = new HashMap<Identifier, OrganizationDetails>();
		locations = new HashMap<Identifier, LocationDetails>();
		persons = new HashMap<Identifier, PersonDetails>();
		users = new HashMap<Identifier, User>();
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
