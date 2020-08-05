package ca.magex.crm.mongodb.config;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
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

@PropertySource("classpath:azure-mongodb.properties")
public class MongoTestConfig {

	@Value("${mongo.url}") private String url;
	@Value("${mongo.username}") private String username;
	@Value("${mongo.password}") private String password;

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb+srv://" + username + ":" + password + "@" + url);
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
