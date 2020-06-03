package ca.magex.crm.hazelcast.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.exceptions.BadRequestException;
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
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.validation.StructureValidationService;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
@Transactional(propagation = Propagation.REQUIRED, noRollbackFor = {
		ItemNotFoundException.class,
		BadRequestException.class
})
public class HazelcastPermissionService implements CrmPermissionService {

	public static String HZ_GROUP_KEY = "groups";
	public static String HZ_ROLE_KEY = "roles";

	private XATransactionAwareHazelcastInstance hzInstance;
	private StructureValidationService validationService;

	public HazelcastPermissionService(
			XATransactionAwareHazelcastInstance hzInstance,
			@Lazy StructureValidationService validationService) {
		this.hzInstance = hzInstance;
		this.validationService = validationService;
	}

	@Override
	public Group createGroup(Localized name) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_GROUP_KEY);
		Group group = new Group(
				new Identifier(Long.toHexString(idGenerator.newId())),
				Status.ACTIVE,
				name);
		groups.put(group.getGroupId(), validationService.validate(group));
		return SerializationUtils.clone(group);
	}

	@Override
	public Group findGroup(Identifier groupId) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		return SerializationUtils.clone(group);
	}

	@Override
	public Group findGroupByCode(String code) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		return groups.values().stream()
				.filter((g) -> g.getCode().equals(code))
				.map((g) -> SerializationUtils.clone(g))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Group Code '" + code + "'"));
	}

	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		if (group.getName().equals(name)) {
			return SerializationUtils.clone(group);
		}
		group = group.withName(name);
		groups.put(group.getGroupId(), validationService.validate(group));
		return SerializationUtils.clone(group);
	}

	@Override
	public Group enableGroup(Identifier groupId) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		if (group.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(group);
		}
		group = group.withStatus(Status.ACTIVE);
		groups.put(group.getGroupId(), validationService.validate(group));
		return SerializationUtils.clone(group);
	}

	@Override
	public Group disableGroup(Identifier groupId) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		Group group = groups.get(groupId);
		if (group == null) {
			throw new ItemNotFoundException("Group ID '" + groupId + "'");
		}
		if (group.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(group);
		}
		group = group.withStatus(Status.INACTIVE);
		groups.put(group.getGroupId(), validationService.validate(group));
		return SerializationUtils.clone(group);
	}

	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		TransactionalMap<Identifier, Group> groups = hzInstance.getGroupsMap();
		return PageBuilder.buildPageFor(filter, groups.values().stream()
				.filter(g -> filter.apply(g))
				.map((g) -> SerializationUtils.clone(g))
				.sorted(paging.new PagingComparator<Group>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role createRole(Identifier groupId, Localized name) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		FlakeIdGenerator idGenerator = hzInstance.getFlakeIdGenerator(HZ_ROLE_KEY);
		Role role = new Role(
				new Identifier(Long.toHexString(idGenerator.newId())),
				groupId,
				Status.ACTIVE,
				name);
		roles.put(role.getRoleId(), validationService.validate(role));
		return SerializationUtils.clone(role);
	}

	@Override
	public Role findRole(Identifier roleId) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		return SerializationUtils.clone(role);
	}

	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getName().equals(name)) {
			return SerializationUtils.clone(role);
		}
		role = role.withName(name);
		roles.put(role.getRoleId(), validationService.validate(role));
		return SerializationUtils.clone(role);
	}

	@Override
	public Role enableRole(Identifier roleId) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getStatus() == Status.ACTIVE) {
			return SerializationUtils.clone(role);
		}
		role = role.withStatus(Status.ACTIVE);
		roles.put(role.getRoleId(), validationService.validate(role));
		return SerializationUtils.clone(role);
	}

	@Override
	public Role disableRole(Identifier roleId) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		Role role = roles.get(roleId);
		if (role == null) {
			throw new ItemNotFoundException("Role ID '" + roleId + "'");
		}
		if (role.getStatus() == Status.INACTIVE) {
			return SerializationUtils.clone(role);
		}
		role = role.withStatus(Status.INACTIVE);
		roles.put(role.getRoleId(), validationService.validate(role));
		return SerializationUtils.clone(role);
	}

	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		return PageBuilder.buildPageFor(filter, roles.values().stream()
				.filter(r -> filter.apply(r))
				.map(r -> SerializationUtils.clone(r))
				.sorted(paging.new PagingComparator<Role>())
				.collect(Collectors.toList()),
				paging);
	}

	@Override
	public Role findRoleByCode(String code) {
		TransactionalMap<Identifier, Role> roles = hzInstance.getRolesMap();
		return roles.values().stream()
				.filter((r) -> r.getCode().equals(code))
				.map(r -> SerializationUtils.clone(r))
				.findFirst()
				.orElseThrow(() -> new ItemNotFoundException("Role Code '" + code + "'"));
	}

	@Override
	public List<String> findActiveGroupCodes() {
		return CrmPermissionService.super.findActiveGroupCodes();
	}

	@Override
	public List<String> findActiveRoleCodesForGroup(String group) {
		return CrmPermissionService.super.findActiveRoleCodesForGroup(group);
	}

	@Override
	public List<Role> findRoles() {
		return CrmPermissionService.super.findRoles();
	}
}