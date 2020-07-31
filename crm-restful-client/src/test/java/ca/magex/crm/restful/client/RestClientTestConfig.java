package ca.magex.crm.restful.client;

import javax.transaction.TransactionManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;

@Configuration
@ComponentScan(basePackages = {
	"ca.magex.crm.springboot",
})
public class RestClientTestConfig {

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
