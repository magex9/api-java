package ca.magex.crm.hazelcast.service;

import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.PageBuilder;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

@Service
@Primary
@Validated
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPermissionService implements CrmPermissionService {

	public static String HZ_GROUP_KEY = "groups";
	public static String HZ_ROLE_KEY = "roles";

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public Group createGroup(
			@NotNull String code, 
			@NotNull Localized name) {
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
	public Group findGroup(
			@NotNull Identifier groupId) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return SerializationUtils.clone(group);
	}

	@Override
	public Group findGroupByCode(
			@NotNull String code) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		return groups.values().stream().filter((g) -> g.getCode().equals(code)).findFirst().orElseThrow(() -> new ItemNotFoundException("Group Code '" + code + "'"));
	}

	@Override
	public Group updateGroupName(
			@NotNull Identifier groupId, 
			@NotNull Localized name) {
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
	public Group enableGroup(
			@NotNull Identifier groupId) {
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
	public Group disableGroup(
			@NotNull Identifier groupId) {
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
	public FilteredPage<Group> findGroups(
			@NotNull GroupsFilter filter,
			@NotNull Paging paging) {
		Map<Identifier, Group> groups = hzInstance.getMap(HZ_GROUP_KEY);
		return PageBuilder.buildPageFor(filter, groups.values().stream()
				.filter(g -> filter.getCode() == null || filter.getCode().equals(g.getCode()))
				.filter(g -> filter.getEnglishName() == null || filter.getEnglishName().equals(g.getName(Lang.ENGLISH)))
				.filter(g -> filter.getFrenchName() == null || filter.getFrenchName().equals(g.getName(Lang.FRENCH)))
				.filter(g -> filter.getStatus() == null || filter.getStatus().equals(g.getStatus()))
				.sorted(paging.new PagingComparator<Group>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role createRole(
			@NotNull Identifier groupId, 
			@NotNull String code, 
			@NotNull Localized name) {
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
	public Role findRole(
			@NotNull Identifier roleId) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return SerializationUtils.clone(role);
	}
	
	@Override
	public Role updateRoleName(
			@NotNull Identifier roleId, 
			@NotNull Localized name) {
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
	public Role enableRole(
			@NotNull Identifier roleId) {
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
	public Role disableRole(
			@NotNull Identifier roleId) {
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
	public FilteredPage<Role> findRoles(
			@NotNull RolesFilter filter, 
			@NotNull Paging paging) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		return PageBuilder.buildPageFor(filter, roles.values().stream()
				.filter(r -> filter.getGroupId() == null || filter.getGroupId().equals(r.getGroupId()))
				.filter(r -> filter.getCode() == null || filter.getCode().equals(r.getCode()))
				.filter(r -> filter.getEnglishName() == null || filter.getEnglishName().equals(r.getName(Lang.ENGLISH)))
				.filter(r -> filter.getFrenchName() == null || filter.getFrenchName().equals(r.getName(Lang.FRENCH)))
				.filter(r -> filter.getStatus() == null || filter.getStatus().equals(r.getStatus()))
				.sorted(paging.new PagingComparator<Role>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role findRoleByCode(
			@NotNull String code) {
		Map<Identifier, Role> roles = hzInstance.getMap(HZ_ROLE_KEY);
		return roles.values().stream().filter((r) -> r.getCode().equals(code)).findFirst().orElseThrow(() -> new ItemNotFoundException("Role Code '" + code + "'"));
	}
}