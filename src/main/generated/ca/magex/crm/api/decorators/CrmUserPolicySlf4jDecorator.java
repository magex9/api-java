package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.policies.CrmUserPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmUserPolicySlf4jDecorator implements CrmUserPolicy {
	
	private CrmUserPolicy delegate;
	
	private Logger logger;
	
	public CrmUserPolicySlf4jDecorator(CrmUserPolicy delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public boolean canCreateUserForPerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreateUserForPerson(" + personId + ")");
				boolean result = delegate.canCreateUserForPerson(personId);
				logger.trace("Executed canCreateUserForPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreateUserForPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreateUserForPerson(" + personId + ")");
				boolean result = delegate.canCreateUserForPerson(personId);
				logger.debug("Executed canCreateUserForPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreateUserForPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreateUserForPerson(" + personId + ")");
			return delegate.canCreateUserForPerson(personId);
		}
		else {
			return delegate.canCreateUserForPerson(personId);
		}
	}
	
	@Override
	public boolean canViewUser(String username) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewUser(" + username + ")");
				boolean result = delegate.canViewUser(username);
				logger.trace("Executed canViewUser(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewUser(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewUser(" + username + ")");
				boolean result = delegate.canViewUser(username);
				logger.debug("Executed canViewUser(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewUser(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewUser(" + username + ")");
			return delegate.canViewUser(username);
		}
		else {
			return delegate.canViewUser(username);
		}
	}
	
	@Override
	public boolean canViewUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewUser(" + userId + ")");
				boolean result = delegate.canViewUser(userId);
				logger.trace("Executed canViewUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewUser(" + userId + ")");
				boolean result = delegate.canViewUser(userId);
				logger.debug("Executed canViewUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewUser(" + userId + ")");
			return delegate.canViewUser(userId);
		}
		else {
			return delegate.canViewUser(userId);
		}
	}
	
	@Override
	public boolean canUpdateUserPassword(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateUserPassword(" + userId + ")");
				boolean result = delegate.canUpdateUserPassword(userId);
				logger.trace("Executed canUpdateUserPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateUserPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateUserPassword(" + userId + ")");
				boolean result = delegate.canUpdateUserPassword(userId);
				logger.debug("Executed canUpdateUserPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateUserPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateUserPassword(" + userId + ")");
			return delegate.canUpdateUserPassword(userId);
		}
		else {
			return delegate.canUpdateUserPassword(userId);
		}
	}
	
	@Override
	public boolean canUpdateUserRole(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateUserRole(" + userId + ")");
				boolean result = delegate.canUpdateUserRole(userId);
				logger.trace("Executed canUpdateUserRole(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateUserRole(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateUserRole(" + userId + ")");
				boolean result = delegate.canUpdateUserRole(userId);
				logger.debug("Executed canUpdateUserRole(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateUserRole(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateUserRole(" + userId + ")");
			return delegate.canUpdateUserRole(userId);
		}
		else {
			return delegate.canUpdateUserRole(userId);
		}
	}
	
	@Override
	public boolean canEnableUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnableUser(" + userId + ")");
				boolean result = delegate.canEnableUser(userId);
				logger.trace("Executed canEnableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnableUser(" + userId + ")");
				boolean result = delegate.canEnableUser(userId);
				logger.debug("Executed canEnableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnableUser(" + userId + ")");
			return delegate.canEnableUser(userId);
		}
		else {
			return delegate.canEnableUser(userId);
		}
	}
	
	@Override
	public boolean canDisableUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisableUser(" + userId + ")");
				boolean result = delegate.canDisableUser(userId);
				logger.trace("Executed canDisableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisableUser(" + userId + ")");
				boolean result = delegate.canDisableUser(userId);
				logger.debug("Executed canDisableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisableUser(" + userId + ")");
			return delegate.canDisableUser(userId);
		}
		else {
			return delegate.canDisableUser(userId);
		}
	}
	
}
