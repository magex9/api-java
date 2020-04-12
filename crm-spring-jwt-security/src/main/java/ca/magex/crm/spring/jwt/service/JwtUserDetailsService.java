package ca.magex.crm.spring.jwt.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmPersonService;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired private CrmPersonService personService;
	@Autowired private CrmPasswordService passwordService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("finding info for user '" + username + "'");
		Page<PersonDetails> page = personService.findPersonDetails(new PersonsFilter(null, null, null, username), Paging.singleInstance());
		if (page.getNumberOfElements() > 0) {			
			PersonDetails person = page.getContent().get(0);
			logger.info("found person " + person);
			return new User(
					person.getUser().getUserName(), 
					passwordService.getPassword(person.getPersonId()),
					person.getUser().getRoles().stream().map((r) -> new SimpleGrantedAuthority("ROLE_" + r)).collect(Collectors.toList()));
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
}