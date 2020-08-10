package ca.magex.crm.restful.client;

import javax.transaction.TransactionManager;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;

import ca.magex.crm.api.authentication.CrmAuthenticationService;

@Configuration
@ComponentScan(basePackages = {
	"ca.magex.crm.springboot",
})
public class RestfulClientTestConfig {

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

	@Bean
	public CrmAuthenticationService authService() {
		return Mockito.mock(CrmAuthenticationService.class);
	}
}
