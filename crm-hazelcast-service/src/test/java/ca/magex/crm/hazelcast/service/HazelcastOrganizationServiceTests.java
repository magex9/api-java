package ca.magex.crm.hazelcast.service;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.hazelcast.config.HazelcastTestConfig;
import ca.magex.crm.test.AbstractOrganizationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HazelcastTestConfig.class })
@ActiveProfiles(profiles = { CrmProfiles.CRM_DATASTORE_DECENTRALIZED, CrmProfiles.CRM_NO_AUTH })
@EnableTransactionManagement
public class HazelcastOrganizationServiceTests extends AbstractOrganizationServiceTests {

	@Autowired
	public void setCrm(Crm crm) {
		this.crm = crm;
	}
	
	@Override
	@Transactional
	public void testCannotUpdateDisabledGroup() throws Exception {
		super.testCannotUpdateDisabledGroup();
	}
	
	@Override
	@Transactional
	public void testCannotUpdateDisabledMainContact() throws Exception {
		super.testCannotUpdateDisabledMainContact();
	}
	
	@Override
	@Transactional
	public void testCannotUpdateDisabledMainLocation() throws Exception {
		super.testCannotUpdateDisabledMainLocation();
	}
	
	@Override
	@Transactional
	public void testCreateOrgWithMissingGroup() throws Exception {
		super.testCreateOrgWithMissingGroup();
	}
	
	@Override
	@Transactional
	public void testCreatingOrgsWithInvalidStatuses() throws Exception {
		super.testCreatingOrgsWithInvalidStatuses();
	}
	
	@Override
	@Transactional
	public void testCreatingOrgWithMainContactFromOtherOrg() throws Exception {
		super.testCreatingOrgWithMainContactFromOtherOrg();
	}
	
	@Override
	@Transactional
	public void testCreatingOrgWithMainLocationFromOtherOrg() throws Exception {
		super.testCreatingOrgWithMainLocationFromOtherOrg();
	}
	
	@Override
	@Transactional
	public void testFindByIdentifierOtherType() throws Exception {
		super.testFindByIdentifierOtherType();
	}
	
	@Override
	@Transactional
	public void testInvalidOrgId() {
		super.testInvalidOrgId();
	}
	
	@Override
	@Transactional
	public void testOrganizations() {
		super.testOrganizations();
	}
	
	@Override
	@Transactional
	public void testOrgWithLongName() throws Exception {
		super.testOrgWithLongName();
	}
	
	@Override
	@Transactional
	public void testOrgWithNoGroup() throws Exception {
		super.testOrgWithNoGroup();
	}
	
	@Override
	@Transactional
	public void testOrgWithNoName() throws Exception {
		super.testOrgWithNoName();
	}
	
	@Override
	@Transactional
	public void testWrongIdentifiers() throws Exception {
		super.testWrongIdentifiers();
	}
}
