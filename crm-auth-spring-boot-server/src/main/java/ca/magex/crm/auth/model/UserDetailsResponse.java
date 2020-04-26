package ca.magex.crm.auth.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsResponse implements Serializable {
	
	private static final long serialVersionUID = 1881172579717887375L;	

	private String username;
	private List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
	
	public UserDetailsResponse(UserDetails userDetails) {
		this.username = userDetails.getUsername();
		this.grantedAuthorities.addAll(userDetails.getAuthorities());
	}

	public String getUsername() {
		return this.username;
	}
	
	public List<GrantedAuthority> getGrantedAuthorities() {
		return grantedAuthorities;
	}
}
