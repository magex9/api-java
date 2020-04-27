package ca.magex.crm.spring.security.auth;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a set of authorization details returned from the auth server when
 * verifying a token
 * 
 * @author Jonny
 */
public class AuthDetails implements Serializable {

	private static final long serialVersionUID = -7211992019209078326L;

	private String username;
	private Date expiration;
	private List<String> grantedAuthorities;
	
	public AuthDetails() {}

	public AuthDetails(String username, Date expiration, List<String> grantedAuthorities) {
		this.username = username;
		this.expiration = expiration;
		this.grantedAuthorities = Collections.unmodifiableList(grantedAuthorities);
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

	public List<String> getGrantedAuthorities() {
		return grantedAuthorities;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}
}