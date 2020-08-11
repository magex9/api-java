package ca.magex.crm.springboot;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication(scanBasePackages = {
		"ca.magex.crm.api",
		"ca.magex.crm.graphql",
		"ca.magex.crm.restful",
		"ca.magex.crm.springboot",
		"ca.magex.crm.spring.security",
		"ca.magex.crm.transform",
})
public class CrmSpringBootApplication {

	public static void main(String[] args) throws Exception {
		if (!new File("config/application.properties").exists()) {
			LoggerFactory.getLogger(CrmSpringBootApplication.class).info("Copying application.template to application.properties");
			FileUtils.copyFile(new File("config/application.template"), new File("config/application.properties"));
		}
		SpringApplication app = new SpringApplication(CrmSpringBootApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}