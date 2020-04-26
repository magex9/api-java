package ca.magex.crm.auth.jwt;

import java.io.Serializable;

public class UserDetailsRequest implements Serializable {
	
	private static final long serialVersionUID = 1881172579717887375L;	

	private String username;

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
