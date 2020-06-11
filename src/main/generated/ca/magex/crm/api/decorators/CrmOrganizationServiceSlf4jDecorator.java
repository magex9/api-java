package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.services.CrmOrganizationService;

import java.util.List;
import javax.validation.constraints.NotNull;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmOrganizationServiceSlf4jDecorator implements CrmOrganizationService {
	
	private CrmOrganizationService delegate;
	
	private Logger logger;
	
	public CrmOrganizationServiceSlf4jDecorator(CrmOrganizationService delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public OrganizationDetails prototypeOrganization(String displayName, List<String> groups) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypeOrganization(" + displayName + ", " + groups + ")");
				OrganizationDetails result = delegate.prototypeOrganization(displayName, groups);
				logger.trace("Executed prototypeOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypeOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypeOrganization(" + displayName + ", " + groups + ")");
				OrganizationDetails result = delegate.prototypeOrganization(displayName, groups);
				logger.debug("Executed prototypeOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypeOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypeOrganization(" + displayName + ", " + groups + ")");
			return delegate.prototypeOrganization(displayName, groups);
		}
		else {
			return delegate.prototypeOrganization(displayName, groups);
		}
	}
	
	@Override
	public OrganizationDetails createOrganization(OrganizationDetails prototype) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createOrganization(" + prototype + ")");
				OrganizationDetails result = delegate.createOrganization(prototype);
				logger.trace("Executed createOrganization(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createOrganization(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createOrganization(" + prototype + ")");
				OrganizationDetails result = delegate.createOrganization(prototype);
				logger.debug("Executed createOrganization(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createOrganization(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createOrganization(" + prototype + ")");
			return delegate.createOrganization(prototype);
		}
		else {
			return delegate.createOrganization(prototype);
		}
	}
	
	@Override
	public OrganizationDetails createOrganization(String displayName, List<String> groups) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createOrganization(" + displayName + ", " + groups + ")");
				OrganizationDetails result = delegate.createOrganization(displayName, groups);
				logger.trace("Executed createOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createOrganization(" + displayName + ", " + groups + ")");
				OrganizationDetails result = delegate.createOrganization(displayName, groups);
				logger.debug("Executed createOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createOrganization(" + displayName + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createOrganization(" + displayName + ", " + groups + ")");
			return delegate.createOrganization(displayName, groups);
		}
		else {
			return delegate.createOrganization(displayName, groups);
		}
	}
	
	@Override
	public OrganizationSummary enableOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enableOrganization(" + organizationId + ")");
				OrganizationSummary result = delegate.enableOrganization(organizationId);
				logger.trace("Executed enableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enableOrganization(" + organizationId + ")");
				OrganizationSummary result = delegate.enableOrganization(organizationId);
				logger.debug("Executed enableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enableOrganization(" + organizationId + ")");
			return delegate.enableOrganization(organizationId);
		}
		else {
			return delegate.enableOrganization(organizationId);
		}
	}
	
	@Override
	public OrganizationSummary disableOrganization(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disableOrganization(" + organizationId + ")");
				OrganizationSummary result = delegate.disableOrganization(organizationId);
				logger.trace("Executed disableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disableOrganization(" + organizationId + ")");
				OrganizationSummary result = delegate.disableOrganization(organizationId);
				logger.debug("Executed disableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disableOrganization(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disableOrganization(" + organizationId + ")");
			return delegate.disableOrganization(organizationId);
		}
		else {
			return delegate.disableOrganization(organizationId);
		}
	}
	
	@Override
	public OrganizationDetails updateOrganizationDisplayName(Identifier organizationId, String name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateOrganizationDisplayName(" + organizationId + ", " + name + ")");
				OrganizationDetails result = delegate.updateOrganizationDisplayName(organizationId, name);
				logger.trace("Executed updateOrganizationDisplayName(" + organizationId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateOrganizationDisplayName(" + organizationId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateOrganizationDisplayName(" + organizationId + ", " + name + ")");
				OrganizationDetails result = delegate.updateOrganizationDisplayName(organizationId, name);
				logger.debug("Executed updateOrganizationDisplayName(" + organizationId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateOrganizationDisplayName(" + organizationId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateOrganizationDisplayName(" + organizationId + ", " + name + ")");
			return delegate.updateOrganizationDisplayName(organizationId, name);
		}
		else {
			return delegate.updateOrganizationDisplayName(organizationId, name);
		}
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainLocation(Identifier organizationId, Identifier locationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateOrganizationMainLocation(" + organizationId + ", " + locationId + ")");
				OrganizationDetails result = delegate.updateOrganizationMainLocation(organizationId, locationId);
				logger.trace("Executed updateOrganizationMainLocation(" + organizationId + ", " + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateOrganizationMainLocation(" + organizationId + ", " + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateOrganizationMainLocation(" + organizationId + ", " + locationId + ")");
				OrganizationDetails result = delegate.updateOrganizationMainLocation(organizationId, locationId);
				logger.debug("Executed updateOrganizationMainLocation(" + organizationId + ", " + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateOrganizationMainLocation(" + organizationId + ", " + locationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateOrganizationMainLocation(" + organizationId + ", " + locationId + ")");
			return delegate.updateOrganizationMainLocation(organizationId, locationId);
		}
		else {
			return delegate.updateOrganizationMainLocation(organizationId, locationId);
		}
	}
	
	@Override
	public OrganizationDetails updateOrganizationMainContact(Identifier organizationId, Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateOrganizationMainContact(" + organizationId + ", " + personId + ")");
				OrganizationDetails result = delegate.updateOrganizationMainContact(organizationId, personId);
				logger.trace("Executed updateOrganizationMainContact(" + organizationId + ", " + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateOrganizationMainContact(" + organizationId + ", " + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateOrganizationMainContact(" + organizationId + ", " + personId + ")");
				OrganizationDetails result = delegate.updateOrganizationMainContact(organizationId, personId);
				logger.debug("Executed updateOrganizationMainContact(" + organizationId + ", " + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateOrganizationMainContact(" + organizationId + ", " + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateOrganizationMainContact(" + organizationId + ", " + personId + ")");
			return delegate.updateOrganizationMainContact(organizationId, personId);
		}
		else {
			return delegate.updateOrganizationMainContact(organizationId, personId);
		}
	}
	
	@Override
	public OrganizationDetails updateOrganizationGroups(Identifier organizationId, List<String> groups) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateOrganizationGroups(" + organizationId + ", " + groups + ")");
				OrganizationDetails result = delegate.updateOrganizationGroups(organizationId, groups);
				logger.trace("Executed updateOrganizationGroups(" + organizationId + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateOrganizationGroups(" + organizationId + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateOrganizationGroups(" + organizationId + ", " + groups + ")");
				OrganizationDetails result = delegate.updateOrganizationGroups(organizationId, groups);
				logger.debug("Executed updateOrganizationGroups(" + organizationId + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateOrganizationGroups(" + organizationId + ", " + groups + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateOrganizationGroups(" + organizationId + ", " + groups + ")");
			return delegate.updateOrganizationGroups(organizationId, groups);
		}
		else {
			return delegate.updateOrganizationGroups(organizationId, groups);
		}
	}
	
	@Override
	public OrganizationSummary findOrganizationSummary(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationSummary(" + organizationId + ")");
				OrganizationSummary result = delegate.findOrganizationSummary(organizationId);
				logger.trace("Executed findOrganizationSummary(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationSummary(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationSummary(" + organizationId + ")");
				OrganizationSummary result = delegate.findOrganizationSummary(organizationId);
				logger.debug("Executed findOrganizationSummary(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationSummary(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationSummary(" + organizationId + ")");
			return delegate.findOrganizationSummary(organizationId);
		}
		else {
			return delegate.findOrganizationSummary(organizationId);
		}
	}
	
	@Override
	public OrganizationDetails findOrganizationDetails(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationDetails(" + organizationId + ")");
				OrganizationDetails result = delegate.findOrganizationDetails(organizationId);
				logger.trace("Executed findOrganizationDetails(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationDetails(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationDetails(" + organizationId + ")");
				OrganizationDetails result = delegate.findOrganizationDetails(organizationId);
				logger.debug("Executed findOrganizationDetails(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationDetails(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationDetails(" + organizationId + ")");
			return delegate.findOrganizationDetails(organizationId);
		}
		else {
			return delegate.findOrganizationDetails(organizationId);
		}
	}
	
	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling countOrganizations(" + filter + ")");
				long result = delegate.countOrganizations(filter);
				logger.trace("Executed countOrganizations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on countOrganizations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling countOrganizations(" + filter + ")");
				long result = delegate.countOrganizations(filter);
				logger.debug("Executed countOrganizations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on countOrganizations(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling countOrganizations(" + filter + ")");
			return delegate.countOrganizations(filter);
		}
		else {
			return delegate.countOrganizations(filter);
		}
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationDetails(" + filter + ", " + paging + ")");
				FilteredPage<OrganizationDetails> result = delegate.findOrganizationDetails(filter, paging);
				logger.trace("Executed findOrganizationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationDetails(" + filter + ", " + paging + ")");
				FilteredPage<OrganizationDetails> result = delegate.findOrganizationDetails(filter, paging);
				logger.debug("Executed findOrganizationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationDetails(" + filter + ", " + paging + ")");
			return delegate.findOrganizationDetails(filter, paging);
		}
		else {
			return delegate.findOrganizationDetails(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationSummaries(" + filter + ", " + paging + ")");
				FilteredPage<OrganizationSummary> result = delegate.findOrganizationSummaries(filter, paging);
				logger.trace("Executed findOrganizationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationSummaries(" + filter + ", " + paging + ")");
				FilteredPage<OrganizationSummary> result = delegate.findOrganizationSummaries(filter, paging);
				logger.debug("Executed findOrganizationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationSummaries(" + filter + ", " + paging + ")");
			return delegate.findOrganizationSummaries(filter, paging);
		}
		else {
			return delegate.findOrganizationSummaries(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationDetails(" + filter + ")");
				FilteredPage<OrganizationDetails> result = delegate.findOrganizationDetails(filter);
				logger.trace("Executed findOrganizationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationDetails(" + filter + ")");
				FilteredPage<OrganizationDetails> result = delegate.findOrganizationDetails(filter);
				logger.debug("Executed findOrganizationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationDetails(" + filter + ")");
			return delegate.findOrganizationDetails(filter);
		}
		else {
			return delegate.findOrganizationDetails(filter);
		}
	}
	
	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummaries(OrganizationsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findOrganizationSummaries(" + filter + ")");
				FilteredPage<OrganizationSummary> result = delegate.findOrganizationSummaries(filter);
				logger.trace("Executed findOrganizationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findOrganizationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findOrganizationSummaries(" + filter + ")");
				FilteredPage<OrganizationSummary> result = delegate.findOrganizationSummaries(filter);
				logger.debug("Executed findOrganizationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findOrganizationSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findOrganizationSummaries(" + filter + ")");
			return delegate.findOrganizationSummaries(filter);
		}
		else {
			return delegate.findOrganizationSummaries(filter);
		}
	}
	
	@Override
	public OrganizationsFilter defaultOrganizationsFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultOrganizationsFilter()");
				OrganizationsFilter result = delegate.defaultOrganizationsFilter();
				logger.trace("Executed defaultOrganizationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultOrganizationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultOrganizationsFilter()");
				OrganizationsFilter result = delegate.defaultOrganizationsFilter();
				logger.debug("Executed defaultOrganizationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultOrganizationsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultOrganizationsFilter()");
			return delegate.defaultOrganizationsFilter();
		}
		else {
			return delegate.defaultOrganizationsFilter();
		}
	}
	
}
