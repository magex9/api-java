package ca.magex.crm.hazelcast.repository;

import org.apache.commons.lang3.SerializationUtils;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.authentication.CrmPasswordDetails;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.store.CrmPasswordStore;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Password Repository that uses the Hazelcast in memory data grid
 * for persisting instances across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastPasswordRepository implements CrmPasswordRepository {

	private XATransactionAwareHazelcastInstance hzInstance;	
	
	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastPasswordRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}
	
	@Override
	public String generateTemporaryPassword() {
		return CrmPasswordStore.generatePassword();
	}

	@Override
	public CrmPasswordDetails savePasswordDetails(CrmPasswordDetails passwordDetails) {
		TransactionalMap<String, CrmPasswordDetails> passwords = hzInstance.getPasswordsMap();
		passwords.put(passwordDetails.getUsername(), SerializationUtils.clone(passwordDetails));
		return passwordDetails;
	}

	@Override
	public CrmPasswordDetails findPasswordDetails(String username) {
		TransactionalMap<String, CrmPasswordDetails> passwords = hzInstance.getPasswordsMap();
		return passwords.get(username);
	}
}
