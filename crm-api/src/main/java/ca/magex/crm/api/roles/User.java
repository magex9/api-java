package ca.magex.crm.api.roles;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Identifier userId;

	private PersonSummary person;
	
	private Status status;
	
	public User(Identifier userId, PersonSummary person, Status status) {
		super();
		this.userId = userId;
		this.person = person;
		this.status = status;
	}
	
	public Identifier getUserId() {
		return userId;
	}

	public PersonSummary getPerson() {
		return person;
	}
	
	public Status getStatus() {
		return status;
	}

	public User withStatus(Status status) {
		return new User(userId, person, status);
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