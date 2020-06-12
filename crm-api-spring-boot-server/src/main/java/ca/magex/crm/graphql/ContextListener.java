package ca.magex.crm.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.services.CrmInitializationService;

@Component
public class ContextListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ContextListener.class);

	@Autowired private CrmInitializationService initializationService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOG.info("Initializing Crm Data");
		initializationService.initializeSystem(
				"Johnnuy", 
				new PersonName("3", "Jonny", "", "Trafford"), 
				"jonny@johnnuy.org", 
				"johnnuy", 
				"admin");
	}	
}
