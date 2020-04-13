package ca.magex.crm.spring.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmPersonService;

/**
 * Used to support updating the strength of a user password on the fly if needed
 * 
 * @author Jonny
 *
 */
@Service
public class JwtUserDetailsPasswordService implements UserDetailsPasswordService {

	@Autowired private CrmPasswordService passwordService;
	@Autowired private CrmPersonService personService;

	@Override
	public UserDetails updatePassword(UserDetails user, String newPassword) {
		Page<PersonDetails> personDetails = personService.findPersonDetails(new PersonsFilter(null, null, null, user.getUsername()), Paging.singleInstance());
		if (personDetails.getTotalElements() != 1) {
			throw new InternalAuthenticationServiceException(
					"Unable to locate user with username '" + user.getUsername() + "'");
		}
		/* update the password for the user */
		passwordService.setPassword(personDetails.getContent().get(0).getPersonId(), newPassword);
		return User.withUserDetails(user).password(newPassword).build();
	}
}
