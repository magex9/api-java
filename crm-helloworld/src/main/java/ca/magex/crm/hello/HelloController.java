package ca.magex.crm.hello;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class HelloController {

	Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@RequestMapping("/")
	public String index() {
		logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message");
        logger.warn("A WARN Message");
        logger.error("An ERROR Message");
		return "Greetings from Spring Boot!";
	}

}
