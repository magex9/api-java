package ca.magex.crm.spring.security.jwt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.spring.security.jwt.internal.MutableUser;

/**
 * User Details Manager using a distributed Hazelcast map to allow for multiple
 * nodes to access the user details
 * 
 * @author Jonny
 */
@Component
public class DistributedUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

	final Log logger = LogFactory.getLog(getClass());

	@Value("classpath:hazelcast-auth.xml") private Resource configResource;

	@Autowired private AuthenticationManager authenticationManager;
	@Autowired private PasswordEncoder passwordEncoder;

	private HazelcastInstance hzInstance;

	@PostConstruct
	public void initialize() throws IOException {
		try (InputStream configInputStream = configResource.getInputStream()) {
			hzInstance = Hazelcast.newHazelcastInstance(new XmlConfigBuilder(configInputStream).build());
		}

		this.createUser(new User(
				"admin",
				passwordEncoder.encode("admin"),
				Set.of(new SimpleGrantedAuthority("ROLE_CRM_ADMIN"))));

		this.createUser(new User(
				"sysadmin",
				passwordEncoder.encode("sysadmin"),
				Set.of(
						new SimpleGrantedAuthority("ROLE_CRM_ADMIN"),
						new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))));

		this.createUser(new User(
				"app_crm",
				passwordEncoder.encode("NutritionFactsPer1Can"),
				Set.of(
						new SimpleGrantedAuthority("ROLE_AUTH_REQUEST"))));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (userExists(username)) {
			MutableUser persistedUser = (MutableUser) hzInstance.getMap("userDetails").get(StringUtils.lowerCase(username));
			return new User(
					persistedUser.getUsername(),
					persistedUser.getPassword(),
					persistedUser.isEnabled(),
					persistedUser.isAccountNonExpired(),
					persistedUser.isCredentialsNonExpired(),
					persistedUser.isAccountNonLocked(),
					persistedUser.getAuthorities());
		}
		throw new UsernameNotFoundException("User does not exist '" + username + "'");
	}

	@Override
	public UserDetails updatePassword(UserDetails user, String newPassword) {
		Assert.isTrue(userExists(user.getUsername()), "User does not exists '" + user.getUsername() + "'");
		MutableUser persistedUser = (MutableUser) hzInstance.getMap("userDetails").get(StringUtils.lowerCase(user.getUsername()));
		persistedUser.setPassword(newPassword);
		hzInstance.getMap("userDetails").put(StringUtils.lowerCase(user.getUsername()), persistedUser);
		return new User(
				persistedUser.getUsername(),
				persistedUser.getPassword(),
				persistedUser.isEnabled(),
				persistedUser.isAccountNonExpired(),
				persistedUser.isCredentialsNonExpired(),
				persistedUser.isAccountNonLocked(),
				persistedUser.getAuthorities());
	}

	@Override
	public void createUser(UserDetails user) {
		Assert.isTrue(!userExists(user.getUsername()), "User already exist '" + user.getUsername() + "'");
		hzInstance.getMap("userDetails").put(StringUtils.lowerCase(user.getUsername()), new MutableUser(user));
	}

	@Override
	public void updateUser(UserDetails user) {
		Assert.isTrue(userExists(user.getUsername()), "User does not exist '" + user.getUsername() + "'");
		hzInstance.getMap("userDetails").put(StringUtils.lowerCase(user.getUsername()), new MutableUser(user));
	}

	@Override
	public void deleteUser(String username) {
		Assert.isTrue(userExists(username), "User does not exist '" + username + "'");
		hzInstance.getMap("userDetails").remove(StringUtils.lowerCase(username));
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null) {
			throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
		}

		/* first we need to authenticate using the old password */
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(currentUser.getName(), oldPassword));

		/* old password is valid, so we can proceed with changing the password */
		MutableUser persistedUser = (MutableUser) hzInstance.getMap("userDetails").get(StringUtils.lowerCase(currentUser.getName()));
		persistedUser.setPassword(newPassword);
		hzInstance.getMap("userDetails").put(StringUtils.lowerCase(currentUser.getName()), persistedUser);
	}

	@Override
	public boolean userExists(String username) {
		return hzInstance.getMap("userDetails").keySet().contains(StringUtils.lowerCase(username));
	}
}