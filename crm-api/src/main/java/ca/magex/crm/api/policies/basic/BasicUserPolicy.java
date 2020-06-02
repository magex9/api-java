package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicUserPolicy implements CrmUserPolicy {

	private CrmPersonService personService;

	private CrmUserService userService;

	/**
	 * Basic User Policy handles presence and status checks require for policy approval
	 * 
	 * @param userService
	 * @param personService
	 */
	public BasicUserPolicy(CrmPersonService personService, CrmUserService userService) {
		this.userService = userService;
		this.personService = personService;
	}

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		/* can create a user for a given person if the person exists */
		personService.findPersonSummary(personId);
		return true;
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		/* can view a user if it exists */
		userService.findUser(userId);
		return true;
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		/* can view a user if it exists and is active */
		return userService.findUser(userId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		/* can view a user password if it exists and is active */
		return userService.findUser(userId).getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		/* can enable a user if it exists */
		userService.findUser(userId).getStatus();
		return true;
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		/* can disable a user if it exists */
		userService.findUser(userId).getStatus();
		return true;
	}
	
}