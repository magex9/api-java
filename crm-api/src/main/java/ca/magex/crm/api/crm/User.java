package ca.magex.crm.api.crm;

import java.util.Collections;
import java.util.List;

import ca.magex.crm.api.system.Role;

public class User {

	private String userName;
	
	private Person person;
	
	private List<Role> roles;

	public User(String userName, Person person, List<Role> roles) {
		super();
		this.userName = userName;
		this.person = person;
		this.roles = Collections.unmodifiableList(roles);
	}

	public String getUserName() {
		return userName;
	}

	public Person getPerson() {
		return person;
	}

	public List<Role> getRoles() {
		return roles;
	}
	
}
