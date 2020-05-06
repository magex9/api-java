package ca.magex.crm.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Lazy;

import com.netflix.discovery.EurekaClient;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",
		"ca.magex.crm.resource",
		"ca.magex.crm.amnesia",
		"ca.magex.crm.hazelcast",
		"ca.magex.crm.graphql",
		"ca.magex.crm.spring.security",
		"ca.magex.crm.policy"
})
public class CrmGraphqlEurekaClientApplication {

	@Autowired @Lazy private EurekaClient eurekaClient;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmGraphqlEurekaClientApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}