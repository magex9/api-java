package ca.magex.crm.auth.config;

import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.CrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.policies.CrmPolicies;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.repositories.CrmRepositories;
import ca.magex.crm.api.services.basic.BasicConfigurationService;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.api.store.basic.BasicPasswordStore;
import ca.magex.crm.api.store.basic.BasicStore;
import ca.magex.crm.mongodb.event.MongoChangeListener;
import ca.magex.crm.mongodb.repository.MongoPasswordRepository;
import ca.magex.crm.mongodb.repository.MongoRepositories;

@Configuration
@Profile(CrmProfiles.MONGO)
@Description("Configures the CRM used by the Auth Server with a Mongo repository")
@PropertySource("mongodb-config.properties")
public class MongoCrmConfig implements CrmConfigurer {

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
	public ThreadFactory threadFactory() {
		CustomizableThreadFactory tf = new CustomizableThreadFactory("mongo");
		tf.setDaemon(true);
		return tf;
	}
	
	@Bean
	public MongoChangeListener mongoCL() {
		return new MongoChangeListener(mongoCrm(), dbName, threadFactory());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public BasicStore store() {
		return new BasicStore();
	}

	@Bean
	public BasicPasswordStore passwordStore() {
		return new BasicPasswordStore();
	}

	@Bean
	public CrmUpdateNotifier notifier() {
		return new CrmUpdateNotifier();
	}

	@Bean
	public CrmRepositories repos() {
		return new MongoRepositories(mongoCrm(), notifier(), dbName);
	}

	@Bean
	public CrmPasswordRepository passwordRepo() {
		return new MongoPasswordRepository(mongoCrm(), notifier(), dbName);
	}

	@Bean(autowireCandidate = false) // ensure this bean doesn't conflict with our CRM for autowiring
	public BasicServices services() {
		return new BasicServices(repos(), passwords());
	}

	@Bean
	public CrmPolicies policies() {
		return new BasicPolicies(services());
	}

	@Bean
	public CrmPasswordService passwords() {
		return new BasicPasswordService(repos(), passwordRepo(), passwordEncoder());
	}
	
	@Bean 
	public BasicConfigurationService config() {
		return new BasicConfigurationService(repos(), passwords());
	}

	@Bean
	@Override
	public Crm crm() {
		return new Crm(services(), policies());
	}

	@PostConstruct
	public void initialize() {
		if (config().isInitialized()) {
			LoggerFactory.getLogger(MongoCrmConfig.class).info("CRM Previously Initialized for " + dbName);
		}
		else {
			LoggerFactory.getLogger(MongoCrmConfig.class).info("Initializing CRM for " + dbName);
			config().initializeSystem("Magex", new PersonName(null, "System", null, "Admin"), "crm-admin@magex.ca", "crmadmin", "crmadmin");
		}
	}
}