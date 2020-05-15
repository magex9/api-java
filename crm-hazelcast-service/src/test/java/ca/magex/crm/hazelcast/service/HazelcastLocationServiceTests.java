package ca.magex.crm.hazelcast.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.test.AbstractLocationServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLocationServiceTests extends AbstractLocationServiceTests {

	@Autowired private CrmLocationService hzLocationService;
	@Autowired private CrmOrganizationService hzOrganizationService;
	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public CrmLocationService getLocationService() {
		return hzLocationService;
	}
	
	@Override
	public CrmOrganizationService getOrganizationService() {
		return hzOrganizationService;
	}
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
		hzInstance.getMap(HazelcastLocationService.HZ_LOCATION_KEY).clear();
	}
}
