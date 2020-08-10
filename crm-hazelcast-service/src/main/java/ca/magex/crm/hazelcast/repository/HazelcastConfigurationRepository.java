package ca.magex.crm.hazelcast.repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.hazelcast.core.TransactionalMap;

import ca.magex.crm.api.repositories.CrmConfigurationRepository;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

/**
 * An implementation of the Configuration Repository that uses the Hazelcast in memory data grid
 * for persisting configuration across multiple nodes
 * 
 * @author Jonny
 */
public class HazelcastConfigurationRepository implements CrmConfigurationRepository {

	private XATransactionAwareHazelcastInstance hzInstance;	
	
	/**
	 * Creates the new repository using the backing Transaction Aware Hazelcast Instance
	 * 
	 * @param hzInstance
	 */
	public HazelcastConfigurationRepository(XATransactionAwareHazelcastInstance hzInstance) {
		this.hzInstance = hzInstance;
	}
	
	@Override
	public boolean isInitialized() {
		TransactionalMap<String, Object> configurations = hzInstance.getConfigurationMap();
		if (configurations.containsKey("initialized")) {
			waitForInitializationToComplete(configurations);
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean prepareInitialize() {
		TransactionalMap<String, Object> configurations = hzInstance.getConfigurationMap();		
		Object previous = configurations.put("initialized", 0L);
		if (previous == null) {
			return true;
		}
		waitForInitializationToComplete(configurations);
		return false;
	}
	
	@Override
	public void setInitialized() {
		TransactionalMap<String, Object> configurations = hzInstance.getConfigurationMap();
		configurations.put("initialized", System.currentTimeMillis());
	}
	
	/**
	 * This method will ensure that this call will wait until the initialization is complete, if it has started
	 * @param initMap
	 */
	private void waitForInitializationToComplete(TransactionalMap<String, Object> initMap) {	
		Long initializedTimestamp = (Long) initMap.get("initialized");
		for (int i=0; i<10; i++) {				
			if (initializedTimestamp == 0L) {
				LoggerFactory.getLogger(getClass()).info("Waiting for hazelcast to be initialized...");
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(3));
				}
				catch(InterruptedException ie) {
					if (Thread.currentThread().isInterrupted()) {
						continue;
					}
				}
				initializedTimestamp = (Long) initMap.get("initialized");
			}
			else {
				break;
			}
		}
		LoggerFactory.getLogger(getClass()).info("Hazelcast CRM Previously Initialized on: " + new Date(initializedTimestamp));
		return;
	}
}