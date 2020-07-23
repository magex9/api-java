package ca.magex.crm.mongodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@ComponentScan(basePackages = {
		"ca.magex.crm.mongodb"
})
@PropertySource("classpath:azure-mongodb.properties")
public class MongoTestConfig {

	@Value("${mongo.url}") private String url;
	@Value("${mongo.username}") private String username;
	@Value("${mongo.password}") private String password;

	@Bean
	public MongoClient mongoClient() {
		return MongoClients.create("mongodb+srv://" + username + ":" + password + "@" + url);
	}
}
