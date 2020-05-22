package ca.magex.crm.hazelcast.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmInitializationService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.test.AbstractOrganizationServiceTests;
import ca.magex.crm.test.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
@ActiveProfiles(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastOrganizationServiceTests extends AbstractOrganizationServiceTests {

	@Autowired private CrmInitializationService hzInitializationService;
	@Autowired private CrmLookupService hzLookupService;
	@Autowired private CrmPermissionService hzPermissionService;
	@Autowired private CrmOrganizationService hzOrganizationService;
	@Autowired private CrmLocationService hzLocationService;
	@Autowired private CrmPersonService hzPersonService;
	
	@Override
	public CrmInitializationService getInitializationService() {
		return hzInitializationService;
	}

	@Override
	public CrmLookupService getLookupService() {
		return hzLookupService;
	}
	
	@Override
	public CrmLocationService getLocationService() {
		return hzLocationService;
	}
	
	@Override
	public CrmOrganizationService getOrganizationService() {
		return hzOrganizationService;
	}
	
	@Override
	public CrmPermissionService getPermissionService() {
		return hzPermissionService;
	}
	
	@Override
	public CrmPersonService getPersonService() {
		return hzPersonService;
	}
	
}
