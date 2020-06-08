package ca.magex.crm.amnesia.services;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.amnesia.AmnesiaPasswordEncoder;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.policies.basic.BasicLocationPolicy;
import ca.magex.crm.api.policies.basic.BasicLookupPolicy;
import ca.magex.crm.api.policies.basic.BasicOrganizationPolicy;
import ca.magex.crm.api.policies.basic.BasicPermissionPolicy;
import ca.magex.crm.api.policies.basic.BasicPersonPolicy;
import ca.magex.crm.api.policies.basic.BasicUserPolicy;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.resource.CrmLookupLoader;

public class AmnesiaCrm extends Crm {

	private final AmnesiaDB db;
	
	public AmnesiaCrm() {
		this(new AmnesiaDB(new AmnesiaPasswordEncoder(), new CrmLookupLoader()));
	}
	
	public AmnesiaCrm(AmnesiaDB db) {
		super(db.getInitialization(), 
				db.getLookups(), new BasicLookupPolicy(db.getLookups()), 
				db.getPermissions(), new BasicPermissionPolicy(db.getPermissions()),
				db.getOrganizations(), new BasicOrganizationPolicy(db.getOrganizations()), 
				db.getLocations(), new BasicLocationPolicy(db.getOrganizations(), db.getLocations()), 
				db.getPersons(), new BasicPersonPolicy(db.getOrganizations(), db.getPersons()), 
				db.getUsers(), new BasicUserPolicy(db.getPersons(), db.getUsers()));
		this.db = db;
	}
	
	public AmnesiaCrm initialize() {
		db.initialize("Amnesia", new PersonName("3", "Chris", "P", "Bacon"), "admin@localhost", "system", "admin");
		return this;
	}
	
	public AmnesiaDB db() {
		return db;
	}
	
}
