package ca.magex.crm.hazelcast.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PermissionsFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Permission;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPermissionService implements CrmPermissionService {

	public static String HZ_GROUP_KEY = "groups";
	public static String HZ_ROLE_KEY = "roles";

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public Group createGroup(String code, Localized name) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_GROUP_KEY);
		Group group = new Group(				
				new Identifier(Long.toHexString(idGenerator.newId())),
				code,
				Status.ACTIVE,
				name);
		groups.put(group.getGroupId(), group);
		return SerializationUtils.clone(group);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return SerializationUtils.clone(group);
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}		
		if (group.getName().equals(name)) {
			return SerializationUtils.clone(group);
		}
		group = group.withName(name);
		groups.put(group.getGroupId(), group);
		return SerializationUtils.clone(group);
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		if (group.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(group);
		}
		group = group.withStatus(Status.ACTIVE);
		groups.put(group.getGroupId(), group);
		return SerializationUtils.clone(group);
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		if (group.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(group);
		}
		group = group.withStatus(Status.INACTIVE);
		groups.put(group.getGroupId(), group);
		return SerializationUtils.clone(group);
	}
	
	@Override
	public Group findGroupByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Page<Group> findGroups(Paging paging) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		return PageBuilder.buildPageFor(groups.values().stream()
				.sorted(paging.new PagingComparator<Group>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role createRole(Identifier groupId, String code, Localized name) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_ROLE_KEY);
		Role role = new Role(
				new Identifier(Long.toHexString(idGenerator.newId())),
				groupId,
				code,
				Status.ACTIVE,
				name);
		roles.put(role.getRoleId(), role);
		return SerializationUtils.clone(role);
	}
	
	@Override
	public Role findRole(Identifier roleId) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return SerializationUtils.clone(role);
	}
	
	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getName().equals(name)) {
			return SerializationUtils.clone(role);
		}
		role = role.withName(name);				
		roles.put(role.getRoleId(), role);
		return SerializationUtils.clone(role);
	}
	
	@Override
	public Role enableRole(Identifier roleId) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(role);
		}
		role = role.withStatus(Status.ACTIVE);				
		roles.put(role.getRoleId(), role);
		return SerializationUtils.clone(role);
	}

	@Override
	public Role disableRole(Identifier roleId) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(role);
		}
		role = role.withStatus(Status.INACTIVE);				
		roles.put(role.getRoleId(), role);
		return SerializationUtils.clone(role);
	}

	@Override
	public Page<Role> findRoles(Identifier groupId, Paging paging) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		return PageBuilder.buildPageFor(roles.values().stream()
				.filter((r) -> r.getGroupId().equals(groupId))
				.sorted(paging.new PagingComparator<Role>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role findRoleByCode(String code) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		return roles.values().stream().filter((r) -> r.getCode().equals(code)).findFirst().orElseThrow(() -> new ItemNotFoundException("Role Code '" + code + "'"));
	}
}