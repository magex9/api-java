package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastUserService implements CrmUserService {
	
	public static String HZ_USER_KEY = "users";
	
	
	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmPasswordService passwordService;
	@Autowired private CrmPersonService personService;
	@Autowired private PasswordEncoder passwordEncoder;

	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		/* run a find on the personId to ensure it exists */
		PersonSummary person = personService.findPersonSummary(personId);		
		/* create our new user */
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_USER_KEY);
		User user = new User(
				new Identifier(Long.toHexString(idGenerator.newId())),
				username,
				person,
				Status.ACTIVE);
		users.put(user.getUserId(), user);
		return SerializationUtils.clone(user);
	}

	@Override
	public User findUser(Identifier userId) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user for id " + userId);
		}
		return SerializationUtils.clone(user);
	}
	
	@Override
	public User findUserByUsername(String username) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
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
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		return SerializationUtils.clone(user);
	}

	@Override
	public User removeUserRole(Identifier userId, String role) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		return SerializationUtils.clone(user);
	}
	
	@Override
	public User setRoles(Identifier userId, List<String> roles) {
		Map<Identifier, User> users = hzInstance.getMap(HZ_USER_KEY);
		User user = users.get(userId);
		if (user == null) {
			throw new ItemNotFoundException("Unable to find user " + userId);
		}
		return SerializationUtils.clone(user);
	}
	
	@Override
	public List<String> getRoles(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean resetPassword(Identifier userId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public User enableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public User disableUser(Identifier userId) {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public long countUsers(UsersFilter filter) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public Page<User> findUsers(UsersFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}		
}
