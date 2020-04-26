package ca.magex.crm.auth.jwt;

import java.io.Serializable;

public class TokenDetailsRequest implements Serializable {

	private static final long serialVersionUID = 1881172579717887375L;	

	private String token;
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}	
}
