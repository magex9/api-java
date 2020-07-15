package ca.magex.crm.api.policies.basic;

import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.crm.UserDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.policies.CrmUserPolicy;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.PersonIdentifier;
import ca.magex.crm.api.system.id.UserIdentifier;

public class BasicUserPolicy implements CrmUserPolicy {

	private CrmPersonService persons;

	private CrmUserService users;

	/**
	 * Basic User Policy handles presence and status checks require for policy approval
	 * 
	 * @param users
	 * @param persons
	 */
	public BasicUserPolicy(CrmPersonService persons, CrmUserService users) {
		this.users = users;
		this.persons = persons;
	}

	@Override
	public boolean canCreateUserForPerson(PersonIdentifier personId) {
		/* can create a user for a given person if the person exists */
		PersonSummary summary = persons.findPersonSummary(personId);
		if (summary == null) {
			throw new ItemNotFoundException("Person ID '" + personId + "'");
		}		
		return true;
	}
	
	@Override
	public boolean canViewUser(String username) {
		try {
			/* can view a group if it exists */
			return users.findUserByUsername(username)!= null;
		} catch (ItemNotFoundException e) {
			throw new ItemNotFoundException("Username '" + username + "'");
		}
	}

	@Override
	public boolean canViewUser(UserIdentifier userId) {
		/* can view a user if it exists */
		if (users.findUserDetails(userId) == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return true;
	}

	@Override
	public boolean canUpdateUserRole(UserIdentifier userId) {
		/* can view a user if it exists and is active */
		UserDetails user = users.findUserDetails(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return user.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canUpdateUserPassword(UserIdentifier userId) {
		/* can view a user password if it exists and is active */
		UserDetails user = users.findUserDetails(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return user.getStatus() == Status.ACTIVE;
	}

	@Override
	public boolean canEnableUser(UserIdentifier userId) {
		/* can enable a user if it exists */
		if (users.findUserDetails(userId) == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return true;
	}

	@Override
	public boolean canDisableUser(UserIdentifier userId) {
		/* can disable a user if it exists */
		if (users.findUserDetails(userId) == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return true;
	}
}