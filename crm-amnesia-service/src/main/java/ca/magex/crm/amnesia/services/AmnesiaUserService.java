package ca.magex.crm.amnesia.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service("PrincipalUserService")
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaUserService implements CrmUserService {

	private AmnesiaDB db;
	
	private AmnesiaPasswordService passwords;
	
	public AmnesiaUserService(AmnesiaDB db) {
		this.db = db;
		this.passwords = new AmnesiaPasswordService(db);
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		User user = db.saveUser(new User(db.generateId(), username, db.findPerson(personId), Status.ACTIVE, roles));
		updateUserRoles(user.getUserId(), roles);
		return user;
	}

	@Override
	public User enableUser(Identifier userId) {
		User user = db.findUser(userId);
		if (user == null) {
			return null;
		}
		return db.saveUser(user.withStatus(Status.ACTIVE));
	}

	@Override
	public User disableUser(Identifier userId) {
		User user = db.findUser(userId);
		if (user == null) {
			return null;
		}
		return db.saveUser(user.withStatus(Status.INACTIVE));
	}

	@Override
	public User findUser(Identifier userId) {
		return db.findUser(userId);
	}
	
	@Override
	public User findUserByUsername(String username) {
		try {
			return db.findByType(User.class).filter(u -> u.getUsername().equals(username)).findAny().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}	

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		User user = db.findUser(userId);
		if (user == null) {
			return null;
		}
		roles.forEach((role) -> {
			db.findRoleByCode(role); // ensure role exists
		});
		return db.saveUser(user.withRoles(roles));
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		if (!isValidPasswordFormat(newPassword))
			return false;
		User user = db.findUser(userId);
		if (passwords.verifyPassword(user.getUsername(), currentPassword)) {
			passwords.updatePassword(user.getUsername(), db.getPasswordEncoder().encode(newPassword));
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String resetPassword(Identifier userId) {
		return passwords.generateTemporaryPassword(db.findUser(userId).getUsername());
	}

	@Override
	public long countUsers(UsersFilter filter) {
		return applyFilter(filter).count();
	}

	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		return PageBuilder.buildPageFor(filter, applyFilter(filter)
			.map(i -> SerializationUtils.clone(i))
			.sorted(filter.getComparator(paging))
			.collect(Collectors.toList()), paging);
	}
	
	private Stream<User> applyFilter(UsersFilter filter) {
		return db.findByType(User.class)
			.filter(user -> filter.apply(user));
	}	
}
