package ca.magex.crm.slf4j.policies;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.policies.CrmPersonPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmPersonPolicySlf4jDecorator implements CrmPersonPolicy {
	
	private CrmPersonPolicy delegate;
	
	private Logger logger;
	
	public CrmPersonPolicySlf4jDecorator(CrmPersonPolicy delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public boolean canCreatePersonForOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreatePersonForOrganization(" + organizationId + ")");
				boolean result = delegate.canCreatePersonForOrganization(organizationId);
				logger.trace("Executed canCreatePersonForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreatePersonForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreatePersonForOrganization(" + organizationId + ")");
				boolean result = delegate.canCreatePersonForOrganization(organizationId);
				logger.debug("Executed canCreatePersonForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreatePersonForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreatePersonForOrganization(" + organizationId + ")");
			return delegate.canCreatePersonForOrganization(organizationId);
		}
		else {
			return delegate.canCreatePersonForOrganization(organizationId);
		}
	}
	
	@Override
	public boolean canViewPerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewPerson(" + personId + ")");
				boolean result = delegate.canViewPerson(personId);
				logger.trace("Executed canViewPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewPerson(" + personId + ")");
				boolean result = delegate.canViewPerson(personId);
				logger.debug("Executed canViewPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewPerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewPerson(" + personId + ")");
			return delegate.canViewPerson(personId);
		}
		else {
			return delegate.canViewPerson(personId);
		}
	}
	
	@Override
	public boolean canUpdatePerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdatePerson(" + personId + ")");
				boolean result = delegate.canUpdatePerson(personId);
				logger.trace("Executed canUpdatePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdatePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdatePerson(" + personId + ")");
				boolean result = delegate.canUpdatePerson(personId);
				logger.debug("Executed canUpdatePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdatePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdatePerson(" + personId + ")");
			return delegate.canUpdatePerson(personId);
		}
		else {
			return delegate.canUpdatePerson(personId);
		}
	}
	
	@Override
	public boolean canEnablePerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnablePerson(" + personId + ")");
				boolean result = delegate.canEnablePerson(personId);
				logger.trace("Executed canEnablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnablePerson(" + personId + ")");
				boolean result = delegate.canEnablePerson(personId);
				logger.debug("Executed canEnablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnablePerson(" + personId + ")");
			return delegate.canEnablePerson(personId);
		}
		else {
			return delegate.canEnablePerson(personId);
		}
	}
	
	@Override
	public boolean canDisablePerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisablePerson(" + personId + ")");
				boolean result = delegate.canDisablePerson(personId);
				logger.trace("Executed canDisablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisablePerson(" + personId + ")");
				boolean result = delegate.canDisablePerson(personId);
				logger.debug("Executed canDisablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisablePerson(" + personId + ")");
			return delegate.canDisablePerson(personId);
		}
		else {
			return delegate.canDisablePerson(personId);
		}
	}
	
}
