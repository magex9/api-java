package ca.magex.crm.hazelcast;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;

@Configuration
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class CrmHazelcastInstance {
	
	private static final Logger LOG = LoggerFactory.getLogger(CrmHazelcastInstance.class);
	
	@Value("classpath:hazelcast-crm.xml")
	private Resource configResource;
	
	private HazelcastInstance hzInstance;
	
	@PostConstruct
	public void initialize() throws IOException {	
		LOG.info("Starting hazelcast instance");
		try (InputStream configInputStream = configResource.getInputStream()) {
			hzInstance = Hazelcast.newHazelcastInstance(new XmlConfigBuilder(configInputStream).build());
		}
	}
	
	@Bean
	public HazelcastInstance hzInstance() {
		return hzInstance;
	}
	
	@PreDestroy
	public void shutdown() {
		LOG.info("Shutting down hazelcast instance");
		hzInstance.shutdown();
	}
}