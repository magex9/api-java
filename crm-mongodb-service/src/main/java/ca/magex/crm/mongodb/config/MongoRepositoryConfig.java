package ca.magex.crm.mongodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

//@PropertySource("classpath:azure-mongodb.properties")
public class MongoRepositoryConfig {

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
}
