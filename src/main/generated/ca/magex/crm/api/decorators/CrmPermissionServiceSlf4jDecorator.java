package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.services.CrmPermissionService;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import ca.magex.crm.api.filters.GroupsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.RolesFilter;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Localized;
import ca.magex.crm.api.system.Status;

public class CrmPermissionServiceSlf4jDecorator implements CrmPermissionService {
	
	private CrmPermissionService delegate;
	
	private Logger logger;
	
	public CrmPermissionServiceSlf4jDecorator(CrmPermissionService delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public Group prototypeGroup(Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypeGroup(" + name + ")");
				Group result = delegate.prototypeGroup(name);
				logger.trace("Executed prototypeGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypeGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypeGroup(" + name + ")");
				Group result = delegate.prototypeGroup(name);
				logger.debug("Executed prototypeGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypeGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypeGroup(" + name + ")");
			return delegate.prototypeGroup(name);
		}
		else {
			return delegate.prototypeGroup(name);
		}
	}
	
	@Override
	public Group createGroup(Group group) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createGroup(" + group + ")");
				Group result = delegate.createGroup(group);
				logger.trace("Executed createGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createGroup(" + group + ")");
				Group result = delegate.createGroup(group);
				logger.debug("Executed createGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createGroup(" + group + ")");
			return delegate.createGroup(group);
		}
		else {
			return delegate.createGroup(group);
		}
	}
	
	@Override
	public Group createGroup(Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createGroup(" + name + ")");
				Group result = delegate.createGroup(name);
				logger.trace("Executed createGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createGroup(" + name + ")");
				Group result = delegate.createGroup(name);
				logger.debug("Executed createGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createGroup(" + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createGroup(" + name + ")");
			return delegate.createGroup(name);
		}
		else {
			return delegate.createGroup(name);
		}
	}
	
	@Override
	public Group findGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findGroup(" + groupId + ")");
				Group result = delegate.findGroup(groupId);
				logger.trace("Executed findGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findGroup(" + groupId + ")");
				Group result = delegate.findGroup(groupId);
				logger.debug("Executed findGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findGroup(" + groupId + ")");
			return delegate.findGroup(groupId);
		}
		else {
			return delegate.findGroup(groupId);
		}
	}
	
	@Override
	public Group findGroupByCode(String code) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findGroupByCode(" + code + ")");
				Group result = delegate.findGroupByCode(code);
				logger.trace("Executed findGroupByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findGroupByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findGroupByCode(" + code + ")");
				Group result = delegate.findGroupByCode(code);
				logger.debug("Executed findGroupByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findGroupByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findGroupByCode(" + code + ")");
			return delegate.findGroupByCode(code);
		}
		else {
			return delegate.findGroupByCode(code);
		}
	}
	
	@Override
	public Group updateGroupName(Identifier groupId, Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateGroupName(" + groupId + ", " + name + ")");
				Group result = delegate.updateGroupName(groupId, name);
				logger.trace("Executed updateGroupName(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateGroupName(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateGroupName(" + groupId + ", " + name + ")");
				Group result = delegate.updateGroupName(groupId, name);
				logger.debug("Executed updateGroupName(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateGroupName(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateGroupName(" + groupId + ", " + name + ")");
			return delegate.updateGroupName(groupId, name);
		}
		else {
			return delegate.updateGroupName(groupId, name);
		}
	}
	
	@Override
	public Group enableGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enableGroup(" + groupId + ")");
				Group result = delegate.enableGroup(groupId);
				logger.trace("Executed enableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enableGroup(" + groupId + ")");
				Group result = delegate.enableGroup(groupId);
				logger.debug("Executed enableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enableGroup(" + groupId + ")");
			return delegate.enableGroup(groupId);
		}
		else {
			return delegate.enableGroup(groupId);
		}
	}
	
	@Override
	public Group disableGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disableGroup(" + groupId + ")");
				Group result = delegate.disableGroup(groupId);
				logger.trace("Executed disableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disableGroup(" + groupId + ")");
				Group result = delegate.disableGroup(groupId);
				logger.debug("Executed disableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disableGroup(" + groupId + ")");
			return delegate.disableGroup(groupId);
		}
		else {
			return delegate.disableGroup(groupId);
		}
	}
	
	@Override
	public GroupsFilter defaultGroupsFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultGroupsFilter()");
				GroupsFilter result = delegate.defaultGroupsFilter();
				logger.trace("Executed defaultGroupsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultGroupsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultGroupsFilter()");
				GroupsFilter result = delegate.defaultGroupsFilter();
				logger.debug("Executed defaultGroupsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultGroupsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultGroupsFilter()");
			return delegate.defaultGroupsFilter();
		}
		else {
			return delegate.defaultGroupsFilter();
		}
	}
	
	@Override
	public FilteredPage<Group> findGroups(GroupsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findGroups(" + filter + ", " + paging + ")");
				FilteredPage<Group> result = delegate.findGroups(filter, paging);
				logger.trace("Executed findGroups(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findGroups(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findGroups(" + filter + ", " + paging + ")");
				FilteredPage<Group> result = delegate.findGroups(filter, paging);
				logger.debug("Executed findGroups(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findGroups(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findGroups(" + filter + ", " + paging + ")");
			return delegate.findGroups(filter, paging);
		}
		else {
			return delegate.findGroups(filter, paging);
		}
	}
	
	@Override
	public List<String> findActiveGroupCodes() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findActiveGroupCodes()");
				List<String> result = delegate.findActiveGroupCodes();
				logger.trace("Executed findActiveGroupCodes() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findActiveGroupCodes() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findActiveGroupCodes()");
				List<String> result = delegate.findActiveGroupCodes();
				logger.debug("Executed findActiveGroupCodes() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findActiveGroupCodes() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findActiveGroupCodes()");
			return delegate.findActiveGroupCodes();
		}
		else {
			return delegate.findActiveGroupCodes();
		}
	}
	
	@Override
	public Role prototypeRole(Identifier groupId, Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypeRole(" + groupId + ", " + name + ")");
				Role result = delegate.prototypeRole(groupId, name);
				logger.trace("Executed prototypeRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypeRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypeRole(" + groupId + ", " + name + ")");
				Role result = delegate.prototypeRole(groupId, name);
				logger.debug("Executed prototypeRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypeRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypeRole(" + groupId + ", " + name + ")");
			return delegate.prototypeRole(groupId, name);
		}
		else {
			return delegate.prototypeRole(groupId, name);
		}
	}
	
	@Override
	public Role createRole(Role role) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createRole(" + role + ")");
				Role result = delegate.createRole(role);
				logger.trace("Executed createRole(" + role + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createRole(" + role + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createRole(" + role + ")");
				Role result = delegate.createRole(role);
				logger.debug("Executed createRole(" + role + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createRole(" + role + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createRole(" + role + ")");
			return delegate.createRole(role);
		}
		else {
			return delegate.createRole(role);
		}
	}
	
	@Override
	public Role createRole(Identifier groupId, Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createRole(" + groupId + ", " + name + ")");
				Role result = delegate.createRole(groupId, name);
				logger.trace("Executed createRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createRole(" + groupId + ", " + name + ")");
				Role result = delegate.createRole(groupId, name);
				logger.debug("Executed createRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createRole(" + groupId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createRole(" + groupId + ", " + name + ")");
			return delegate.createRole(groupId, name);
		}
		else {
			return delegate.createRole(groupId, name);
		}
	}
	
	@Override
	public Role findRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findRole(" + roleId + ")");
				Role result = delegate.findRole(roleId);
				logger.trace("Executed findRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findRole(" + roleId + ")");
				Role result = delegate.findRole(roleId);
				logger.debug("Executed findRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findRole(" + roleId + ")");
			return delegate.findRole(roleId);
		}
		else {
			return delegate.findRole(roleId);
		}
	}
	
	@Override
	public Role findRoleByCode(String code) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findRoleByCode(" + code + ")");
				Role result = delegate.findRoleByCode(code);
				logger.trace("Executed findRoleByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findRoleByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findRoleByCode(" + code + ")");
				Role result = delegate.findRoleByCode(code);
				logger.debug("Executed findRoleByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findRoleByCode(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findRoleByCode(" + code + ")");
			return delegate.findRoleByCode(code);
		}
		else {
			return delegate.findRoleByCode(code);
		}
	}
	
	@Override
	public Role updateRoleName(Identifier roleId, Localized name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateRoleName(" + roleId + ", " + name + ")");
				Role result = delegate.updateRoleName(roleId, name);
				logger.trace("Executed updateRoleName(" + roleId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateRoleName(" + roleId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateRoleName(" + roleId + ", " + name + ")");
				Role result = delegate.updateRoleName(roleId, name);
				logger.debug("Executed updateRoleName(" + roleId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateRoleName(" + roleId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateRoleName(" + roleId + ", " + name + ")");
			return delegate.updateRoleName(roleId, name);
		}
		else {
			return delegate.updateRoleName(roleId, name);
		}
	}
	
	@Override
	public Role enableRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enableRole(" + roleId + ")");
				Role result = delegate.enableRole(roleId);
				logger.trace("Executed enableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enableRole(" + roleId + ")");
				Role result = delegate.enableRole(roleId);
				logger.debug("Executed enableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enableRole(" + roleId + ")");
			return delegate.enableRole(roleId);
		}
		else {
			return delegate.enableRole(roleId);
		}
	}
	
	@Override
	public Role disableRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disableRole(" + roleId + ")");
				Role result = delegate.disableRole(roleId);
				logger.trace("Executed disableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disableRole(" + roleId + ")");
				Role result = delegate.disableRole(roleId);
				logger.debug("Executed disableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disableRole(" + roleId + ")");
			return delegate.disableRole(roleId);
		}
		else {
			return delegate.disableRole(roleId);
		}
	}
	
	@Override
	public FilteredPage<Role> findRoles(RolesFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findRoles(" + filter + ", " + paging + ")");
				FilteredPage<Role> result = delegate.findRoles(filter, paging);
				logger.trace("Executed findRoles(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findRoles(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findRoles(" + filter + ", " + paging + ")");
				FilteredPage<Role> result = delegate.findRoles(filter, paging);
				logger.debug("Executed findRoles(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findRoles(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findRoles(" + filter + ", " + paging + ")");
			return delegate.findRoles(filter, paging);
		}
		else {
			return delegate.findRoles(filter, paging);
		}
	}
	
	@Override
	public List<Role> findRoles() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findRoles()");
				List<Role> result = delegate.findRoles();
				logger.trace("Executed findRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findRoles()");
				List<Role> result = delegate.findRoles();
				logger.debug("Executed findRoles() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findRoles()");
			return delegate.findRoles();
		}
		else {
			return delegate.findRoles();
		}
	}
	
	@Override
	public List<String> findActiveRoleCodesForGroup(String group) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findActiveRoleCodesForGroup(" + group + ")");
				List<String> result = delegate.findActiveRoleCodesForGroup(group);
				logger.trace("Executed findActiveRoleCodesForGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findActiveRoleCodesForGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findActiveRoleCodesForGroup(" + group + ")");
				List<String> result = delegate.findActiveRoleCodesForGroup(group);
				logger.debug("Executed findActiveRoleCodesForGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findActiveRoleCodesForGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findActiveRoleCodesForGroup(" + group + ")");
			return delegate.findActiveRoleCodesForGroup(group);
		}
		else {
			return delegate.findActiveRoleCodesForGroup(group);
		}
	}
	
	@Override
	public RolesFilter defaultRolesFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultRolesFilter()");
				RolesFilter result = delegate.defaultRolesFilter();
				logger.trace("Executed defaultRolesFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultRolesFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultRolesFilter()");
				RolesFilter result = delegate.defaultRolesFilter();
				logger.debug("Executed defaultRolesFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultRolesFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultRolesFilter()");
			return delegate.defaultRolesFilter();
		}
		else {
			return delegate.defaultRolesFilter();
		}
	}
	
}
