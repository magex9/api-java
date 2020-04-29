package ca.magex.crm.amnesia.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.User;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_CENTRALIZED)
public class AmnesiaUserService implements CrmUserService {

	private AmnesiaDB db;
	
	private PasswordEncoder passwordEncoder;

	public AmnesiaUserService(AmnesiaDB db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public User findUserById(Identifier userId) {
		return db.findUser(userId);
	}

	@Override
	public User findUserByUsername(String userName) {
		return db.findByType(User.class)
				.filter((u) -> StringUtils.equals(u.getUsername(), userName))
				.findFirst()
				.orElseThrow(() -> {
					return new ItemNotFoundException("Unable to find user with userName " + userName);
				});
	}

	@Override
	public User createUser(Identifier personId, String userName, List<String> roles) {
		PersonDetails pd = db.findPerson(personId);
		return db.saveUser(new User(db.generateId(), pd.getOrganizationId(), personId, userName, roles));
	}

	@Override
	public User setUserRoles(Identifier userId, List<String> roles) {
		User user = findUserById(userId);
		user = user.withRoles(roles);
		return db.saveUser(user);
	}

	@Override
	public User setUserPassword(Identifier userId, String password, boolean encoded) {
		User user = findUserById(userId);
		if (encoded) {
			db.setPassword(userId, password);
		}
		else {
			db.setPassword(userId, passwordEncoder.encode(password));
		}
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
