package ca.magex.crm.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.magex.crm.feign.controller.CrmGraphQLClient;

@SpringBootApplication
@EnableFeignClients
@Controller
public class FeignClientApplication {

	@Autowired CrmGraphQLClient graphQlClient;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(FeignClientApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
	
	@RequestMapping("/get-orgs")
	public String showOrgs(Model model) {
		model.addAttribute("orgs", graphQlClient.Orgs());
		return "orgs-view";
	}
	
}
