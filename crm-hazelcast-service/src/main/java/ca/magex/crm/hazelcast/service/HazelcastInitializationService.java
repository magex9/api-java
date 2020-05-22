package ca.magex.crm.hazelcast.service;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.common.Communication;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.resource.CrmLookupLoader;
import ca.magex.crm.resource.CrmRoleInitializer;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastInitializationService implements CrmInitializationService {

	private static final Logger LOG = LoggerFactory.getLogger(HazelcastInitializationService.class);
	
	@Autowired private HazelcastInstance hzInstance;
	@Autowired private CrmLookupLoader lookupLoader;
	@Autowired private CrmPermissionService hzPermissionService;
	@Autowired private CrmOrganizationService hzOrganizationService;
	@Autowired private CrmPersonService hzPersonService;
	@Autowired private CrmUserService hzUserService;
	@Autowired private CrmPasswordService hzPasswordService;
	@Autowired private PasswordEncoder passwordEncoder;
	
	private boolean shouldInitialize = false;
	
	@Override
	public boolean isInitialized() {
		Map<String, Object> initMap = hzInstance.getMap("init");
		if (initMap.containsKey("timestamp")) {
			Long initTimeStamp = (Long) initMap.get("timestamp");
			for (int i=0; i<10; i++) {				
				if (initTimeStamp == 0L) {
					LOG.info("Waiting for hazelcast to be initialized...");
					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(3));
					}
					catch(InterruptedException ie) {
						if (Thread.currentThread().isInterrupted()) {
							continue;
						}
					}
					initTimeStamp = (Long) initMap.get("timestamp");
				}
				else {
					break;
				}
			}
			LOG.info("Hazelcast CRM Previously Initialized on: " + new Date(initTimeStamp));
			return true;
		}
		initMap.put("timestamp", 0L);
		this.shouldInitialize = true;
		return false;
	}
	
	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		if (shouldInitialize) {
			LOG.info("Initializing Lookups");
			hzInstance.getList("statuses").addAll(Arrays.asList(Status.values()));
			hzInstance.getList("countries").addAll(lookupLoader.loadLookup(Country.class, "Country.csv"));
			hzInstance.getList("languages").addAll(lookupLoader.loadLookup(Language.class, "Language.csv"));
			hzInstance.getList("salutations").addAll(lookupLoader.loadLookup(Salutation.class, "Salutation.csv"));
			hzInstance.getList("sectors").addAll(lookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv"));
			hzInstance.getList("units").addAll(lookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv"));
			hzInstance.getList("classifications").addAll(lookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv"));
			
			LOG.info("Initializing Permissions");
			CrmRoleInitializer.initialize(hzPermissionService);			
			
			Identifier organizationId = hzOrganizationService.createOrganization(organization, List.of("SYS", "CRM")).getOrganizationId();
			Identifier personId = hzPersonService.createPerson(organizationId, name, null, new Communication(null, null, email, null, null), null).getPersonId();
			User initialUser = hzUserService.createUser(personId, username, List.of("SYS_ADMIN", "SYS_ACTUATOR", "SYS_ACCESS", "CRM_ADMIN"));			
			hzPasswordService.generateTemporaryPassword(username);
			hzPasswordService.updatePassword(username, passwordEncoder.encode(password));
			
			shouldInitialize = false;
			return initialUser;
		}
		return hzUserService.findUserByUsername(username);
	}
	
	@Override
	public boolean reset() {
		hzInstance.getList("statuses").clear();
		hzInstance.getList("countries").clear();
		hzInstance.getList("languages").clear();
		hzInstance.getList("salutations").clear();
		hzInstance.getList("sectors").clear();
		hzInstance.getList("units").clear();
		hzInstance.getList("classifications").clear();
		
		hzInstance.getMap(HazelcastLocationService.HZ_LOCATION_KEY).clear();
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
		hzInstance.getMap(HazelcastPasswordService.HZ_PASSWORDS_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_GROUP_KEY).clear();
		hzInstance.getMap(HazelcastPermissionService.HZ_ROLE_KEY).clear();
		hzInstance.getMap(HazelcastPersonService.HZ_PERSON_KEY).clear();
		hzInstance.getMap(HazelcastUserService.HZ_USER_KEY).clear();
		
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
}
