package ca.magex.crm.spring.security.jwt.userdetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;

/**
 * User Details Manager backed by the Crm Services
 * 
 * @author Jonny
 */
@Component
public class CrmUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {

	@Autowired private CrmPasswordService passwordService;

	@Override
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			UserDetails user = passwordService.findUser(username);
			String encodedPassword = passwordService.getEncodedPassword(username);
			return new CrmUserDetails(user, encodedPassword);
		}
		catch(ItemNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
	}

	@Override
	public org.springframework.security.core.userdetails.UserDetails updatePassword(org.springframework.security.core.userdetails.UserDetails userDetails, String newPassword) {
		UserDetails user = passwordService.findUser(userDetails.getUsername());
		passwordService.updatePassword(user.getUsername(), newPassword);
		return new CrmUserDetails(user, newPassword);
	}

	@Override
	public void createUser(org.springframework.security.core.userdetails.UserDetails user) {
		throw new UnsupportedOperationException("Must be done through CRM User Service");		
	}

	@Override
	public void updateUser(org.springframework.security.core.userdetails.UserDetails user) {
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
			passwordService.findUser(username);
			return true;
		}
		catch(ItemNotFoundException notFound) {
			return false;
		}
	}
}