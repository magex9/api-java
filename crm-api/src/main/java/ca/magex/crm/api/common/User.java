package ca.magex.crm.api.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.magex.crm.api.system.Identifier;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Identifier userId;
	
	private Identifier organizationId;
	
	private Identifier personId;
	
	private String userName;

	private List<String> roles;

	public User(Identifier userId, Identifier organizationId, Identifier personId, String username, List<String> roles) {
		super();
		this.userId = userId;
		this.organizationId = organizationId;
		this.personId = personId;
		this.userName = username;
		this.roles = Collections.unmodifiableList(roles);
	}
	
	public Identifier getUserId() {
		return userId;
	}
	
	public Identifier getOrganizationId() {
		return organizationId;
	}
	
	public Identifier getPersonId() {
		return personId;
	}

	public String getUserName() {
		return userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public User withRoles(List<String> roles) {
		return new User(userId, organizationId, personId, userName, roles);
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