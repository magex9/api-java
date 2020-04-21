package ca.magex.crm.api.common;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String userName;

	private List<String> roles;

	public User(String userName, List<String> roles) {
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

	public List<String> getRoles() {
		return roles;
	}

	public User withRoles(List<String> roles) {
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}