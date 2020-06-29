package ca.magex.crm.spring.security.jwt.userdetails;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ca.magex.crm.api.crm.User;
import ca.magex.crm.api.system.Status;

/**
 */
public class CrmUserDetails implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final String password;
	private final User delegate;

	public CrmUserDetails(User user, String password) {
		this.delegate = user;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return delegate.getRoles().stream().map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public String getUsername() {
		return delegate.getUsername();
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
		return delegate.getStatus() == Status.ACTIVE;
	}
}
