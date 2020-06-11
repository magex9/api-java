package ca.magex.crm.api.decorators;

import org.slf4j.Logger;
import java.time.Duration;

import ca.magex.crm.api.services.CrmUserService;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import ca.magex.crm.api.crm.PersonSummary;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.filters.UsersFilter;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class CrmUserServiceSlf4jDecorator implements CrmUserService {
	
	private CrmUserService delegate;
	
	private Logger logger;
	
	public CrmUserServiceSlf4jDecorator(CrmUserService delegate, Logger logger) {
		this.delegate = delegate;
		this.logger = logger;
	}
	
	@Override
	public User prototypeUser(Identifier personId, String username, List<String> roles) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling prototypeUser(" + personId + ", " + username + ", " + roles + ")");
				User result = delegate.prototypeUser(personId, username, roles);
				logger.trace("Executed prototypeUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on prototypeUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling prototypeUser(" + personId + ", " + username + ", " + roles + ")");
				User result = delegate.prototypeUser(personId, username, roles);
				logger.debug("Executed prototypeUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on prototypeUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling prototypeUser(" + personId + ", " + username + ", " + roles + ")");
			return delegate.prototypeUser(personId, username, roles);
		}
		else {
			return delegate.prototypeUser(personId, username, roles);
		}
	}
	
	@Override
	public User createUser(User prototype) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createUser(" + prototype + ")");
				User result = delegate.createUser(prototype);
				logger.trace("Executed createUser(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createUser(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createUser(" + prototype + ")");
				User result = delegate.createUser(prototype);
				logger.debug("Executed createUser(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createUser(" + prototype + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createUser(" + prototype + ")");
			return delegate.createUser(prototype);
		}
		else {
			return delegate.createUser(prototype);
		}
	}
	
	@Override
	public User createUser(Identifier personId, String username, List<String> roles) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling createUser(" + personId + ", " + username + ", " + roles + ")");
				User result = delegate.createUser(personId, username, roles);
				logger.trace("Executed createUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on createUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling createUser(" + personId + ", " + username + ", " + roles + ")");
				User result = delegate.createUser(personId, username, roles);
				logger.debug("Executed createUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on createUser(" + personId + ", " + username + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling createUser(" + personId + ", " + username + ", " + roles + ")");
			return delegate.createUser(personId, username, roles);
		}
		else {
			return delegate.createUser(personId, username, roles);
		}
	}
	
	@Override
	public User enableUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling enableUser(" + userId + ")");
				User result = delegate.enableUser(userId);
				logger.trace("Executed enableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on enableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling enableUser(" + userId + ")");
				User result = delegate.enableUser(userId);
				logger.debug("Executed enableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on enableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling enableUser(" + userId + ")");
			return delegate.enableUser(userId);
		}
		else {
			return delegate.enableUser(userId);
		}
	}
	
	@Override
	public User disableUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling disableUser(" + userId + ")");
				User result = delegate.disableUser(userId);
				logger.trace("Executed disableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on disableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling disableUser(" + userId + ")");
				User result = delegate.disableUser(userId);
				logger.debug("Executed disableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on disableUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling disableUser(" + userId + ")");
			return delegate.disableUser(userId);
		}
		else {
			return delegate.disableUser(userId);
		}
	}
	
	@Override
	public User updateUserRoles(Identifier userId, List<String> roles) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling updateUserRoles(" + userId + ", " + roles + ")");
				User result = delegate.updateUserRoles(userId, roles);
				logger.trace("Executed updateUserRoles(" + userId + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on updateUserRoles(" + userId + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling updateUserRoles(" + userId + ", " + roles + ")");
				User result = delegate.updateUserRoles(userId, roles);
				logger.debug("Executed updateUserRoles(" + userId + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on updateUserRoles(" + userId + ", " + roles + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling updateUserRoles(" + userId + ", " + roles + ")");
			return delegate.updateUserRoles(userId, roles);
		}
		else {
			return delegate.updateUserRoles(userId, roles);
		}
	}
	
	@Override
	public boolean changePassword(Identifier userId, String currentPassword, String newPassword) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ")");
				boolean result = delegate.changePassword(userId, currentPassword, newPassword);
				logger.trace("Executed changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ")");
				boolean result = delegate.changePassword(userId, currentPassword, newPassword);
				logger.debug("Executed changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling changePassword(" + userId + ", " + currentPassword + ", " + newPassword + ")");
			return delegate.changePassword(userId, currentPassword, newPassword);
		}
		else {
			return delegate.changePassword(userId, currentPassword, newPassword);
		}
	}
	
	@Override
	public String resetPassword(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling resetPassword(" + userId + ")");
				String result = delegate.resetPassword(userId);
				logger.trace("Executed resetPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on resetPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling resetPassword(" + userId + ")");
				String result = delegate.resetPassword(userId);
				logger.debug("Executed resetPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on resetPassword(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling resetPassword(" + userId + ")");
			return delegate.resetPassword(userId);
		}
		else {
			return delegate.resetPassword(userId);
		}
	}
	
	@Override
	public User findUser(Identifier userId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findUser(" + userId + ")");
				User result = delegate.findUser(userId);
				logger.trace("Executed findUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findUser(" + userId + ")");
				User result = delegate.findUser(userId);
				logger.debug("Executed findUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findUser(" + userId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findUser(" + userId + ")");
			return delegate.findUser(userId);
		}
		else {
			return delegate.findUser(userId);
		}
	}
	
	@Override
	public User findUserByUsername(String username) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findUserByUsername(" + username + ")");
				User result = delegate.findUserByUsername(username);
				logger.trace("Executed findUserByUsername(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findUserByUsername(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findUserByUsername(" + username + ")");
				User result = delegate.findUserByUsername(username);
				logger.debug("Executed findUserByUsername(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findUserByUsername(" + username + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findUserByUsername(" + username + ")");
			return delegate.findUserByUsername(username);
		}
		else {
			return delegate.findUserByUsername(username);
		}
	}
	
	@Override
	public long countUsers(UsersFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling countUsers(" + filter + ")");
				long result = delegate.countUsers(filter);
				logger.trace("Executed countUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on countUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling countUsers(" + filter + ")");
				long result = delegate.countUsers(filter);
				logger.debug("Executed countUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on countUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling countUsers(" + filter + ")");
			return delegate.countUsers(filter);
		}
		else {
			return delegate.countUsers(filter);
		}
	}
	
	@Override
	public FilteredPage<User> findUsers(UsersFilter filter, Paging paging) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findUsers(" + filter + ", " + paging + ")");
				FilteredPage<User> result = delegate.findUsers(filter, paging);
				logger.trace("Executed findUsers(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findUsers(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findUsers(" + filter + ", " + paging + ")");
				FilteredPage<User> result = delegate.findUsers(filter, paging);
				logger.debug("Executed findUsers(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findUsers(" + filter + ", " + paging + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findUsers(" + filter + ", " + paging + ")");
			return delegate.findUsers(filter, paging);
		}
		else {
			return delegate.findUsers(filter, paging);
		}
	}
	
	@Override
	public boolean isValidPasswordFormat(String password) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling isValidPasswordFormat(" + password + ")");
				boolean result = delegate.isValidPasswordFormat(password);
				logger.trace("Executed isValidPasswordFormat(" + password + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + "returnType" + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on isValidPasswordFormat(" + password + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling isValidPasswordFormat(" + password + ")");
				boolean result = delegate.isValidPasswordFormat(password);
				logger.debug("Executed isValidPasswordFormat(" + password + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on isValidPasswordFormat(" + password + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling isValidPasswordFormat(" + password + ")");
			return delegate.isValidPasswordFormat(password);
		}
		else {
			return delegate.isValidPasswordFormat(password);
		}
	}
	
	@Override
	public FilteredPage<User> findUsers(UsersFilter filter) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findUsers(" + filter + ")");
				FilteredPage<User> result = delegate.findUsers(filter);
				logger.trace("Executed findUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findUsers(" + filter + ")");
				FilteredPage<User> result = delegate.findUsers(filter);
				logger.debug("Executed findUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findUsers(" + filter + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findUsers(" + filter + ")");
			return delegate.findUsers(filter);
		}
		else {
			return delegate.findUsers(filter);
		}
	}
	
	@Override
	public FilteredPage<User> findActiveUserForOrg(Identifier organizationId) {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling findActiveUserForOrg(" + organizationId + ")");
				FilteredPage<User> result = delegate.findActiveUserForOrg(organizationId);
				logger.trace("Executed findActiveUserForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on findActiveUserForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling findActiveUserForOrg(" + organizationId + ")");
				FilteredPage<User> result = delegate.findActiveUserForOrg(organizationId);
				logger.debug("Executed findActiveUserForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on findActiveUserForOrg(" + organizationId + ") in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling findActiveUserForOrg(" + organizationId + ")");
			return delegate.findActiveUserForOrg(organizationId);
		}
		else {
			return delegate.findActiveUserForOrg(organizationId);
		}
	}
	
	@Override
	public UsersFilter defaultUsersFilter() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultUsersFilter()");
				UsersFilter result = delegate.defaultUsersFilter();
				logger.trace("Executed defaultUsersFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultUsersFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultUsersFilter()");
				UsersFilter result = delegate.defaultUsersFilter();
				logger.debug("Executed defaultUsersFilter() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultUsersFilter() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultUsersFilter()");
			return delegate.defaultUsersFilter();
		}
		else {
			return delegate.defaultUsersFilter();
		}
	}
	
	@Override
	public Paging defaultUsersPaging() {
		if (logger.isTraceEnabled()) {
			long start = System.nanoTime();
			try {
				logger.trace("Calling defaultUsersPaging()");
				Paging result = delegate.defaultUsersPaging();
				logger.trace("Executed defaultUsersPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + result.getClass() + ": " + result + ").");
				return result;
			}
			catch (Exception e) {
				logger.trace("Exception on defaultUsersPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isDebugEnabled()) {
			long start = System.nanoTime();
			try {
				logger.debug("Calling defaultUsersPaging()");
				Paging result = delegate.defaultUsersPaging();
				logger.debug("Executed defaultUsersPaging() in " + Duration.ofNanos(System.nanoTime() - start) + ".");
				return result;
			}
			catch (Exception e) {
				logger.debug("Exception on defaultUsersPaging() in " + Duration.ofNanos(System.nanoTime() - start) + " (" + e.getClass() + ": " + e.getMessage() + ").");
				throw e;
			}
		}
		else if (logger.isInfoEnabled()) {
			logger.info("Calling defaultUsersPaging()");
			return delegate.defaultUsersPaging();
		}
		else {
			return delegate.defaultUsersPaging();
		}
	}
	
}
