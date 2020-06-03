package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.hazelcast.predicate.CrmFilterPredicate;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastUserService implements CrmUserService {

	public static String HZ_USER_KEY = "users";

	private XATransactionAwareHazelcastInstance hzInstance;
	private PasswordEncoder passwordEncoder;
	private CrmPasswordService passwordService;
	private CrmPersonService personService;
	private CrmPermissionService permissionService;

	public HazelcastUserService(
			XATransactionAwareHazelcastInstance hzInstance,
			PasswordEncoder passwordEncoder,
			CrmPasswordService passwordService,
			CrmPersonService personService,
			CrmPermissionService permissionService) {
		this.hzInstance = hzInstance;
		this.passwordEncoder = passwordEncoder;
		this.passwordService = passwordService;
		this.personService = personService;
		this.permissionService = permissionService;
	}

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		roles.forEach((role) -> {
			permissionService.findRoleByCode(role); // ensure each role exists
		});
		/* run a find on the personId to ensure it exists */
		PersonSummary person = personService.findPersonSummary(personId);
		/* create our new user */
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_USER_KEY);
		/* ensure the user name doen't exist */
		if (users.values().stream()
				.filter(u -> StringUtils.equalsIgnoreCase(u.getUsername(), username))
				.count() > 0) {
			throw new DuplicateItemFoundException("Username '" + username + "'");
		}

		User user = new User(
				new Identifier(Long.toHexString(idGenerator.newId())),
				username,
				person,
				Status.ACTIVE,
				roles);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User findUser(Identifier userId) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return SerializationUtils.clone(user);
	}

	@Override
	public User findUserByUsername(String username) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.values().stream()
				.filter(u -> StringUtils.equalsIgnoreCase(u.getUsername(), username))
				.findFirst()
				.orElseThrow(() -> {
					return new ItemNotFoundException("Username '" + username + "'");
				});
		return SerializationUtils.clone(user);
	}

	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		if (user.getRoles().containsAll(roles) && roles.containsAll(user.getRoles())) {
			return SerializationUtils.clone(user);
		}
		user = user.withRoles(roles);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User enableUser(Identifier userId) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		if (user.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(user);
		}
		user = user.withStatus(Status.ACTIVE);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User disableUser(Identifier userId) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		if (user.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(user);
		}
		user = user.withStatus(Status.INACTIVE);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		if (!isValidPasswordFormat(newPassword)) {
			return false;
		}
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		if (passwordService.verifyPassword(user.getUsername(), currentPassword)) {
			passwordService.updatePassword(user.getUsername(), passwordEncoder.encode(newPassword));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String resetPassword(Identifier userId) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return passwordService.generateTemporaryPassword(user.getUsername());
	}

	@Override
	public long countUsers(UsersFilter filter) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		return users.values(new CrmFilterPredicate<User>(filter)).size();
	}

	@Override
	public FilteredPage<User> findUsers(
			UsersFilter filter,
			Paging paging) {
		TransactionalMap<Identifier, User> users = hzInstance.getUsersMap();
		List<User> allMatchingUsers = users.values(new CrmFilterPredicate<User>(filter))
				.stream()
				.map(u -> SerializationUtils.clone(u))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingUsers, paging);
	}

	@Override
	public Page<User> findActiveUserForOrg(Identifier organizationId) {
		return CrmUserService.super.findActiveUserForOrg(organizationId);
	}

	@Override
	public Page<User> findUsers(UsersFilter filter) {
		return CrmUserService.super.findUsers(filter);
	}

	@Override
	public User createUser(User prototype) {
		return CrmUserService.super.createUser(prototype);
	}

	@Override
	public User prototypeUser(@NotNull Identifier personId, @NotNull String username, @NotNull List<String> roles) {
		return CrmUserService.super.prototypeUser(personId, username, roles);
	}
}