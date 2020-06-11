package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.policies.CrmPermissionPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmPermissionPolicySlf4jDecorator implements CrmPermissionPolicy {
	
	private CrmPermissionPolicy delegate;
	
	private Logger logger;
	
	public CrmPermissionPolicySlf4jDecorator(CrmPermissionPolicy delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public boolean canCreateGroup() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreateGroup()");
				boolean result = delegate.canCreateGroup();
				logger.trace("Executed canCreateGroup() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreateGroup() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreateGroup()");
				boolean result = delegate.canCreateGroup();
				logger.debug("Executed canCreateGroup() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreateGroup() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreateGroup()");
			return delegate.canCreateGroup();
		}
		else {
			return delegate.canCreateGroup();
		}
	}
	
	@Override
	public boolean canViewGroup(String group) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewGroup(" + group + ")");
				boolean result = delegate.canViewGroup(group);
				logger.trace("Executed canViewGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewGroup(" + group + ")");
				boolean result = delegate.canViewGroup(group);
				logger.debug("Executed canViewGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewGroup(" + group + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewGroup(" + group + ")");
			return delegate.canViewGroup(group);
		}
		else {
			return delegate.canViewGroup(group);
		}
	}
	
	@Override
	public boolean canViewGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewGroup(" + groupId + ")");
				boolean result = delegate.canViewGroup(groupId);
				logger.trace("Executed canViewGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewGroup(" + groupId + ")");
				boolean result = delegate.canViewGroup(groupId);
				logger.debug("Executed canViewGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewGroup(" + groupId + ")");
			return delegate.canViewGroup(groupId);
		}
		else {
			return delegate.canViewGroup(groupId);
		}
	}
	
	@Override
	public boolean canUpdateGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateGroup(" + groupId + ")");
				boolean result = delegate.canUpdateGroup(groupId);
				logger.trace("Executed canUpdateGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateGroup(" + groupId + ")");
				boolean result = delegate.canUpdateGroup(groupId);
				logger.debug("Executed canUpdateGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateGroup(" + groupId + ")");
			return delegate.canUpdateGroup(groupId);
		}
		else {
			return delegate.canUpdateGroup(groupId);
		}
	}
	
	@Override
	public boolean canEnableGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnableGroup(" + groupId + ")");
				boolean result = delegate.canEnableGroup(groupId);
				logger.trace("Executed canEnableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnableGroup(" + groupId + ")");
				boolean result = delegate.canEnableGroup(groupId);
				logger.debug("Executed canEnableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnableGroup(" + groupId + ")");
			return delegate.canEnableGroup(groupId);
		}
		else {
			return delegate.canEnableGroup(groupId);
		}
	}
	
	@Override
	public boolean canDisableGroup(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisableGroup(" + groupId + ")");
				boolean result = delegate.canDisableGroup(groupId);
				logger.trace("Executed canDisableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisableGroup(" + groupId + ")");
				boolean result = delegate.canDisableGroup(groupId);
				logger.debug("Executed canDisableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisableGroup(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisableGroup(" + groupId + ")");
			return delegate.canDisableGroup(groupId);
		}
		else {
			return delegate.canDisableGroup(groupId);
		}
	}
	
	@Override
	public boolean canCreateRole(Identifier groupId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreateRole(" + groupId + ")");
				boolean result = delegate.canCreateRole(groupId);
				logger.trace("Executed canCreateRole(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreateRole(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreateRole(" + groupId + ")");
				boolean result = delegate.canCreateRole(groupId);
				logger.debug("Executed canCreateRole(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreateRole(" + groupId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreateRole(" + groupId + ")");
			return delegate.canCreateRole(groupId);
		}
		else {
			return delegate.canCreateRole(groupId);
		}
	}
	
	@Override
	public boolean canViewRoles() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewRoles()");
				boolean result = delegate.canViewRoles();
				logger.trace("Executed canViewRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewRoles()");
				boolean result = delegate.canViewRoles();
				logger.debug("Executed canViewRoles() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewRoles() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewRoles()");
			return delegate.canViewRoles();
		}
		else {
			return delegate.canViewRoles();
		}
	}
	
	@Override
	public boolean canViewRole(String code) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewRole(" + code + ")");
				boolean result = delegate.canViewRole(code);
				logger.trace("Executed canViewRole(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewRole(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewRole(" + code + ")");
				boolean result = delegate.canViewRole(code);
				logger.debug("Executed canViewRole(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewRole(" + code + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewRole(" + code + ")");
			return delegate.canViewRole(code);
		}
		else {
			return delegate.canViewRole(code);
		}
	}
	
	@Override
	public boolean canViewRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewRole(" + roleId + ")");
				boolean result = delegate.canViewRole(roleId);
				logger.trace("Executed canViewRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewRole(" + roleId + ")");
				boolean result = delegate.canViewRole(roleId);
				logger.debug("Executed canViewRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewRole(" + roleId + ")");
			return delegate.canViewRole(roleId);
		}
		else {
			return delegate.canViewRole(roleId);
		}
	}
	
	@Override
	public boolean canUpdateRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateRole(" + roleId + ")");
				boolean result = delegate.canUpdateRole(roleId);
				logger.trace("Executed canUpdateRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateRole(" + roleId + ")");
				boolean result = delegate.canUpdateRole(roleId);
				logger.debug("Executed canUpdateRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateRole(" + roleId + ")");
			return delegate.canUpdateRole(roleId);
		}
		else {
			return delegate.canUpdateRole(roleId);
		}
	}
	
	@Override
	public boolean canEnableRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnableRole(" + roleId + ")");
				boolean result = delegate.canEnableRole(roleId);
				logger.trace("Executed canEnableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnableRole(" + roleId + ")");
				boolean result = delegate.canEnableRole(roleId);
				logger.debug("Executed canEnableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnableRole(" + roleId + ")");
			return delegate.canEnableRole(roleId);
		}
		else {
			return delegate.canEnableRole(roleId);
		}
	}
	
	@Override
	public boolean canDisableRole(Identifier roleId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisableRole(" + roleId + ")");
				boolean result = delegate.canDisableRole(roleId);
				logger.trace("Executed canDisableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisableRole(" + roleId + ")");
				boolean result = delegate.canDisableRole(roleId);
				logger.debug("Executed canDisableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisableRole(" + roleId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisableRole(" + roleId + ")");
			return delegate.canDisableRole(roleId);
		}
		else {
			return delegate.canDisableRole(roleId);
		}
	}
	
}
