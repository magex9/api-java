package ca.magex.crm.mongodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.event.CrmEventNotifier;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.mongodb.repository.MongoPasswordRepository;
import ca.magex.crm.mongodb.repository.MongoRepositories;

@Configuration
@Profile(CrmProfiles.MONGO)
@Description("Configures the CRM using the Mongo Repository")
public class MongoRepositoryConfig {

	@Value("${mongo.db.url}") private String url;
	@Value("${mongo.db.username}") private String username;
	@Value("${mongo.db.password}") private String password;
	@Value("${mongo.db.name}") private String dbName;
	
	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb+srv://" + username + ":" + password + "@" + url);
	}

	@Bean
	public MongoDatabase mongoCrm() {
		return mongoClient().getDatabase("crm");
	}
	
	@Bean
	public CrmEventNotifier notifier() {
		return new CrmEventNotifier();
	}
	
	@Bean
	public CrmRepositories repos() {
		return new MongoRepositories(mongoCrm(), notifier(), dbName);
	}

	@Bean
	public CrmPasswordRepository passwordRepo() {
		return new MongoPasswordRepository(mongoCrm(), notifier(), dbName);
	}
}
