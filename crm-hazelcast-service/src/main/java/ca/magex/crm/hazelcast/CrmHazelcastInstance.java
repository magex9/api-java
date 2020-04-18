package ca.magex.crm.hazelcast;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class CrmHazelcastInstance {
	
	@Value("classpath:hazelcast-crm.xml")
	private Resource configResource;
	
	private HazelcastInstance hzInstance;
	
	@PostConstruct
	public void initialize() throws IOException {	
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
		hzInstance.shutdown();
	}
}