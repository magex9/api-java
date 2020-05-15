package ca.magex.crm.hazelcast.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.test.AbstractPersonServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPersonServiceTests extends AbstractPersonServiceTests {

	@Autowired private CrmPersonService hzPersonService;
	@Autowired private CrmOrganizationService hzOrganizationService;
	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public CrmOrganizationService getOrganizationService() {
		return hzOrganizationService;
	}
	
	@Override
	public CrmPersonService getPersonService() {
		return hzPersonService;
	}	
	
	@Before
	public void reset() {
		hzInstance.getMap(HazelcastOrganizationService.HZ_ORGANIZATION_KEY).clear();
		hzInstance.getMap(HazelcastPersonService.HZ_PERSON_KEY).clear();
	}
}