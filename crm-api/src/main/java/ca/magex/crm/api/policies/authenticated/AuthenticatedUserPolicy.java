package ca.magex.crm.api.policies.authenticated;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
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

	/**
	 * Authenticated User Policy handles roles and association checks required for policy approval
	 * 
	 * @param authenticationService
	 * @param personService
	 * @param userService
	 */
	public AuthenticatedUserPolicy(
			CrmAuthenticationService authenticationService,
			CrmPersonService personService,
			CrmUserService userService) {
		super(authenticationService, userService);
		this.basicPolicy = new BasicUserPolicy(personService, userService);
		this.personService = personService;
		this.userService = userService;
		
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		if (!basicPolicy.canCreateUserForPerson(personId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated with the organization the person belongs to */
		if (currentUser.getPerson().getOrganizationId().equals(personService.findPersonSummary(personId).getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* current user not associated to the organization of the person */
		return false;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		if (!basicPolicy.canViewUser(userId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		return currentUser.getPerson().getOrganizationId().equals(userService.findUser(userId).getPerson().getOrganizationId());
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		if (!basicPolicy.canUpdateUserRole(userId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (currentUser.getPerson().getOrganizationId().equals(userService.findUser(userId).getPerson().getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		if (!basicPolicy.canUpdateUserPassword(userId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* current user can update their own password */
		if (currentUser.getUserId().equals(userId)) {
			return true;
		}		
		/* ensure the current user is associated to the users organization */
		if (currentUser.getPerson().getOrganizationId().equals(userService.findUser(userId).getPerson().getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		if (!basicPolicy.canEnableUser(userId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (currentUser.getPerson().getOrganizationId().equals(userService.findUser(userId).getPerson().getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		if (!basicPolicy.canDisableUser(userId)) {
			return false;
		}
		User currentUser = getCurrentUser();
		/* if the user is a CRM Admin then return true */
		if (isCrmAdmin(currentUser)) {
			return true;
		}
		/* ensure the current user is associated to the users organization */
		if (currentUser.getPerson().getOrganizationId().equals(userService.findUser(userId).getPerson().getOrganizationId())) {
			return isReAdmin(currentUser);
		}
		/* ensure the current user is associated to the users organization */
		return false;
	}
}
