package ca.magex.crm.api.common;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ca.magex.crm.api.system.Role;

public class User {
	
	private String userName;

	private List<Role> roles;

	public User(String userName, List<Role> roles) {
		super();
		this.userName = userName;
		this.roles = Collections.unmodifiableList(roles);
	}

	public String getUserName() {
		return userName;
	}

	public User withUserName(String userName) {
		return new User(userName, roles);
	}

	public List<Role> getRoles() {
		return roles;
	}

	public User withRoles(List<Role> roles) {
		return new User(userName, roles);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

}
