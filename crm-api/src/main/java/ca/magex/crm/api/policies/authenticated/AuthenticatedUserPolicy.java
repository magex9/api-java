package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.policies.basic.BasicUserPolicy;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_AUTH)
public class AuthenticatedUserPolicy extends BaseAuthenticatedPolicy implements CrmUserPolicy {

	private CrmUserPolicy basicPolicy;
	private CrmUserService userService;
	private CrmPersonService personService;

	public AuthenticatedUserPolicy(
			CrmAuthenticationService authenticationService,
			CrmPersonService personService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.personService = personService;
		this.userService = userService;
		this.basicPolicy = new BasicUserPolicy();
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canCreateUserForPerson(personId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		PersonSummary person = personService.findPersonSummary(personId);
		if (currentUser.getPerson().getOrganizationId().equals(person.getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canCreateUserForPerson(personId);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canViewUser(userId);
		}
		/* check if the target user belongs to the same organization */
		User user = userService.findUser(userId);
		return currentUser.getPerson().getOrganizationId().equals(user.getPerson().getOrganizationId()) && basicPolicy.canViewUser(userId);
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canUpdateUserRole(userId);
		}
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUser(userId);
		if (currentUser.getPerson().getOrganizationId().equals(user.getPerson().getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canUpdateUserRole(userId);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return basicPolicy.canUpdateUserPassword(userId);
		}
		/* current user can update their own password */
		if (currentUser.getUserId().equals(userId)) {
			return basicPolicy.canUpdateUserPassword(userId);
		}
		
		/*
		 * if the person belongs to the organization, then return true if they are an RE
		 * Admin
		 */
		User user = userService.findUser(userId);
		if (currentUser.getPerson().getOrganizationId().equals(user.getPerson().getOrganizationId())) {
			return isReAdmin(currentUser) && basicPolicy.canUpdateUserPassword(userId);
		}
		/* not part of the organization that the personId belongs to */
		return false;
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		return basicPolicy.canEnableUser(userId);
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		return basicPolicy.canDisableUser(userId);
	}
}
