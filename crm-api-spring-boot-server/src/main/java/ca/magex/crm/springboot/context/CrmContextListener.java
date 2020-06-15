package ca.magex.crm.springboot.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.services.Crm;

//@Component
public class CrmContextListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CrmContextListener.class);

	@Autowired private Crm crm;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {		
		if (!crm.isInitialized()) {
			LOG.info("Initializing Crm Data");
			crm.initializeSystem(
					"Johnnuy", 
					new PersonName("3", "Jonny", "", "Trafford"), 
					"jonny@johnnuy.org", 
					"johnnuy", 
					"admin");
			LOG.info(crm.findOrganizationDetails(new OrganizationsFilter()).toString());
		}
		else {
			LOG.info("Crm Previously Initialized");
		}
	}	
}
