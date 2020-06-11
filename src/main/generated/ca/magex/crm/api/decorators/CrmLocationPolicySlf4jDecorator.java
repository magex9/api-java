package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.policies.CrmLocationPolicy;

import ca.magex.crm.api.system.Identifier;

public class CrmLocationPolicySlf4jDecorator implements CrmLocationPolicy {
	
	private CrmLocationPolicy delegate;
	
	private Logger logger;
	
	public CrmLocationPolicySlf4jDecorator(CrmLocationPolicy delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public boolean canCreateLocationForOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canCreateLocationForOrganization(" + organizationId + ")");
				boolean result = delegate.canCreateLocationForOrganization(organizationId);
				logger.trace("Executed canCreateLocationForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canCreateLocationForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canCreateLocationForOrganization(" + organizationId + ")");
				boolean result = delegate.canCreateLocationForOrganization(organizationId);
				logger.debug("Executed canCreateLocationForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canCreateLocationForOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canCreateLocationForOrganization(" + organizationId + ")");
			return delegate.canCreateLocationForOrganization(organizationId);
		}
		else {
			return delegate.canCreateLocationForOrganization(organizationId);
		}
	}
	
	@Override
	public boolean canViewLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canViewLocation(" + locationId + ")");
				boolean result = delegate.canViewLocation(locationId);
				logger.trace("Executed canViewLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canViewLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canViewLocation(" + locationId + ")");
				boolean result = delegate.canViewLocation(locationId);
				logger.debug("Executed canViewLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canViewLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canViewLocation(" + locationId + ")");
			return delegate.canViewLocation(locationId);
		}
		else {
			return delegate.canViewLocation(locationId);
		}
	}
	
	@Override
	public boolean canUpdateLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canUpdateLocation(" + locationId + ")");
				boolean result = delegate.canUpdateLocation(locationId);
				logger.trace("Executed canUpdateLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canUpdateLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canUpdateLocation(" + locationId + ")");
				boolean result = delegate.canUpdateLocation(locationId);
				logger.debug("Executed canUpdateLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canUpdateLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canUpdateLocation(" + locationId + ")");
			return delegate.canUpdateLocation(locationId);
		}
		else {
			return delegate.canUpdateLocation(locationId);
		}
	}
	
	@Override
	public boolean canEnableLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canEnableLocation(" + locationId + ")");
				boolean result = delegate.canEnableLocation(locationId);
				logger.trace("Executed canEnableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canEnableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canEnableLocation(" + locationId + ")");
				boolean result = delegate.canEnableLocation(locationId);
				logger.debug("Executed canEnableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canEnableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canEnableLocation(" + locationId + ")");
			return delegate.canEnableLocation(locationId);
		}
		else {
			return delegate.canEnableLocation(locationId);
		}
	}
	
	@Override
	public boolean canDisableLocation(Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling canDisableLocation(" + locationId + ")");
				boolean result = delegate.canDisableLocation(locationId);
				logger.trace("Executed canDisableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on canDisableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling canDisableLocation(" + locationId + ")");
				boolean result = delegate.canDisableLocation(locationId);
				logger.debug("Executed canDisableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on canDisableLocation(" + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling canDisableLocation(" + locationId + ")");
			return delegate.canDisableLocation(locationId);
		}
		else {
			return delegate.canDisableLocation(locationId);
		}
	}
	
}
