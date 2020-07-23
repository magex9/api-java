package ca.magex.crm.mongodb.repository;

import java.awt.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.magex.crm.mongodb.config.MongoTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoTestConfig.class })
@EnableTransactionManagement
public class MongoOptionRepositoryTests {

	@Autowired MongoTemplate mongo;
	
	@Test
	public void testOptionsRepository() {

		
		mongo.insert(new Point(20, 20));
	}
}
