package ca.magex.crm.mongodb.repository;

import java.awt.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mongodb.client.MongoClient;

import ca.magex.crm.mongodb.config.MongoTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
@EnableTransactionManagement
public class MongoOptionRepositoryTests {

	@Autowired MongoClient mongoClient;
	
	@Test
	public void testOptionsRepository() {
		
		MongoOperations mongoOps = new MongoTemplate(mongoClient, "crm");
		
		mongoOps.insert(new Point(20, 20));
	}
}
