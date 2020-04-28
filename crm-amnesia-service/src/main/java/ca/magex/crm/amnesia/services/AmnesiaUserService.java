package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Service
@Primary
public class AmnesiaUserService implements CrmUserService {

	@Autowired private AmnesiaDB db;

	@Override
	public User findUserById(Identifier userId) {
		return db.findUser(userId);
	}

	@Override
	public User findUserByUsername(String username) {
		return db.findByType(User.class)
				.filter((u) -> StringUtils.equals(u.getUsername(), username))
				.findFirst()
				.orElseThrow(() -> {
					return new ItemNotFoundException("Unable to find user with username " + username);
				});
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		PersonDetails pd = db.findPerson(personId);
		return db.saveUser(new User(db.generateId(), pd.getOrganizationId(), personId, username, roles));
	}

	@Override
	public User setUserRoles(Identifier userId, List<String> roles) {
		User user = findUserById(userId);
		user = user.withRoles(roles);
		return db.saveUser(user);
	}

	@Override
	public User setUserPassword(Identifier userId, String password) {
		User user = findUserById(userId);
		db.setPassword(userId, password);
		return user;
	}

	@Override
	public User addUserRole(Identifier userId, String role) {
		User user = findUserById(userId);
		List<String> roles = new ArrayList<String>(user.getRoles());
		roles.add(role);
		return setUserRoles(userId, roles);
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		User user = findUserById(userId);
		List<String> roles = new ArrayList<String>(user.getRoles());
		roles.remove(role);
		return setUserRoles(userId, roles);
	}
}
