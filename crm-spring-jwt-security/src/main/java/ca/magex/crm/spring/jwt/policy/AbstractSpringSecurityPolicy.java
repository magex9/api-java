package ca.magex.crm.spring.jwt.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.system.Status;

public class AbstractSpringSecurityPolicy {

	@Autowired CrmPersonService personService;
	
	/**
	 * retrieve the current user from the spring security context
	 * @return
	 */
	protected PersonDetails getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return null;
		}
		Page<PersonDetails> page = personService.findPersonDetails(new PersonsFilter(null, null, Status.ACTIVE, auth.getName()), Paging.singleInstance());
		if (page.getTotalElements() == 1) {
			return page.getContent().get(0);
		}
		else {
			throw new ApiException("Expected 1 person for username " + auth.getName() + ", but found " + page.getTotalElements());			
		}
	}
}
