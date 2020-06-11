package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.services.CrmPersonService;

import javax.validation.constraints.NotNull;
import ca.magex.crm.api.common.BusinessPosition;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.MailingAddress;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.PersonsFilter;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmPersonServiceSlf4jDecorator implements CrmPersonService {
	
	private CrmPersonService delegate;
	
	private Logger logger;
	
	public CrmPersonServiceSlf4jDecorator(CrmPersonService delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public PersonDetails prototypePerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
				PersonDetails result = delegate.prototypePerson(organizationId, name, address, communication, position);
				logger.trace("Executed prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
				PersonDetails result = delegate.prototypePerson(organizationId, name, address, communication, position);
				logger.debug("Executed prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypePerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
			return delegate.prototypePerson(organizationId, name, address, communication, position);
		}
		else {
			return delegate.prototypePerson(organizationId, name, address, communication, position);
		}
	}
	
	@Override
	public PersonDetails createPerson(PersonDetails prototype) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createPerson(" + prototype + ")");
				PersonDetails result = delegate.createPerson(prototype);
				logger.trace("Executed createPerson(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createPerson(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createPerson(" + prototype + ")");
				PersonDetails result = delegate.createPerson(prototype);
				logger.debug("Executed createPerson(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createPerson(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createPerson(" + prototype + ")");
			return delegate.createPerson(prototype);
		}
		else {
			return delegate.createPerson(prototype);
		}
	}
	
	@Override
	public PersonDetails createPerson(Identifier organizationId, PersonName name, MailingAddress address, Communication communication, BusinessPosition position) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
				PersonDetails result = delegate.createPerson(organizationId, name, address, communication, position);
				logger.trace("Executed createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
				PersonDetails result = delegate.createPerson(organizationId, name, address, communication, position);
				logger.debug("Executed createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createPerson(" + organizationId + ", " + name + ", " + address + ", " + communication + ", " + position + ")");
			return delegate.createPerson(organizationId, name, address, communication, position);
		}
		else {
			return delegate.createPerson(organizationId, name, address, communication, position);
		}
	}
	
	@Override
	public PersonSummary enablePerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enablePerson(" + personId + ")");
				PersonSummary result = delegate.enablePerson(personId);
				logger.trace("Executed enablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enablePerson(" + personId + ")");
				PersonSummary result = delegate.enablePerson(personId);
				logger.debug("Executed enablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enablePerson(" + personId + ")");
			return delegate.enablePerson(personId);
		}
		else {
			return delegate.enablePerson(personId);
		}
	}
	
	@Override
	public PersonSummary disablePerson(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disablePerson(" + personId + ")");
				PersonSummary result = delegate.disablePerson(personId);
				logger.trace("Executed disablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disablePerson(" + personId + ")");
				PersonSummary result = delegate.disablePerson(personId);
				logger.debug("Executed disablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disablePerson(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disablePerson(" + personId + ")");
			return delegate.disablePerson(personId);
		}
		else {
			return delegate.disablePerson(personId);
		}
	}
	
	@Override
	public PersonDetails updatePersonName(Identifier personId, PersonName name) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updatePersonName(" + personId + ", " + name + ")");
				PersonDetails result = delegate.updatePersonName(personId, name);
				logger.trace("Executed updatePersonName(" + personId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updatePersonName(" + personId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updatePersonName(" + personId + ", " + name + ")");
				PersonDetails result = delegate.updatePersonName(personId, name);
				logger.debug("Executed updatePersonName(" + personId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updatePersonName(" + personId + ", " + name + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updatePersonName(" + personId + ", " + name + ")");
			return delegate.updatePersonName(personId, name);
		}
		else {
			return delegate.updatePersonName(personId, name);
		}
	}
	
	@Override
	public PersonDetails updatePersonAddress(Identifier personId, MailingAddress address) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updatePersonAddress(" + personId + ", " + address + ")");
				PersonDetails result = delegate.updatePersonAddress(personId, address);
				logger.trace("Executed updatePersonAddress(" + personId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updatePersonAddress(" + personId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updatePersonAddress(" + personId + ", " + address + ")");
				PersonDetails result = delegate.updatePersonAddress(personId, address);
				logger.debug("Executed updatePersonAddress(" + personId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updatePersonAddress(" + personId + ", " + address + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updatePersonAddress(" + personId + ", " + address + ")");
			return delegate.updatePersonAddress(personId, address);
		}
		else {
			return delegate.updatePersonAddress(personId, address);
		}
	}
	
	@Override
	public PersonDetails updatePersonCommunication(Identifier personId, Communication communication) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updatePersonCommunication(" + personId + ", " + communication + ")");
				PersonDetails result = delegate.updatePersonCommunication(personId, communication);
				logger.trace("Executed updatePersonCommunication(" + personId + ", " + communication + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updatePersonCommunication(" + personId + ", " + communication + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updatePersonCommunication(" + personId + ", " + communication + ")");
				PersonDetails result = delegate.updatePersonCommunication(personId, communication);
				logger.debug("Executed updatePersonCommunication(" + personId + ", " + communication + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updatePersonCommunication(" + personId + ", " + communication + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updatePersonCommunication(" + personId + ", " + communication + ")");
			return delegate.updatePersonCommunication(personId, communication);
		}
		else {
			return delegate.updatePersonCommunication(personId, communication);
		}
	}
	
	@Override
	public PersonDetails updatePersonBusinessPosition(Identifier personId, BusinessPosition position) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updatePersonBusinessPosition(" + personId + ", " + position + ")");
				PersonDetails result = delegate.updatePersonBusinessPosition(personId, position);
				logger.trace("Executed updatePersonBusinessPosition(" + personId + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updatePersonBusinessPosition(" + personId + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updatePersonBusinessPosition(" + personId + ", " + position + ")");
				PersonDetails result = delegate.updatePersonBusinessPosition(personId, position);
				logger.debug("Executed updatePersonBusinessPosition(" + personId + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updatePersonBusinessPosition(" + personId + ", " + position + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updatePersonBusinessPosition(" + personId + ", " + position + ")");
			return delegate.updatePersonBusinessPosition(personId, position);
		}
		else {
			return delegate.updatePersonBusinessPosition(personId, position);
		}
	}
	
	@Override
	public PersonSummary findPersonSummary(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonSummary(" + personId + ")");
				PersonSummary result = delegate.findPersonSummary(personId);
				logger.trace("Executed findPersonSummary(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonSummary(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonSummary(" + personId + ")");
				PersonSummary result = delegate.findPersonSummary(personId);
				logger.debug("Executed findPersonSummary(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonSummary(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonSummary(" + personId + ")");
			return delegate.findPersonSummary(personId);
		}
		else {
			return delegate.findPersonSummary(personId);
		}
	}
	
	@Override
	public PersonDetails findPersonDetails(Identifier personId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonDetails(" + personId + ")");
				PersonDetails result = delegate.findPersonDetails(personId);
				logger.trace("Executed findPersonDetails(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonDetails(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonDetails(" + personId + ")");
				PersonDetails result = delegate.findPersonDetails(personId);
				logger.debug("Executed findPersonDetails(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonDetails(" + personId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonDetails(" + personId + ")");
			return delegate.findPersonDetails(personId);
		}
		else {
			return delegate.findPersonDetails(personId);
		}
	}
	
	@Override
	public long countPersons(PersonsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling countPersons(" + filter + ")");
				long result = delegate.countPersons(filter);
				logger.trace("Executed countPersons(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on countPersons(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling countPersons(" + filter + ")");
				long result = delegate.countPersons(filter);
				logger.debug("Executed countPersons(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on countPersons(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling countPersons(" + filter + ")");
			return delegate.countPersons(filter);
		}
		else {
			return delegate.countPersons(filter);
		}
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonSummaries(" + filter + ", " + paging + ")");
				FilteredPage<PersonSummary> result = delegate.findPersonSummaries(filter, paging);
				logger.trace("Executed findPersonSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonSummaries(" + filter + ", " + paging + ")");
				FilteredPage<PersonSummary> result = delegate.findPersonSummaries(filter, paging);
				logger.debug("Executed findPersonSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonSummaries(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonSummaries(" + filter + ", " + paging + ")");
			return delegate.findPersonSummaries(filter, paging);
		}
		else {
			return delegate.findPersonSummaries(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonDetails(" + filter + ", " + paging + ")");
				FilteredPage<PersonDetails> result = delegate.findPersonDetails(filter, paging);
				logger.trace("Executed findPersonDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonDetails(" + filter + ", " + paging + ")");
				FilteredPage<PersonDetails> result = delegate.findPersonDetails(filter, paging);
				logger.debug("Executed findPersonDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonDetails(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonDetails(" + filter + ", " + paging + ")");
			return delegate.findPersonDetails(filter, paging);
		}
		else {
			return delegate.findPersonDetails(filter, paging);
		}
	}
	
	@Override
	public FilteredPage<PersonDetails> findPersonDetails(PersonsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonDetails(" + filter + ")");
				FilteredPage<PersonDetails> result = delegate.findPersonDetails(filter);
				logger.trace("Executed findPersonDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonDetails(" + filter + ")");
				FilteredPage<PersonDetails> result = delegate.findPersonDetails(filter);
				logger.debug("Executed findPersonDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonDetails(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonDetails(" + filter + ")");
			return delegate.findPersonDetails(filter);
		}
		else {
			return delegate.findPersonDetails(filter);
		}
	}
	
	@Override
	public FilteredPage<PersonSummary> findPersonSummaries(PersonsFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findPersonSummaries(" + filter + ")");
				FilteredPage<PersonSummary> result = delegate.findPersonSummaries(filter);
				logger.trace("Executed findPersonSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findPersonSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findPersonSummaries(" + filter + ")");
				FilteredPage<PersonSummary> result = delegate.findPersonSummaries(filter);
				logger.debug("Executed findPersonSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findPersonSummaries(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findPersonSummaries(" + filter + ")");
			return delegate.findPersonSummaries(filter);
		}
		else {
			return delegate.findPersonSummaries(filter);
		}
	}
	
	@Override
	public FilteredPage<PersonSummary> findActivePersonSummariesForOrg(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findActivePersonSummariesForOrg(" + organizationId + ")");
				FilteredPage<PersonSummary> result = delegate.findActivePersonSummariesForOrg(organizationId);
				logger.trace("Executed findActivePersonSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findActivePersonSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findActivePersonSummariesForOrg(" + organizationId + ")");
				FilteredPage<PersonSummary> result = delegate.findActivePersonSummariesForOrg(organizationId);
				logger.debug("Executed findActivePersonSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findActivePersonSummariesForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findActivePersonSummariesForOrg(" + organizationId + ")");
			return delegate.findActivePersonSummariesForOrg(organizationId);
		}
		else {
			return delegate.findActivePersonSummariesForOrg(organizationId);
		}
	}
	
	@Override
	public PersonsFilter defaultPersonsFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultPersonsFilter()");
				PersonsFilter result = delegate.defaultPersonsFilter();
				logger.trace("Executed defaultPersonsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultPersonsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultPersonsFilter()");
				PersonsFilter result = delegate.defaultPersonsFilter();
				logger.debug("Executed defaultPersonsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultPersonsFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultPersonsFilter()");
			return delegate.defaultPersonsFilter();
		}
		else {
			return delegate.defaultPersonsFilter();
		}
	}
	
}
