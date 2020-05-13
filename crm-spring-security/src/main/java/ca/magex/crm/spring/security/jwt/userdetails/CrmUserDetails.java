package ca.magex.crm.spring.security.jwt.userdetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ca.magex.crm.api.roles.User;

/**
 */
public class CrmUserDetails implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final String password;
	private final User delegate;
	private final List<String> roles;

	public CrmUserDetails(User user, String password, List<String> roles) {
		this.delegate = user;
		this.password = password;
		this.roles = new ArrayList<>(roles);
	}

	public String getPassword() {
		return password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public String getUsername() {
		return delegate.getUserId().toString();
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}
