package ca.magex.crm.hazelcast.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.test.AbstractLookupServiceTests;
import ca.magex.crm.test.CrmAsserts;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastLookupServiceTests extends AbstractLookupServiceTests {

	@Autowired private HazelcastInitializationService hzInitializationService;
	@Autowired private HazelcastLookupService hzLookupService;
	
	@Override
	public CrmLookupService getLookupService() {
		return hzLookupService;
	}
	
	@Override
	public CrmInitializationService getInitializationService() {
		return hzInitializationService;
	}
	
	@Before
	public void loadResource() {
		hzInitializationService.initializeSystem("JUnit", CrmAsserts.PERSON_NAME, "junit@junit.com", "admin", "admin");
	}

}