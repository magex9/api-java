package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.policies.CrmOrganizationPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmOrganizationPolicySlf4jDecorator implements CrmOrganizationPolicy {
	
	private CrmOrganizationPolicy delegate;
	
	private Logger logger;
	
	public CrmOrganizationPolicySlf4jDecorator(CrmOrganizationPolicy delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public boolean canCreateOrganization() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreateOrganization()");
				boolean result = delegate.canCreateOrganization();
				logger.trace("Executed canCreateOrganization() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreateOrganization() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreateOrganization()");
				boolean result = delegate.canCreateOrganization();
				logger.debug("Executed canCreateOrganization() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreateOrganization() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreateOrganization()");
			return delegate.canCreateOrganization();
		}
		else {
			return delegate.canCreateOrganization();
		}
	}
	
	@Override
	public boolean canViewOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewOrganization(" + organizationId + ")");
				boolean result = delegate.canViewOrganization(organizationId);
				logger.trace("Executed canViewOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewOrganization(" + organizationId + ")");
				boolean result = delegate.canViewOrganization(organizationId);
				logger.debug("Executed canViewOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewOrganization(" + organizationId + ")");
			return delegate.canViewOrganization(organizationId);
		}
		else {
			return delegate.canViewOrganization(organizationId);
		}
	}
	
	@Override
	public boolean canUpdateOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateOrganization(" + organizationId + ")");
				boolean result = delegate.canUpdateOrganization(organizationId);
				logger.trace("Executed canUpdateOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateOrganization(" + organizationId + ")");
				boolean result = delegate.canUpdateOrganization(organizationId);
				logger.debug("Executed canUpdateOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateOrganization(" + organizationId + ")");
			return delegate.canUpdateOrganization(organizationId);
		}
		else {
			return delegate.canUpdateOrganization(organizationId);
		}
	}
	
	@Override
	public boolean canEnableOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnableOrganization(" + organizationId + ")");
				boolean result = delegate.canEnableOrganization(organizationId);
				logger.trace("Executed canEnableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnableOrganization(" + organizationId + ")");
				boolean result = delegate.canEnableOrganization(organizationId);
				logger.debug("Executed canEnableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnableOrganization(" + organizationId + ")");
			return delegate.canEnableOrganization(organizationId);
		}
		else {
			return delegate.canEnableOrganization(organizationId);
		}
	}
	
	@Override
	public boolean canDisableOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisableOrganization(" + organizationId + ")");
				boolean result = delegate.canDisableOrganization(organizationId);
				logger.trace("Executed canDisableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisableOrganization(" + organizationId + ")");
				boolean result = delegate.canDisableOrganization(organizationId);
				logger.debug("Executed canDisableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisableOrganization(" + organizationId + ")");
			return delegate.canDisableOrganization(organizationId);
		}
		else {
			return delegate.canDisableOrganization(organizationId);
		}
	}
	
}
