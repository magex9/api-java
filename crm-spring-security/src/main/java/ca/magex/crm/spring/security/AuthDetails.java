package ca.magex.crm.spring.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class AuthDetails {

	private String username;
	private List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
	
	public AuthDetails() {		
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public List<GrantedAuthority> getGrantedAuthorities() {
		return grantedAuthorities;
	}
}
