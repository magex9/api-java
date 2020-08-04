package ca.magex.crm.mongodb.repository;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.magex.crm.mongodb.config.MongoTestConfig;
import ca.magex.crm.test.AbstractConfigurationServiceTests;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
public class MongoBasicConfigurationServiceTests extends AbstractConfigurationServiceTests {

	@Override
	public void testDataDump() throws Exception {
	}
	
	@Override
	public void testDataDumpToInvalidFile() throws Exception {
	}
	
	
}
