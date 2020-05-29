package ca.magex.crm.api.policies.basic;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Component
@Primary
@Profile(MagexCrmProfiles.CRM_NO_AUTH)
public class BasicUserPolicy implements CrmUserPolicy {

	private CrmUserService userService;
	private CrmPersonService personService;

	/**
	 * Basic User Policy handles presence and status checks require for policy approval
	 * 
	 * @param userService
	 * @param personService
	 */
	public BasicUserPolicy(
			CrmUserService userService,
			CrmPersonService personService) {
		this.userService = userService;
		this.personService = personService;
	}

	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		try {
			/* can create a user for a given person if the person exists */
			personService.findPersonSummary(personId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canViewUser(Identifier userId) {
		try {
			/* can view a user if it exists */
			userService.findUser(userId);
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		try {
			/* can view a user if it exists and is active */
			return userService.findUser(userId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		try {
			/* can view a user password if it exists and is active */
			return userService.findUser(userId).getStatus() == Status.ACTIVE;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canEnableUser(Identifier userId) {
		try {
			/* can enable a user if it exists */
			userService.findUser(userId).getStatus();
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}

	@Override
	public boolean canDisableUser(Identifier userId) {
		try {
			/* can disable a user if it exists */
			userService.findUser(userId).getStatus();
			return true;
		} catch (ItemNotFoundException e) {
			return false;
		}
	}
}