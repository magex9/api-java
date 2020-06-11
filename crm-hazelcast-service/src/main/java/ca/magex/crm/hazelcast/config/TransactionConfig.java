package ca.magex.crm.hazelcast.config;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionManagerImple;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
	
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