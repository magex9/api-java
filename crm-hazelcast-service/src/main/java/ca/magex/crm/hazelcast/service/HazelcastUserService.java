package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.PersonSummary;
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

@Service
@Primary
@Validated
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserService implements CrmUserService {

	public static String HZ_USER_KEY = "users";

	@Autowired private HazelcastInstance hzInstance;
	@Autowired private PasswordEncoder passwordEncoder;

	// these need to be marked as lazy because spring proxies this class due to the @Validated annotation
	// if these are not lazy then they are autowired before the proxy is created and we get a cyclic dependency
	// so making them lazy allows the proxy to be created before autowiring
	@Autowired @Lazy private CrmPasswordService passwordService;
	@Autowired @Lazy private CrmPersonService personService;
	@Autowired @Lazy private CrmPermissionService permissionService;

	@Override
	public User createUser(
			@NotNull Identifier personId,
			@NotNull String username,
			@NotNull List<String> roles) {
		roles.forEach((role) -> {
			permissionService.findRoleByCode(role); // ensure each role exists
		});
		/* run a find on the personId to ensure it exists */
		PersonSummary person = personService.findPersonSummary(personId);
		/* create our new user */
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
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
	public User findUser(
			@NotNull Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return SerializationUtils.clone(user);
	}

	@Override
	public User findUserByUsername(
			@NotNull String username) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.values().stream()
				.filter(u -> StringUtils.equalsIgnoreCase(u.getUsername(), username))
				.findFirst()
				.orElseThrow(() -> {
					return new ItemNotFoundException("Username '" + username + "'");
				});
		return SerializationUtils.clone(user);
	}

	@Override
	public User updateUserRoles(
			@NotNull Identifier userId,
			@NotNull List<String> roles) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
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
	public User enableUser(
			@NotNull Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
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
	public User disableUser(
			@NotNull Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
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
	public boolean changePassword(
			@NotNull Identifier userId,
			@NotNull String currentPassword,
			@NotNull String newPassword) {
		if (!isValidPasswordFormat(newPassword))
			return false;
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		if (passwordService.verifyPassword(user.getUsername(), currentPassword)) {
			passwordService.updatePassword(user.getUsername(), passwordEncoder.encode(newPassword));
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String resetPassword(
			@NotNull Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("User ID '" + userId + "'");
		}
		return passwordService.generateTemporaryPassword(user.getUsername());
	}

	@Override
	public long countUsers(
			@NotNull UsersFilter filter) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		return users.values()
				.stream()
				.filter(u -> filter.getOrganizationId() != null ? filter.getOrganizationId().equals(u.getPerson().getOrganizationId()) : true)
				.filter(u -> filter.getStatus() != null ? filter.getStatus().equals(u.getStatus()) : true)
				.filter(u -> filter.getPersonId() != null ? filter.getPersonId().equals(u.getPerson().getPersonId()) : true)
				.filter(u -> filter.getRole() != null ? u.getRoles().contains(filter.getRole()) : true)
				.filter(u -> filter.getUsername() != null ? StringUtils.equals(filter.getUsername(), u.getUsername()) : true)
				.count();
	}

	@Override
	public FilteredPage<User> findUsers(
			@NotNull UsersFilter filter,
			@NotNull Paging paging) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		List<User> allMatchingUsers = users.values()
				.stream()
				.filter(u -> filter.getOrganizationId() != null ? filter.getOrganizationId().equals(u.getPerson().getOrganizationId()) : true)
				.filter(u -> filter.getStatus() != null ? filter.getStatus().equals(u.getStatus()) : true)
				.filter(u -> filter.getPersonId() != null ? filter.getPersonId().equals(u.getPerson().getPersonId()) : true)
				.filter(u -> filter.getRole() != null ? u.getRoles().contains(filter.getRole()) : true)
				.filter(u -> filter.getUsername() != null ? StringUtils.equals(filter.getUsername(), u.getUsername()) : true)
				.map(u -> SerializationUtils.clone(u))
				.sorted(filter.getComparator(paging))
				.collect(Collectors.toList());
		return PageBuilder.buildPageFor(filter, allMatchingUsers, paging);
	}
}