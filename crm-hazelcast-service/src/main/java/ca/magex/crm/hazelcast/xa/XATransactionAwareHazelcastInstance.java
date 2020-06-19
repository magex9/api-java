package ca.magex.crm.hazelcast.xa;

import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.transaction.HazelcastXAResource;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.PasswordDetails;
import ca.magex.crm.api.crm.LocationDetails;
import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.PersonDetails;
import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.repositories.CrmStore;
import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.hazelcast.service.HazelcastLocationService;
import ca.magex.crm.hazelcast.service.HazelcastOrganizationService;
import ca.magex.crm.hazelcast.service.HazelcastPasswordService;
import ca.magex.crm.hazelcast.service.HazelcastPermissionService;
import ca.magex.crm.hazelcast.service.HazelcastPersonService;
import ca.magex.crm.hazelcast.service.HazelcastUserService;

@Component
@Profile(CrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class XATransactionAwareHazelcastInstance {

	private static final Logger LOG = LoggerFactory.getLogger(XATransactionAwareHazelcastInstance.class);

	private HazelcastInstance hzInstance;
	private TransactionManager tm;

	private ThreadLocal<Transaction> txTracker = new ThreadLocal<Transaction>();

	public XATransactionAwareHazelcastInstance(HazelcastInstance hzInstance, TransactionManager tm) {
		this.hzInstance = hzInstance;
		this.tm = tm;
	}

	public TransactionalMap<Identifier, OrganizationDetails> getOrganizationsMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving organizations map", e);
		}
	}

	public TransactionalMap<Identifier, LocationDetails> getLocationsMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastLocationService.HZ_LOCATION_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving locations map", e);
		}
	}

	public TransactionalMap<Identifier, PersonDetails> getPersonsMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastPersonService.HZ_PERSON_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving persons map", e);
		}
	}

	public TransactionalMap<Identifier, User> getUsersMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastUserService.HZ_USER_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving users map", e);
		}
	}

	public TransactionalMap<Identifier, Group> getGroupsMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastPermissionService.HZ_GROUP_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving groups map", e);
		}
	}

	public TransactionalMap<Identifier, Role> getRolesMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastPermissionService.HZ_ROLE_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving groups map", e);
		}
	}

	public TransactionalMap<String, PasswordDetails> getPasswordsMap() {
		try {
			HazelcastXAResource xaRes = hzInstance.getXAResource();
			Transaction currentTrans = tm.getTransaction();
			enlistResource(xaRes, currentTrans);
			return xaRes.getTransactionContext().getMap(HazelcastPasswordService.HZ_PASSWORDS_KEY);
		} catch (RollbackException | SystemException e) {
			throw new ApiException("Error retrieving passwords map", e);
		}
	}

	public FlakeIdGenerator getFlakeIdGenerator(String name) {
		return hzInstance.getFlakeIdGenerator(name);
	}

	private void enlistResource(HazelcastXAResource xaRes, Transaction currentTrans) throws SystemException, RollbackException {
		if (txTracker.get() == null) {
			LOG.debug("Enlisting Resource with current Transaction: " + currentTrans);
			currentTrans.enlistResource(xaRes);
			currentTrans.registerSynchronization(new Synchronization() {

				@Override
				public void beforeCompletion() {
					try {
						// Don't know why this is done, but the the Hazelcast documentation does it...
						txTracker.get().delistResource(xaRes, HazelcastXAResource.TMSUCCESS);
					} catch (SystemException se) {
						LOG.warn("Unable to delist HazelcastXAResource", se);
					}
				}

				@Override
				public void afterCompletion(int status) {
					txTracker.remove();
				}
			});
			txTracker.set(currentTrans);
		} else {
			LOG.debug("Skipping Resource Enlistment, already enlisted with " + txTracker.get());
		}
	}
}
