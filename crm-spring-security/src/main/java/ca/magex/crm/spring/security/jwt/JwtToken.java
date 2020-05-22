package ca.magex.crm.spring.security.jwt;

import java.io.Serializable;

/**
 * Represents a JWT Token used for transfer between authentication server and application
 * 
 * @author Jonny
 */
public class JwtToken implements Serializable {

	private static final long serialVersionUID = 2587120691667440745L;
	
	private String token;
	
	public JwtToken() {};

	public JwtToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
}
