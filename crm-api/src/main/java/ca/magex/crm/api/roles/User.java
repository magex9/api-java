package ca.magex.crm.api.roles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class User implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	private Identifier userId;
	
	private String username;

	private PersonSummary person;
	
	private Status status;
	
	private List<String> roles;
	
	public User(Identifier userId, String username, PersonSummary person, Status status, List<String> roles) {
		super();
		this.userId = userId;
		this.username = username;
		this.person = person;
		this.status = status;
		this.roles = new ArrayList<String>(roles);
	}
	
	public Identifier getUserId() {
		return userId;
	}
	
	public String getUsername() {
		return username;
	}

	public PersonSummary getPerson() {
		return person;
	}
	
	public Status getStatus() {
		return status;
	}

	public User withStatus(Status status) {
		return new User(userId, username, person, status, roles);
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
	public User withRoles(List<String> roles) {
		return new User(userId, username, person, status, roles);
	}
	
	public boolean isInRole(String role) {
		return roles.contains(role);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}