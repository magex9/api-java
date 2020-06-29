package ca.magex.crm.hazelcast.config;

import javax.transaction.TransactionManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.authentication.CrmAuthenticationService;
import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.authentication.basic.BasicAuthenticationService;
import ca.magex.crm.api.authentication.basic.BasicPasswordService;
import ca.magex.crm.api.config.CrmConfigurer;
import ca.magex.crm.api.dictionary.basic.BasicDictionary;
import ca.magex.crm.api.policies.basic.BasicPolicies;
import ca.magex.crm.api.repositories.CrmPasswordRepository;
import ca.magex.crm.api.services.basic.BasicServices;
import ca.magex.crm.hazelcast.repository.HazelcastPasswordRepository;
import ca.magex.crm.hazelcast.repository.HazelcastRepositories;
import ca.magex.crm.hazelcast.xa.XATransactionAwareHazelcastInstance;

@Configuration
@ComponentScan(basePackages = {
		"ca.magex.crm.hazelcast"
})
public class HazelcastTestConfig implements CrmConfigurer {

	@Autowired private XATransactionAwareHazelcastInstance hzInstance;

	// build up a Basic Services Using the Hazelcast Repositories
	@Bean
	@Override
	public Crm crm() {
		BasicServices services = new BasicServices(
				new HazelcastRepositories(hzInstance),
				passwords(),
				new BasicDictionary());
		return new Crm(services, new BasicPolicies(services));
	}

	@Bean
	public CrmPasswordRepository passwordRepo() {
		return new HazelcastPasswordRepository(hzInstance);
	}

	@Bean
	public CrmAuthenticationService authenticationService() {
		return new BasicAuthenticationService(crm(), passwordRepo());
	}
	
	@Bean
	public CrmPasswordService passwords() {
		return new BasicPasswordService(passwordRepo());
	}

	@Bean
	@Primary
	public PlatformTransactionManager jtaTransactionManager() {
		JtaTransactionManager tm = new JtaTransactionManager();
		tm.setTransactionManager(transactionManager());
		return tm;
	}

	@Bean
	public TransactionManager transactionManager() {
		TransactionManager tm = new TransactionManagerImple();
		return tm;
	}
}