package ca.magex.crm.hazelcast.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserService implements CrmUserService {
	
	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmPasswordService passwordService;
	@Autowired private CrmPersonService personService;
	@Autowired private PasswordEncoder passwordEncoder;

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		/* run a find on the personId to ensure it exists */
		PersonSummary personSummary = personService.findPersonSummary(personId);		
		/* create our new user */
		Map<Identifier, User> users = hzInstance.getMap("users");
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator("users");
		User user = new User(
				new Identifier(Long.toHexString(idGenerator.newId())),
				personSummary.getOrganizationId(), 
				personSummary.getPersonId(), 
				username, 
				roles);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}
	
	@Override
	public User findUserById(Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap("users");
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		return SerializationUtils.clone(user);
	}
	
	@Override
	public User findUserByUsername(String username) {
		Map<Identifier, User> users = hzInstance.getMap("users");
		User user = users.values().stream()
			.filter(u -> StringUtils.equalsIgnoreCase(u.getUsername(), username))
			.findFirst()
			.orElseThrow(() -> {
				return new ItemNotFoundException("Unable to find user with username " + username);
			});
		return SerializationUtils.clone(user);
	}
	
	@Override
	public User addUserRole(Identifier userId, String role) {
		Map<Identifier, User> users = hzInstance.getMap("users");
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		List<String> roles = new ArrayList<String>(user.getRoles());
		if (roles.contains(role)) {
			return SerializationUtils.clone(user);
		}
		roles.add(role);
		user = user.withRoles(roles);
		users.put(userId, user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		Map<Identifier, User> users = hzInstance.getMap("users");
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		List<String> roles = new ArrayList<String>(user.getRoles());
		if (!roles.contains(role)) {
			return SerializationUtils.clone(user);
		}
		roles.remove(role);
		user = user.withRoles(roles);
		users.put(userId, user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User setUserRoles(Identifier userId, List<String> roles) {
		Map<Identifier, User> users = hzInstance.getMap("users");
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		if (user.getRoles().containsAll(roles) && roles.containsAll(user.getRoles())) {
			return SerializationUtils.clone(user);
		}
		user = user.withRoles(roles);
		users.put(userId, user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User setUserPassword(Identifier userId, String encodedPassword, boolean encoded) {
		/* ensure the user exists first */
		User user = findUserById(userId);
		if (encoded) {
			passwordService.setPassword(userId, encodedPassword);
		}
		else {
			passwordService.setPassword(userId, passwordEncoder.encode(encodedPassword));
		}
		return user;
	}
}
