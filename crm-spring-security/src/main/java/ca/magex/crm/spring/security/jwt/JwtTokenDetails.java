package ca.magex.crm.spring.security.jwt;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JwtTokenDetails implements Serializable {

	private static final long serialVersionUID = -2318271589922449335L;
	
	private String token;
	private String username;	
	private Date expiration;
	
	public JwtTokenDetails(String token, String username, Date expiration) {
		this.token = token;
		this.username = username;
		this.expiration = expiration;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Date getExpiration() {
		return expiration;
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