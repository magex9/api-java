package ca.magex.crm.mongodb.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.CrmServices;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.CrmPasswordStore;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.mongodb.repository.MongoPasswordRepository;
import ca.magex.crm.mongodb.repository.MongoRepositories;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoTestConfig {
	
	private MongodExecutable mongod = null;
	private int mongodbPort = -1; 
		
	@PostConstruct
	public void startEmbeddedMongo() {
		try {
			String ip = "localhost";		
			mongodbPort = Network.getFreeServerPort();
			IMongodConfig mongodConfig = new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(ip, mongodbPort, Network.localhostIsIPv6()))
				.build();
			
			MongodStarter starter = MongodStarter.getDefaultInstance();
			mongod = starter.prepare(mongodConfig);
			mongod.start();
		}
		catch(IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	@PreDestroy
	public void stopEmbeddedMongo() {
		if (mongod != null) {
			mongod.stop();
			mongod = null;
		}
	}

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb://localhost:" + mongodbPort);
	}
	
	@Bean
	public MongoDatabase mongoCrm() {
		return mongoClient().getDatabase("crm");
	}
	
	@Bean 
	public PlatformTransactionManager txManager() {
		return Mockito.mock(PlatformTransactionManager.class);
	}
	
	@Bean
	public CrmAuthenticationService auth() {
		return Mockito.mock(CrmAuthenticationService.class);
	}
	
	@Bean
	public CrmPasswordStore passwordStore() {
		return new BasicPasswordStore();
	}
	
	@Bean
	public CrmUpdateNotifier notifier() {
		return new CrmUpdateNotifier();
	}
	
	@Bean
	public CrmRepositories repos() {
		return new MongoRepositories(mongoCrm(), notifier(), "junit");
	}
	
	@Bean
	public CrmPasswordRepository passwordRepo() {
		return new MongoPasswordRepository(mongoCrm(), notifier(), "junit");
	}
	
	@Bean 
	public CrmServices services() {
		return new BasicServices(repos(), passwords());
	}

	@Bean
	public CrmPolicies policies() {
		return new BasicPolicies(services());
	}
	
	@Bean
	public CrmPasswordService passwords() {
		return new BasicPasswordService(repos(), passwordRepo());
	}
	
	@Bean
	public Crm crm() {
		return new Crm(services(), policies());
	}
}
