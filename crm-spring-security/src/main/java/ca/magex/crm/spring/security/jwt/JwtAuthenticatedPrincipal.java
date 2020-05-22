package ca.magex.crm.spring.security.jwt;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.AuthenticatedPrincipal;

/**
 * A Simple Jwt Authenticated Principal
 * 
 * @author Jonny
 */
public class JwtAuthenticatedPrincipal implements AuthenticatedPrincipal {

	private String username;
	
	public JwtAuthenticatedPrincipal(String username) {
		this.username = username;
	}
	
	@Override
	public String getName() {
		return username;
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
