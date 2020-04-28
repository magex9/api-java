package ca.magex.crm.spring.security.policy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
public class SpringSecurityUserPolicy extends AbstractSpringSecurityPolicy implements CrmUserPolicy {

	@Autowired private CrmUserService userService;
	@Autowired private CrmPersonService personService;

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonSummary person = personService.findPersonSummary(personId);
		if (currentUser.getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* check if the target user belongs to the same organization */
		User user = userService.findUserById(userId);
		return currentUser.getOrganizationId().equals(user.getOrganizationId());
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUserById(userId);
		if (currentUser.getOrganizationId().equals(user.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* current user can update their own password */
		if (currentUser.getUserId().equals(userId)) {
			return true;
		}
		
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUserById(userId);
		if (currentUser.getOrganizationId().equals(user.getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}
}
