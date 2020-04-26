package ca.magex.crm.auth.jwt;

import java.io.Serializable;
import java.util.Date;

public class TokenDetailsResponse implements Serializable {

	private static final long serialVersionUID = 1881172579717887375L;	

	private String username;
	private Date expiration;
	
	public TokenDetailsResponse(String username, Date expiration) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Date getExpiration() {
		return expiration;
	}
	
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
}
