package ca.magex.crm.hazelcast.service;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Province;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.resource.CrmLookupLoader;
import ca.magex.crm.resource.CrmRoleInitializer;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastInitializationService implements CrmInitializationService {

	private static final Logger LOG = LoggerFactory.getLogger(HazelcastInitializationService.class);
	
	public static String HZ_INIT_KEY = "init";
	
	@Autowired private HazelcastInstance hzInstance;	
	@Autowired private HazelcastPermissionService hzPermissionService;
	@Autowired private CrmLookupLoader lookupLoader;	
	@Autowired private HazelcastOrganizationService hzOrganizationService;
	@Autowired private HazelcastPersonService hzPersonService;
	@Autowired private HazelcastUserService hzUserService;
	@Autowired private HazelcastPasswordService hzPasswordService;
	@Autowired private PasswordEncoder passwordEncoder;
	
	Long initializedTimestamp = null;
	Long startedTimestamp = null;	

	@PostConstruct
	public void start() {
		Map<String, Object> initMap = hzInstance.getMap(HZ_INIT_KEY);
		if (initMap.containsKey("started")) {
			waitForStartup(initMap);
			return;
		}
		else {
			startedTimestamp = (Long) initMap.put("started", 0L); 
			if (startedTimestamp == null) {
				LOG.info("Loading Lookups");
				hzInstance.getList(HazelcastLookupService.HZ_STATUS_KEY).addAll(Arrays.asList(Status.values()));
				hzInstance.getList(HazelcastLookupService.HZ_COUNTRY_KEY).addAll(lookupLoader.loadLookup(Country.class, "Country.csv"));
				hzInstance.getList(HazelcastLookupService.HZ_LANGUAGE_KEY).addAll(lookupLoader.loadLookup(Language.class, "Language.csv"));
				hzInstance.getList(HazelcastLookupService.HZ_SALUTATION_KEY).addAll(lookupLoader.loadLookup(Salutation.class, "Salutation.csv"));
				hzInstance.getList(HazelcastLookupService.HZ_SECTOR_KEY).addAll(lookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv"));
				hzInstance.getList(HazelcastLookupService.HZ_UNIT_KEY).addAll(lookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv"));
				hzInstance.getList(HazelcastLookupService.HZ_CLASSIFICATION_KEY).addAll(lookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv"));
				// FIXME update to proper countries
				hzInstance.getMap(HazelcastLookupService.HZ_PROVINCES_KEY).put("CA", lookupLoader.loadLookup(new Country("CA", "CA", "CA"), Province.class, "CaProvince.csv"));
				hzInstance.getMap(HazelcastLookupService.HZ_PROVINCES_KEY).put("US", lookupLoader.loadLookup(new Country("US", "US", "US"), Province.class, "UsProvince.csv"));
				hzInstance.getMap(HazelcastLookupService.HZ_PROVINCES_KEY).put("MX", lookupLoader.loadLookup(new Country("MX", "MX", "MX"), Province.class, "MxProvince.csv"));
				initMap.put("started", System.currentTimeMillis());
				LOG.info("Hazelcast CRM Started on: " + new Date((Long) initMap.get("started")));
				return;
			}
			else if (startedTimestamp.equals(Long.valueOf(0))) {
				/* this means another node is starting, so just wait for it */
				waitForStartup(initMap);
				return;
			}
			else {
				/* another node has already started */
				LOG.info("Hazelcast CRM Previously Started on: " + new Date(startedTimestamp));
				return;
			}
		}
	}
	
	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		Map<String, Object> initMap = hzInstance.getMap(HZ_INIT_KEY);
		if (initMap.get("initialized") != null) {
			waitForInitialization(initMap);
			return hzUserService.findUserByUsername(username);
		}
		else {
			initializedTimestamp = (Long) initMap.put("initialized", 0L);
			if (initializedTimestamp == null) {
				LOG.info("Initializing Permissions");
				CrmRoleInitializer.initialize(hzPermissionService);	
				LOG.info("Initializing Organizations");
				Identifier organizationId = hzOrganizationService.createOrganization(organization, List.of("SYS", "CRM")).getOrganizationId();
				Identifier personId = hzPersonService.createPerson(organizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
				User initialUser = hzUserService.createUser(personId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN"));			
				hzPasswordService.generateTemporaryPassword(username);
				hzPasswordService.updatePassword(username, passwordEncoder.encode(password));
				initMap.put("initialized", System.currentTimeMillis());
				return initialUser; 
			}
			else if (initializedTimestamp.equals(Long.valueOf(0))) {
				/* this means another node is starting, so just wait for it */
				waitForInitialization(initMap);
				return hzUserService.findUserByUsername(username);
			}
			else {
				/* another node has already started */
				LOG.info("Hazelcast CRM Previously Started on: " + new Date(startedTimestamp));
				return hzUserService.findUserByUsername(username);
			}
		}		
	}
	
	@Override
	public boolean isInitialized() {
		Map<String, Object> initMap = hzInstance.getMap(HZ_INIT_KEY);
		if (initMap.containsKey("initialized")) {
			waitForInitialization(initMap);
			return true;
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean reset() {
		Map<String, Object> initMap = hzInstance.getMap(HZ_INIT_KEY);
		initMap.remove("initialized");
		hzInstance.getMap(HazelcastLocationService.HZ_LOCATION_KEY).clear();
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
		hzInstance.getMap(HazelcastPasswordService.HZ_PASSWORDS_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_GROUP_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_ROLE_KEY).clear();
		hzInstance.getMap(HazelcastPersonService.HZ_PERSON_KEY).clear();
		hzInstance.getMap(HazelcastUserService.HZ_USER_KEY).clear();		
		initializedTimestamp = null;
		return true;
	}
	
	@Override
	public void dump(OutputStream os) {
		List<String> keys = List.of(
			HazelcastLocationService.HZ_LOCATION_KEY,
			HazelcastOrganizationService.HZ_ORGANIZATION_KEY,
			HazelcastPasswordService.HZ_PASSWORDS_KEY,
			HazelcastPermissionService.HZ_GROUP_KEY,
			HazelcastPermissionService.HZ_ROLE_KEY,
			HazelcastPersonService.HZ_PERSON_KEY,
			HazelcastUserService.HZ_USER_KEY
		);
		
		for (String map : keys) {
			Map<Identifier, Object> data = hzInstance.getMap(map);
			data.keySet()
				.stream()
				.sorted((x, y) -> x.toString().compareTo(y.toString()))
				.forEach(key -> {
					try {
						os.write(new String(key + " => " + data.get(key) + "\n").getBytes());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
		}
	}
	
	/**
	 * helper method to wait for startup to occur
	 * @param initMap
	 */
	private void waitForStartup(Map<String, Object> initMap) {	
		startedTimestamp = (Long) initMap.get("started");
		for (int i=0; i<10; i++) {				
			if (startedTimestamp == 0L) {
				LOG.info("Waiting for hazelcast to be started...");
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(3));
				}
				catch(InterruptedException ie) {
					if (Thread.currentThread().isInterrupted()) {
						continue;
					}
				}
				startedTimestamp = (Long) initMap.get("started");
			}
			else {
				break;
			}
		}
		LOG.info("Hazelcast CRM Previously Started on: " + new Date(startedTimestamp));
		return;
	}
	
	/**
	 * helper method to wait for startup to occur
	 * @param initMap
	 */
	private void waitForInitialization(Map<String, Object> initMap) {	
		initializedTimestamp = (Long) initMap.get("initialized");
		for (int i=0; i<10; i++) {				
			if (initializedTimestamp == 0L) {
				LOG.info("Waiting for hazelcast to be initialized...");
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
		LOG.info("Hazelcast CRM Previously Initialized on: " + new Date(initializedTimestamp));
		return;
	}
}
