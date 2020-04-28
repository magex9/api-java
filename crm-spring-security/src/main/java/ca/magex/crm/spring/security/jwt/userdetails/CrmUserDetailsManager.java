package ca.magex.crm.spring.security.jwt.userdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmUserService;

/**
 * User Details Manager backed by the Crm Services
 * 
 * @author Jonny
 */
@Component
public class CrmUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

	@Autowired private CrmUserService userService;
	@Autowired private CrmPasswordService passwordService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.findUserByUsername(username);
		return new CrmUserDetails(user, passwordService.getPassword(user.getUserId()));
	}

	@Override
	public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
		User user = userService.findUserByUsername(userDetails.getUsername());
		passwordService.setPassword(user.getUserId(), newPassword);
		return new CrmUserDetails(user, newPassword);
	}

	@Override
	public void createUser(UserDetails user) {
		throw new UnsupportedOperationException("Must be done through CRM User Service");		
	}

	@Override
	public void updateUser(UserDetails user) {
		throw new UnsupportedOperationException("Must be done through CRM User Service");
	}

	@Override
	public void deleteUser(String username) {
		throw new UnsupportedOperationException("Must be done through CRM User Service");
	}

	@Override
	public void changePassword(String oldPassword, String newPassword) {
		throw new UnsupportedOperationException("Must be done through CRM User Service");
	}

	@Override
	public boolean userExists(String username) {
		try {
			userService.findUserByUsername(username);
			return true;
		}
		catch(ItemNotFoundException notFound) {
			return false;
		}
	}
}